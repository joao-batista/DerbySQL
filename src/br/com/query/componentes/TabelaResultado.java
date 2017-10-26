package br.com.query.componentes;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class TabelaResultado extends JTable {

	private static final long serialVersionUID = 1L;
	private int contadorDeLinhas = 0;
	private boolean ajustar;
	private String consulta;

	public TabelaResultado() {
	}

	public TabelaResultado(Vector<?> linhas, Vector<String> colunas) {
		super(linhas, colunas);
	}

	public int getContadorDeLinhas() {
		return this.contadorDeLinhas;
	}

	public void setContadorDeLinhas(int paramInt) {
		this.contadorDeLinhas = paramInt;
	}

	public String getConsulta() {
		return this.consulta;
	}

	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}

	public boolean getAjustar() {
		return this.ajustar;
	}

	public void setAjustar(boolean ajustar) {
		this.ajustar = ajustar;
	}

	public void ajustarLarguraDaColunaAoConteudo() {
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int larguraMinima = 150;

		for (int indiceDaColuna = 0; indiceDaColuna < this.getColumnCount(); indiceDaColuna++) {
			TableColumn coluna = this.getColumnModel().getColumn(indiceDaColuna);
			int largura = coluna.getMinWidth();

			for (int linha = 0; linha < this.getRowCount(); linha++) {
				TableCellRenderer cellRenderer = this.getCellRenderer(linha, indiceDaColuna);
				Component componente = this.prepareRenderer(cellRenderer, linha, indiceDaColuna);
				int width = componente.getPreferredSize().width + this.getIntercellSpacing().width;
				largura = Math.max(largura, width);
			}

			if (largura < larguraMinima) {
				largura = larguraMinima;
			}
			coluna.setPreferredWidth(largura);
		}
	}

	public static class MeuRenderer implements TableCellRenderer {

		private JTable tabela;
		private TabResultado tabResultado;
		
		public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
		private Component renderer;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			this.tabela = table;
			this.tabResultado = (TabResultado) tabela.getParent().getParent().getParent();
			
			renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			
			((JLabel) renderer).setOpaque(true);

			Color foreground, background;
			

			if (isSelected) {
				tabResultado.getBtnExcluir().setEnabled(true);
				foreground = Color.white;
				background = new Color(51, 153, 255);
			} else if (row % 2 == 0) {
				foreground = Color.BLACK;
				background = Color.white;

			} else {
				foreground = Color.BLACK;
				background = new Color(238, 243, 253);

			}

			renderer.setForeground(foreground);
			renderer.setBackground(background);
			return renderer;

		}
	}
	
	public boolean isCellEditable(int row, int column){  
        return false;  
    }
}
