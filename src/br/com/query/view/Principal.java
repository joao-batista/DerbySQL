package br.com.query.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import br.com.query.action.ExecutarSQLAction;
import br.com.query.componentes.StatusBar;
import br.com.query.componentes.TabResultado;
import br.com.query.componentes.TabelaResultado;
import br.com.query.helper.NavegacaoHelper;
import br.com.query.model.CatalogoSchema;
import br.com.query.util.Constantes;
import br.com.query.util.Recursos;


public class Principal extends JFrame implements ClipboardOwner {

	private static final long serialVersionUID = 1L;
	// private Connection conexao;
	private AutoCompletion autoCompletion;
	private JPanel panel;
	private JMenuBar barraDoMenu;
	private JMenu menuArquivo;
	private JMenuItem itemMenu_01;
	private JMenu menuExibir;
	private JCheckBoxMenuItem itemMenu_02;
	private JCheckBoxMenuItem itemMenu_03;

	private JToolBar barraDeBotoes;
	private JButton btnConectar;
	private JButton btnDesconectar;
	private JButton btnNovo;
	private JButton btnAbrir;
	private JButton btnSalvar;
	private JButton btnConsultar;
	private JToggleButton btnAjustar;
	private JButton btnExibirOcultarNavegacao;
	private JButton btnExibirOcultarResultado;

	private JTabbedPane abaResultado;
	private JScrollPane scrollSQL;
	private JScrollPane scrollMensagem;
	private RSyntaxTextArea txtSQL;
	private JTextArea txtMensagem;
	private Navegacao navegacao;

	private JSplitPane splitVertical;
	private JSplitPane splitHorizontal;
	private StatusBar stsbrPronto;

	private JCheckBox checkLimite;
	private JTextField txtLimite;

	private JPanel panelSchema;
	private JComboBox<CatalogoSchema> comboSchema;

	public String url;
	public String driver;
	public String usuario;
	public String senha;
	public String databaseUrl;
	public boolean isConectado = false;
	boolean ajustarLargura = false;

	public String consultaAtual;

	protected Action executarSQLAction;
	private JPanel painelPesquisa;
	private JPanel painelComponentes;

