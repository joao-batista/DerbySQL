package br.com.query.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import br.com.query.action.ExecutarSQLAction;
import br.com.query.util.Constantes;
import br.com.query.util.Recursos;
import br.com.query.view.Principal;

public class TabResultado extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private TabelaResultado tabelaResultado;
	private JButton btnPDF;
	private JButton btnTruncar;
	private JButton btnProximo;
	private JButton btnAnterior;
	private JButton btnPrimeiro;
	private JButton btnUltimo;
//	private JToolBar barraDeBotoes;
	private JScrollPane scroll;
	private String query;
	private int pagina = 0;
	private int maxPaginas;
	private ExecutarSQLAction executar;
	public static final int QUANTIDADE_DE_LINHAS = 20;
	
	public TabResultado(TabelaResultado tabelaReualtado, Principal principal){
		this.executar = new ExecutarSQLAction(principal);
		this.tabelaResultado = tabelaReualtado;
		inicializarComponentes();
		habilitarBotoes();
	}

	private void inicializarComponentes() {
		setLayout(new BorderLayout());
		
		int rowCount = this.tabelaResultado.getContadorDeLinhas();
		maxPaginas = rowCount / QUANTIDADE_DE_LINHAS + (rowCount % QUANTIDADE_DE_LINHAS == 0 ? 0 : 1);
		//
		// btnPDF
		//
		this.btnPDF = new JButton();
		this.btnPDF.setBackground(Color.WHITE);
		this.btnPDF.setFocusPainted(false);
		this.btnPDF.setToolTipText("Conectar");
		this.btnPDF.setIcon(Recursos.carregarIcone(Constantes.DOCUMENTO_PDF));
		this.btnPDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		//
		// btnTruncar
		//
		this.btnTruncar = new JButton();
		this.btnTruncar.setBackground(Color.WHITE);
		this.btnTruncar.setFocusPainted(false);
		this.btnTruncar.setToolTipText("Conectar");
		this.btnTruncar.setIcon(Recursos.carregarIcone(Constantes.TRUNCAR_TABELA));
		this.btnTruncar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		//
		// btnProximo
		//
		this.btnProximo = new JButton();
		this.btnProximo.setBackground(Color.WHITE);
		this.btnProximo.setFocusPainted(false);
		this.btnProximo.setToolTipText("Conectar");
		this.btnProximo.setIcon(Recursos.carregarIcone(Constantes.SETA_PROXIMO));
		this.btnProximo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pagina += 1;
				executar.executarConsulta(query, false, pagina, true);
				habilitarBotoes();
			}
		});
		//
		// btnAnterior
		//
		this.btnAnterior = new JButton();
		this.btnAnterior.setBackground(Color.WHITE);
		this.btnAnterior.setFocusPainted(false);
		this.btnAnterior.setToolTipText("Conectar");
		this.btnAnterior.setIcon(Recursos.carregarIcone(Constantes.SETA_ANTERIOR));
		this.btnAnterior.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pagina -= 1;
				executar.executarConsulta(query, false, pagina, true);
				habilitarBotoes();
			}
		});
		//
		// btnPrimeiro
		//
		this.btnPrimeiro = new JButton();
		this.btnPrimeiro.setBackground(Color.WHITE);
		this.btnPrimeiro.setFocusPainted(false);
		this.btnPrimeiro.setToolTipText("Conectar");
		this.btnPrimeiro.setIcon(Recursos.carregarIcone(Constantes.SETA_PRIMEIRO));
		this.btnPrimeiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pagina = 0;
				executar.executarConsulta(query, false, pagina, true);
				habilitarBotoes();
			}
		});
		//
		// btnUltimo
		//
		this.btnUltimo = new JButton();
		this.btnUltimo.setBackground(Color.WHITE);
		this.btnUltimo.setFocusPainted(false);
		this.btnUltimo.setToolTipText("Conectar");
		this.btnUltimo.setIcon(Recursos.carregarIcone(Constantes.SETA_ULTIMO));
		this.btnUltimo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pagina = maxPaginas - 1;
				executar.executarConsulta(query, false, pagina, true);
				habilitarBotoes();
			}
		});
		//
		// barraDeBotoes
		//
//		this.barraDeBotoes = new JToolBar();
//		this.barraDeBotoes.setBackground(Color.WHITE);
//		this.barraDeBotoes.setFloatable(false);
//		this.barraDeBotoes.add(btnExcel);
////		this.barraDeBotoes.add(btnPDF);
//		this.barraDeBotoes.add(btnPrimeiro);
//		this.barraDeBotoes.add(btnAnterior);
//		this.barraDeBotoes.add(btnProximo);
//		this.barraDeBotoes.add(btnUltimo);
//		this.barraDeBotoes.add(btnTruncar);
		//
		// scroll
		//
		this.scroll = new JScrollPane();
		this.scroll.setViewportView(this.tabelaResultado);
		scroll.setHorizontalScrollBarPolicy(30);
		scroll.setVerticalScrollBarPolicy(22);
		
//		add(barraDeBotoes, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		setBorder(null);
	}

	private void habilitarBotoes(){
		btnPrimeiro.setEnabled(pagina > 0);
		btnAnterior.setEnabled(pagina > 0);
		btnProximo.setEnabled(pagina < maxPaginas - 1);
		btnUltimo.setEnabled(pagina < maxPaginas - 1);
	}
	public JTable getTabelaResultado() {
		return tabelaResultado;
	}

	public void setTabelaResultado(TabelaResultado tabelaResultado) {
		this.tabelaResultado = tabelaResultado;
		this.scroll.setViewportView(this.tabelaResultado);
	}

	public JButton getBtnExcluir() {
		return btnPDF;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	

}
