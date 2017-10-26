package br.com.query.componentes;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import br.com.query.model.Campo;
import br.com.query.model.Indice;
import br.com.query.model.Item;
import br.com.query.util.Constantes;

public class ArvoreNavegacao extends JTree {

	private static final long serialVersionUID = 1L;

	public ArvoreNavegacao(DefaultTreeModel treeModel) {
		super(treeModel);
	}

	public static class CustomizadorDeItem implements TreeCellRenderer {
		private JLabel label;
		DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

		public CustomizadorDeItem() {
			label = new JLabel();
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

			label = (JLabel) defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

			Object objeto = ((DefaultMutableTreeNode) value).getUserObject();
			String objetoString = objeto.toString();
			if (objetoString.equals("Database")) {
				URL imageUrl = getClass().getResource(Constantes.CONECTAR);
				if (imageUrl != null) {
					label.setIcon(new ImageIcon(imageUrl));
				}
				label.setText("Database");
			} else if (objetoString.equals("Tabelas") || objetoString.equals("Views")) {
				URL imageUrl = getClass().getResource(Constantes.DIRETORIO);
				if (imageUrl != null) {
					label.setIcon(new ImageIcon(imageUrl));
				}
				label.setText(objetoString);
			} else if (objetoString.equals("Indices")) {
				URL imageUrl = getClass().getResource(Constantes.INDICES);
				if (imageUrl != null) {
					label.setIcon(new ImageIcon(imageUrl));
				}
				label.setText(objetoString);
			} else if (objeto instanceof Item) {
				Item item = (Item) objeto;
				URL imageUrl = getClass().getResource(item.getIcone());
				if (imageUrl != null) {
					label.setIcon(new ImageIcon(imageUrl));
				}
				label.setText(item.getTabela());
			} else if (objeto instanceof Campo) {
				Campo campo = (Campo) objeto;
				URL imageUrl = getClass().getResource(campo.getIcone());
				if (imageUrl != null) {
					label.setIcon(new ImageIcon(imageUrl));
				}
			} else if (objeto instanceof Indice) {
				Indice indice = (Indice) objeto;
				URL imageUrl = getClass().getResource(indice.getIcone());
				if (imageUrl != null) {
					label.setIcon(new ImageIcon(imageUrl));
				}
				label.setText(indice.getNome());
			} else {
				label.setIcon(null);
				label.setText("" + value);
			}

			if (selected) {
				label.setBackground(Color.BLUE);
			}

			return label;
		}
	}
}