	static {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.out.println("Failed loading L&F: ");
			System.out.println(ex);
		}
	}

	public Principal() {
		inicializarComponentes();
	}

	public void inicializarComponentes() {
		//
		// itemMenu_01
		//
		this.itemMenu_01 = new JMenuItem();
		this.itemMenu_01.setText("Nova Conexão");
		this.itemMenu_01.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				conectar();
			}
		});
		//
		// menuArquivo
		//
		this.menuArquivo = new JMenu();
		this.menuArquivo.setText("Arquivo");
		this.menuArquivo.add(this.itemMenu_01);
		//
		// itemMenu_02
		//
		this.itemMenu_02 = new JCheckBoxMenuItem("Left", Recursos.carregarIcone(Constantes.EXIBIR_OCULTAR_NAVEGACAO));
		this.itemMenu_02.setText("Barra Navegação");
		this.itemMenu_02.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exibirOcultarNavegacao();
			}
		});
		//
		// itemMenu_03
		//
		this.itemMenu_03 = new JCheckBoxMenuItem("Left", Recursos.carregarIcone(Constantes.EXIBIR_OCULTAR_RESULTADO));
		this.itemMenu_03.setText("Resultado");
		this.itemMenu_03.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exibirOcultarResultado();
			}
		});
		//
		// menuArquivo
		//
		this.menuExibir = new JMenu();
		this.menuExibir.setText("Exibir");
		this.menuExibir.add(this.itemMenu_02);
		this.menuExibir.add(this.itemMenu_03);
		//
		// barraDoMenu
		//
		this.barraDoMenu = new JMenuBar();
		this.barraDoMenu.add(this.menuArquivo);
		this.barraDoMenu.add(this.menuExibir);
		setJMenuBar(this.barraDoMenu);
		//
		// btnConectar
		//
		this.btnConectar = new JButton();
		this.btnConectar.setFocusPainted(false);
		this.btnConectar.setToolTipText("Conectar");
		this.btnConectar.setIcon(Recursos.carregarIcone(Constantes.CONECTAR));
		this.btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				conectar();
			}
		});
		//
		// btnDesconectar
		//
		this.btnDesconectar = new JButton();
		this.btnDesconectar.setEnabled(false);
		this.btnDesconectar.setToolTipText("Desconectar");
		this.btnDesconectar.setFocusPainted(false);
		this.btnDesconectar.setIcon(Recursos.carregarIcone(Constantes.DESCONECTAR));
		this.btnDesconectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desconectar();
			}
		});
		//
		// btnNovo
		//
		this.btnNovo = new JButton();
		this.btnNovo.setFocusPainted(false);
		this.btnNovo.setIcon(Recursos.carregarIcone(Constantes.NOVO));
		this.btnNovo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		//
		// btnAbrir
		//
		this.btnAbrir = new JButton();
		this.btnAbrir.setFocusPainted(false);
		this.btnAbrir.setToolTipText("Abrir script SQL");
		this.btnAbrir.setIcon(Recursos.carregarIcone(Constantes.ABRIR));
		this.btnAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		//
		// btnSalvar
		//
		this.btnSalvar = new JButton();
		this.btnSalvar.setFocusPainted(false);
		this.btnSalvar.setToolTipText("Salvar script SQL");
		this.btnSalvar.setIcon(Recursos.carregarIcone(Constantes.SALVAR));
		this.btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		//
		// btnConsultar
		//
		this.executarSQLAction = new ExecutarSQLAction(this);
		this.btnConsultar = new JButton(this.executarSQLAction);
		this.btnConsultar.setFocusPainted(false);
		this.btnConsultar.setToolTipText("Executar");
		this.btnConsultar.setIcon(Recursos.carregarIcone(Constantes.EXECUTAR));
		//
		// btnConsultar
		//
		this.btnAjustar = new JToggleButton();
		this.btnAjustar.setFocusPainted(false);
		this.btnAjustar.setToolTipText("Ajustar colunas");
		this.btnAjustar.setIcon(Recursos.carregarIcone(Constantes.AJUSTAR));
		this.btnAjustar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ajustarResultado();
			}
		});
		//
		// btnExibirOcultarNavegacao
		//
		this.btnExibirOcultarNavegacao = new JButton();
		this.btnExibirOcultarNavegacao.setFocusPainted(false);
		this.btnExibirOcultarNavegacao.setToolTipText("Exibir/Ocultar Navegação");
		this.btnExibirOcultarNavegacao.setIcon(Recursos.carregarIcone(Constantes.EXIBIR_OCULTAR_NAVEGACAO));
		this.btnExibirOcultarNavegacao.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exibirOcultarNavegacao();
			}
		});
		//
		// btnExibirOcultarResultado
		//
		this.btnExibirOcultarResultado = new JButton();
		this.btnExibirOcultarResultado.setFocusPainted(false);
		this.btnExibirOcultarResultado.setToolTipText("Exibir/Ocultar Resultado");
		this.btnExibirOcultarResultado.setIcon(Recursos.carregarIcone(Constantes.EXIBIR_OCULTAR_RESULTADO));
		this.btnExibirOcultarResultado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exibirOcultarResultado();
			}
		});
		//
		// barraDeBotoes
		//
		this.barraDeBotoes = new JToolBar();
		this.barraDeBotoes.add(btnConectar);
		this.barraDeBotoes.add(btnDesconectar);
		// this.barraDeBotoes.add(btnNovo);
		this.barraDeBotoes.add(btnAbrir);
		this.barraDeBotoes.add(btnSalvar);
		this.barraDeBotoes.add(btnConsultar);
		this.barraDeBotoes.add(btnAjustar);
