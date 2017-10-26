package br.com.query.action;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import br.com.query.componentes.TabResultado;
import br.com.query.componentes.TabelaResultado;
import br.com.query.componentes.TabelaResultado.MeuRenderer;
import br.com.query.util.Constantes;
import br.com.query.util.QueryExecutor;
import br.com.query.util.Recursos;
import br.com.query.view.Principal;
import br.com.query.view.View;

public class ExecutarSQLAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private static final String QUERY_DERBY = "${consulta} OFFSET ${inicio} ROWS FETCH NEXT ${quantidade} ROWS ONLY";
	private static final String CONSULTA = "\\$\\{consulta\\}";
	private static final String INICIO = "\\$\\{inicio\\}";
	private static final String QUANTIDADE = "\\$\\{quantidade\\}";

	private Principal principal;
	private int contadorDeLinhas;
	private String consultaAtual;

	public ExecutarSQLAction(Principal principal) {
		this.principal = principal;
		putValue("AcceleratorKey", KeyStroke.getKeyStroke(116, 0));
		putValue("ShortDescription", "Executar Query - F5");
	}

	public void actionPerformed(ActionEvent e) {
		doQuery();
	}

	public void doQuery() {

		principal.mudarCursor(true);
		principal.getTxtMensagem().setText("");
		principal.fecharTabs();

		for (String consulta : separarConsultas()) {
			executar(consulta);
		}

		principal.mudarCursor(false);
	}

	public int executar(String sql) {
		if (principal.isConectado) {
			boolean update = false;

			double inicio = System.currentTimeMillis();

			List<Pattern> patterns = new ArrayList<Pattern>();
			patterns.add(Pattern.compile("insert"));
			patterns.add(Pattern.compile("update"));

			for (Pattern pattern : patterns) {
				Matcher matcher = pattern.matcher(sql.toLowerCase());
				if (matcher.find()) {
					update = true;
				}
			}

			if (!update) {
				executarConsulta(sql, true, 0, false);
			} else {
				executarUpdate(sql);
			}

			double fim = System.currentTimeMillis();
			String informacoes = "Executado em: " + (fim - inicio) / 1000 + "s";
			informacoes += "            Linhas: " + contadorDeLinhas + "  ";
			principal.getStatusBar().setMensagem("Status   " + informacoes);
		} else {
			principal.getTxtMensagem().setText("Você precisa estar conectado para executar consultas");
		}
		return 0;
	}

	public int executarConsulta(String sql, boolean gerarGrid, int pagina, boolean paginacao) {
		try {
			consultaAtual = sql;
			Statement statement = principal.getConexao().createStatement();

			QueryExecutor executor = new QueryExecutor();
			this.contadorDeLinhas = 0;

			PreparedStatement stmt = principal.getConexao().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet contador = stmt.executeQuery();
			if (contador != null) {
				contador.last();
				this.contadorDeLinhas = contador.getRow();
			}

			ResultSet resultSet = executor.executeQuery(statement, sql);
			if (resultSet != null) {
				ResultSetMetaData metadata = resultSet.getMetaData();

				int numeroDeColunas = metadata.getColumnCount();
				Vector<String> colunas = new Vector<String>();
				for (int coluna = 0; coluna < numeroDeColunas; coluna++) {
					colunas.addElement(metadata.getColumnLabel(coluna + 1));
				}
				Vector<Vector<Object>> linhas = new Vector<Vector<Object>>();
				while (resultSet.next()) {

					Vector<Object> novaLinha = new Vector<Object>();
					String valor = null;
					for (int i = 1; i <= metadata.getColumnCount(); i++) {
						String tipoDaColuna;
						switch (metadata.getColumnType(i)) {
						case 2005:
							valor = resultSet.getString(i);
							if (resultSet.wasNull()) {
								valor = "";
							}
							novaLinha.addElement(valor);

							break;
						case -1:
							valor = resultSet.getString(i);
							if (resultSet.wasNull()) {
								valor = "";
							}
							novaLinha.addElement(valor);
							break;
						case 1111:
							tipoDaColuna = metadata.getColumnTypeName(i);
							novaLinha.addElement("[? " + tipoDaColuna + " ?]");

							break;
						case 2000:
							tipoDaColuna = metadata.getColumnTypeName(i);
							if (tipoDaColuna.toLowerCase().equals("st_geometry")) {
								byte[] arrayDeByte = resultSet.getBytes(i);
								novaLinha.addElement(arrayDeByte);
							} else {
								novaLinha.addElement("[? " + tipoDaColuna + " ?]");
							}
							break;
						default:
							novaLinha.addElement(resultSet.getObject(i));
						}
					}

					linhas.addElement(novaLinha);
				}
				if (gerarGrid) {
					criarTabelaDeResultado(linhas, colunas);
				} else {
					if (!paginacao) {
						for (Vector<Object> linha : linhas) {
							RSyntaxTextArea txtSQL = principal.getTxtSQL();
							String string = linha.toString().replaceAll("\\[", "").replaceAll("\\]", "");
							txtSQL.append(string);
						}
					} else {
						TabelaResultado novaTabelaResultado = new TabelaResultado(linhas, colunas);
						novaTabelaResultado.setAjustar(false);
						novaTabelaResultado.setDefaultRenderer(Object.class, new MeuRenderer());
						principal.setTabelaAtual(novaTabelaResultado);
						novaTabelaResultado.ajustarLarguraDaColunaAoConteudo();
					}
				}
				principal.getTxtMensagem().setText("Executada com sucesso");
			} else {
				principal.getTxtMensagem().setText("Nenhum resultado Retornado");
			}
		} catch (SQLException | InterruptedException se) {
			principal.getTxtMensagem().setText(" " + se.getMessage());
			principal.getAbaResultado().setSelectedIndex(0);
		}
		return 0;
	}

	public int executarUpdate(String sql) {
		try {
			consultaAtual = sql;
			PreparedStatement stmt = principal.getConexao().prepareStatement(sql);
			int executeUpdate = stmt.executeUpdate();
			if (executeUpdate == 1) {
				principal.getTxtMensagem().setText("Consulta realizada com sucesso!");
			}
			principal.getAbaResultado().setSelectedIndex(0);
		} catch (SQLException se) {
			principal.getTxtMensagem().setText(" " + se.getMessage());
			principal.getAbaResultado().setSelectedIndex(0);
		}
		return 0;
	}

	public void criarTabelaDeResultado(Vector<Vector<Object>> linhas, Vector<String> colunas) {
		final TabelaResultado resultado = new TabelaResultado(linhas, colunas);
		resultado.setAjustar(false);
		resultado.setDefaultRenderer(Object.class, new MeuRenderer());
		resultado.setContadorDeLinhas(contadorDeLinhas);
		resultado.addMouseListener(new MouseAdapter() {
			private View view;

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // check if a double click
					int row = resultado.rowAtPoint(e.getPoint());
					int col = resultado.columnAtPoint(e.getPoint());
					view = new View(new Point(e.getXOnScreen(), e.getYOnScreen()));
					view.setTexto(resultado.getValueAt(row, col).toString());
					view.setAlwaysOnTop(true);
					System.out.println(" x: " + e.getX() + " y: " + e.getY());
				}
			}
		});

		TabResultado tabResultado = new TabResultado(resultado, principal);
		String titulo = this.consultaAtual;
		tabResultado.setQuery(this.consultaAtual);

		JPanel tabAtual = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tabAtual.setOpaque(false);

		JLabel tituloDoTab = new JLabel(titulo);
		tituloDoTab.setPreferredSize(new Dimension(120, 20));

		JButton botaoDoTab = new JButton();
		botaoDoTab.setIcon(Recursos.carregarIcone(Constantes.FECHAR));
		botaoDoTab.setBorderPainted(false);
		botaoDoTab.setContentAreaFilled(false);
		botaoDoTab.setFocusPainted(false);
		botaoDoTab.setPreferredSize(new Dimension(20, 20));
		botaoDoTab.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int indice = principal.getAbaResultado().indexOfTabComponent(e.getComponent().getParent());
				principal.getAbaResultado().removeTabAt(indice);
			}
		});

		tabAtual.add(tituloDoTab);
		tabAtual.add(botaoDoTab);

		principal.getAbaResultado().addTab(titulo, tabResultado);
		principal.getAbaResultado().setTabComponentAt(principal.getAbaResultado().getTabCount() - 1, tabAtual);
		principal.getAbaResultado().setSelectedIndex(principal.getAbaResultado().getTabCount() - 1);
		principal.getAbaResultado().setToolTipTextAt(principal.getAbaResultado().getTabCount() - 1, titulo);

		resultado.ajustarLarguraDaColunaAoConteudo();
	}

	private List<String> separarConsultas() {
		String sql = "";
		List<String> consultas = new ArrayList<String>();

		if (principal.getTxtSQL().getSelectedText() != null) {
			sql = principal.getTxtSQL().getSelectedText();
		} else {
			sql = principal.getTxtSQL().getText();
		}

		String[] split = sql.split(";");
		for (String string : split) {
			consultas.add(string);
		}
		if (consultas.isEmpty()) {
			consultas.add(sql);
		}
		return consultas;
	}

	public void gerarScript(String sql) {
		executarConsulta(sql, false, 0, false);
	}

	@SuppressWarnings("unused")
	private String paginarConsulta(String sql, int inicio, int quantidade) {
		String query = QUERY_DERBY.replaceFirst(CONSULTA, sql);
		if (inicio > 0) {
			inicio = inicio * TabResultado.QUANTIDADE_DE_LINHAS;
		}
		query = query.replaceFirst(INICIO, String.valueOf(inicio));
		query = query.replaceFirst(QUANTIDADE, String.valueOf(quantidade));

		return query;
	}

}