//		this.barraDeBotoes.add(btnExibirOcultarNavegacao);
//		this.barraDeBotoes.add(btnExibirOcultarResultado);
		//
		//
		//
		this.txtMensagem = new JTextArea();
		this.txtMensagem.setBorder(null);
		this.txtMensagem.setMargin(new Insets(0, 0, 0, 0));
		this.txtMensagem.setEditable(false);
		//
		// scrollMensagem
		//
		this.scrollMensagem = new JScrollPane();
		this.scrollMensagem.setBorder(null);
		this.scrollMensagem.setViewportView(this.txtMensagem);
		//
		// abaResultado
		//
		this.abaResultado = new JTabbedPane();
		this.abaResultado.setAlignmentY(0.0f);
		this.abaResultado.setAlignmentX(0.0f);
		this.abaResultado.setBorder(null);
		this.abaResultado.add(scrollMensagem);
		this.abaResultado.setTitleAt(0, "Mensagens");
		//
		// txtSQL
		//
		this.txtSQL = new RSyntaxTextArea();
		Constantes.mudarEstilo(txtSQL);
		this.txtSQL.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		this.txtSQL.setCodeFoldingEnabled(true);
		this.autoCompletion = new AutoCompletion(Constantes.createCompletionProvider());
		this.autoCompletion.install(txtSQL);
		// this.autoCompletion.setShowDescWindow(true);
		//
		// checkLimite
		//
		this.checkLimite = new JCheckBox("               ");
		this.checkLimite.setSelected(true);
		this.checkLimite.setHorizontalTextPosition(SwingConstants.CENTER);
		this.checkLimite.setHorizontalAlignment(SwingConstants.CENTER);
		//
		// txtLimite
		//
		this.txtLimite = new JTextField("100");
		this.txtLimite.setMaximumSize(new Dimension(50, 20));
		this.txtLimite.setPreferredSize(new Dimension(50, 10));
		this.txtLimite.setColumns(10);
		//
		// scrollSQL
		//
		this.scrollSQL = new JScrollPane();
		this.scrollSQL.setViewportView(this.txtSQL);
		//
		// comboSchema
		//
		this.comboSchema = new JComboBox<CatalogoSchema>();
		this.comboSchema.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CatalogoSchema tipo = (CatalogoSchema) comboSchema.getSelectedItem();
				
				if (tipo != null) {
					exibirTabelas(tipo);
				}
			}
		});
		//
		// painelComponentes
		//
		this.painelComponentes = new JPanel();
		this.painelComponentes.setLayout(new BorderLayout(0, 0));
		//
		// panelSchema
		//
		this.panelSchema = new JPanel();
		this.panelSchema.setLayout(new BorderLayout(3, 3));
		this.panelSchema.add(comboSchema, BorderLayout.NORTH);
		this.panelSchema.add(painelComponentes, BorderLayout.CENTER);
		//
		// splitHorizontal
		//
		this.splitHorizontal = new JSplitPane();
		this.splitHorizontal.setBorder(null);
		this.splitHorizontal.setDividerLocation(getToolkit().getScreenSize().height / 3);
		this.splitHorizontal.setDividerSize(2);
		this.splitHorizontal.setOrientation(0);
		this.splitHorizontal.setLeftComponent(scrollSQL);
		this.splitHorizontal.setRightComponent(abaResultado);
		//
		// splitVertical
		//
		this.splitVertical = new JSplitPane();
		this.splitVertical.setBorder(null);
		this.splitVertical.setDividerLocation(250);
		this.splitVertical.setDividerSize(2);
		this.splitVertical.setLeftComponent(panelSchema);
		// painelPesquisa
		//
		this.painelPesquisa = new JPanel();
		this.painelPesquisa.setPreferredSize(new Dimension(15, 20));
		this.painelPesquisa.setLayout(new BorderLayout(0, 0));
		this.painelComponentes.add(painelPesquisa, BorderLayout.NORTH);
		//
		// navegacao
		//
		this.navegacao = new Navegacao(this);
		this.painelComponentes.add(navegacao, BorderLayout.CENTER);
		this.splitVertical.setRightComponent(splitHorizontal);
		//
		// statusBar
		//
		this.stsbrPronto = new StatusBar();
		this.stsbrPronto.setMensagem("Status   ");
//		this.stsbrPronto.setHorizontalAlignment(SwingConstants.RIGHT);

		getContentPane().add(stsbrPronto, java.awt.BorderLayout.SOUTH);
		getContentPane().add(barraDeBotoes, BorderLayout.NORTH);

		panel = new JPanel();
		barraDeBotoes.add(panel);
		getContentPane().add(splitVertical);

		setKeyMappings();

		this.setIconImage(Recursos.carregarIcone(Constantes.ICONE).getImage());
		this.setTitle("Derby SQL");
		this.setLocation(new Point(0, 0));
//		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setSize(new Dimension(1000, 700));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable transferable) {

	}

	public static void main(String[] args) {
		new Principal().setVisible(true);
	}

	public void conectar() {
		mudarCursor(true);

		Conexao parametros = new Conexao(this, true);

		parametros.setLocationRelativeTo(this);
		parametros.setVisible(true);
		if (parametros.conectado()) {
			this.isConectado = (Conexao.getConexao() != null);
			if (this.isConectado) {
				this.databaseUrl = parametros.getTxtURL();
				int indice = this.databaseUrl.indexOf("password");
				if (indice > 0) {
					this.databaseUrl = this.databaseUrl.substring(0, indice);
				}
				setTitle("Derby SQL - Conectado a " + this.databaseUrl);
				this.btnConectar.setEnabled(false);
				this.btnDesconectar.setEnabled(true);
				carregarSchemas();
				CatalogoSchema tipo = (CatalogoSchema) comboSchema.getSelectedItem();
				exibirTabelas(tipo);
			}
		}
		mudarCursor(false);
	}

	private void desconectar() {
		try {
			if (Conexao.getConexao() != null) {
				Conexao.getConexao().close();
				this.btnConectar.setEnabled(true);
				this.btnDesconectar.setEnabled(false);
				this.comboSchema.removeAllItems();
				this.txtSQL.setText("");
				this.txtMensagem.setText("");
				this.stsbrPronto.setMensagem("Status ");
				this.setTitle("Derby SQL");
				removerTabelas();
				fecharTabs();
			}
		} catch (SQLException se) {
		}
	}

	private void exibirOcultarNavegacao() {
		if (splitVertical.getLeftComponent() != null)
			splitVertical.setLeftComponent(null);
		else
			splitVertical.setLeftComponent(panelSchema);
		this.splitVertical.setDividerLocation(250);
	}

	private void exibirOcultarResultado() {
		if (splitHorizontal.getRightComponent() != null)
			splitHorizontal.setRightComponent(null);
		else
			splitHorizontal.setRightComponent(abaResultado);
		this.splitHorizontal.setDividerLocation(getToolkit().getScreenSize().height / 3);
	}

	public void exibirTabelas(final CatalogoSchema tipo) {
		mostrarTabelas(tipo);
	}

	public void mostrarTabelas(CatalogoSchema tipo) {
		mudarCursor(true);
		this.navegacao.setVisible(true);
		this.navegacao.carregarTabelas(tipo);
		mudarCursor(false);
	}

	public void removerTabelas() {
		mudarCursor(true);
		this.navegacao.setVisible(true);
		this.navegacao.liberarTabelas();
		mudarCursor(false);
	}

	public void mudarCursor(boolean espera) {
		Cursor cursor = espera ? new Cursor(3) : new Cursor(0);
		setCursor(cursor);
		this.navegacao.setCursor(cursor);
		this.txtSQL.setCursor(cursor);
	}

	public void ajustarResultado() {
		TabelaResultado tabelaResultado = (TabelaResultado) getTabelaAtual();
		if (tabelaResultado != null) {
			this.ajustarLargura = (!tabelaResultado.getAjustar());
			tabelaResultado.setAjustar(this.ajustarLargura);
			if (this.ajustarLargura) {
				tabelaResultado.setAutoResizeMode(4);
			} else {
				tabelaResultado.setAutoResizeMode(0);
			}
			doLayout();
		} else {
			this.btnAjustar.setSelected(this.ajustarLargura);
		}
	}

	public JTable getTabelaAtual() {
		// se o tab atual for o de mensagens
		if (this.abaResultado.getSelectedIndex() == 0) {
			return null;
		}
		// senão retorna a tabela do tab selecionado
		TabResultado tabResultado = (TabResultado) this.abaResultado.getSelectedComponent();
		Component tabelaResultado = null;
		if (tabResultado != null) {
			tabelaResultado = tabResultado.getTabelaResultado();
		}
		return (JTable) tabelaResultado;
	}
	
	public void setTabelaAtual(TabelaResultado novaTabelaResultado) {
		// substitui a tabela de resultado da aba atual
		TabResultado tabResultado = (TabResultado) this.abaResultado.getSelectedComponent();
		if (tabResultado != null) {
			tabResultado.setTabelaResultado(novaTabelaResultado);
		}
		tabResultado.repaint();
	}

	public void fecharTabs() {
		int tabCount = this.abaResultado.getTabCount() - 1;
		for (int i = tabCount; i > 0; i--) {
			this.abaResultado.removeTabAt(i);
		}
	}

	private void carregarSchemas() {
		ResultSet rsCatalogos = NavegacaoHelper.listarCatalogos();
		ResultSet rsSchemas = NavegacaoHelper.listarSchemas();
		try {
			if (rsCatalogos != null && rsCatalogos.next()) {
				while (rsCatalogos.next()) {
					CatalogoSchema catalogo = new CatalogoSchema(rsCatalogos.getString(1).trim(), 1);
					comboSchema.addItem(catalogo);
				}
			} else if (rsSchemas != null && rsSchemas.next()) {
				while (rsSchemas.next()) {
					CatalogoSchema schema = new CatalogoSchema(rsSchemas.getString(1).trim(), 2);
					comboSchema.addItem(schema);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setKeyMappings() {
		// InputMap jtabMap = this.abaResultado.getInputMap(2);
		// ActionMap jtabAction = this.abaResultado.getActionMap();
		// jtabMap.put(KeyStroke.getKeyStroke(86, 3), "pasteSql");

		// jtabAction.put("pasteSql", this.pasteSqlAction);
		//
		// jtabMap.put(KeyStroke.getKeyStroke(69, 3), "executeAgain");
		//
		// jtabAction.put("executeAgain", this.sqlExecuteAgainAction);
		//
		// jtabMap.put(KeyStroke.getKeyStroke(115, 2), "closeResult");
		// jtabAction.put("closeResult", this.resultsClose);
		//
		// jtabMap.put(KeyStroke.getKeyStroke(115, 3), "closeAllResults");
		// jtabAction.put("closeAllResults", this.resultsCloseAll);
		//
		// jtabMap.put(KeyStroke.getKeyStroke(67, 3), "copySql");
		//
		// jtabAction.put("copySql", this.copySqlAction);
		// jtabMap.put(KeyStroke.getKeyStroke(83, 3), "saveDelimited");
		//
		// jtabAction.put("saveDelimited", this.saveAsDelimitedAction);
		// jtabMap.put(KeyStroke.getKeyStroke(82, 3), "viewReport");
		//
		// jtabAction.put("viewReport", this.viewReportAction);
		//
		this.btnConsultar.getInputMap(2).put(KeyStroke.getKeyStroke(116, 0), "executarSQL");
		this.btnConsultar.getActionMap().put("executarSQL", this.executarSQLAction);
	}

	public RSyntaxTextArea getTxtSQL() {
		return txtSQL;
	}

	public void setTxtSQL(RSyntaxTextArea txtSQL) {
		this.txtSQL = txtSQL;
	}

	public JTabbedPane getAbaResultado() {
		return abaResultado;
	}

	public void setAbaResultado(JTabbedPane abaResultado) {
		this.abaResultado = abaResultado;
	}

	public Connection getConexao() {
		return Conexao.getConexao();
	}

	public JTextArea getTxtMensagem() {
		return txtMensagem;
	}

	public StatusBar getStatusBar() {
		return stsbrPronto;
	}


}
