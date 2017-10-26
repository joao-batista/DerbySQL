package br.com.query.view;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import br.com.query.componentes.ArvoreNavegacao;
import br.com.query.componentes.ArvoreNavegacao.CustomizadorDeItem;
import br.com.query.helper.NavegacaoHelper;
import br.com.query.model.CatalogoSchema;
import br.com.query.model.Item;

/**
 * @author JOAO
 *
 */
public class Navegacao extends JPanel implements TreeExpansionListener {

	private static final long serialVersionUID = 1L;

	private final DefaultMutableTreeNode tabelas = new DefaultMutableTreeNode("Tabelas");
	private final DefaultMutableTreeNode views = new DefaultMutableTreeNode("Views");

	private boolean expandindo;

	private DefaultMutableTreeNode noRaiz;
	private DefaultTreeModel treeModel;

	private ArvoreNavegacao arvoreDeNavegacao;
	private JScrollPane scroll;

	private GridBagConstraints container;

	private JPopupMenu menu;
	private Item itemSelecionado;
	private static Principal principal;
	static int posicaoDoCusor;

	public Navegacao(Principal principalP) {
		principal = principalP;
		incializarComponentes();
	}
	
	public Navegacao() {
		
	}

	private void incializarComponentes() {
		setLayout(new GridBagLayout());
		//
		// noRaiz
		//
		this.noRaiz = new DefaultMutableTreeNode("Database");
		//
		// treeModel
		//
		this.treeModel = new DefaultTreeModel(this.noRaiz);
		//
		// arvoreDeNavegacao
		//
		this.arvoreDeNavegacao = new ArvoreNavegacao(this.treeModel);
		this.arvoreDeNavegacao.setCellRenderer(new CustomizadorDeItem());
		this.arvoreDeNavegacao.addTreeExpansionListener(this);
		this.arvoreDeNavegacao.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.arvoreDeNavegacao.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evento) {
				Navegacao.this.abrirMenu(evento);
			}
		});
		//
		// scroll
		//
		this.scroll = new JScrollPane();
		scroll.setViewportView(arvoreDeNavegacao);
		//
		// container
		//
		this.container = new GridBagConstraints();
		this.container.fill = 1;
		this.container.weightx = 1.0D;
		this.container.weighty = 1.0D;

		add(scroll, this.container);
	}
	
	@Override
	public void treeCollapsed(TreeExpansionEvent evento) {

	}

	@Override
	public void treeExpanded(TreeExpansionEvent evento) {
		setCursor(new Cursor(3));
		popularCampos(evento);
		setCursor(new Cursor(0));
	}

	public void carregarTabelas(CatalogoSchema tipo) {
		setCursor(new Cursor(3));
		this.noRaiz.removeAllChildren();
		try {
			ResultSet resultSet = NavegacaoHelper.consultarListaDeTabelas(tipo);
			montarArvore(resultSet);
			resultSet.close();
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(this, "Sql exception: " + se.getMessage());
		}
		this.treeModel.reload();
		this.arvoreDeNavegacao.expandPath(new TreePath(tabelas.getPath()));
		repaint();
		setCursor(new Cursor(0));
	}

	public void liberarTabelas() {
		this.noRaiz.removeAllChildren();
		this.treeModel.reload();
		repaint();
	}
	
	private void montarArvore(ResultSet resultSet) throws SQLException {
		this.tabelas.removeAllChildren();
		this.views.removeAllChildren();
		this.noRaiz.add(tabelas);
		this.noRaiz.add(views);
		TreeSet<Item> itens = new TreeSet<Item>();
		while (resultSet.next()) {
			itens.add(NavegacaoHelper.criarItem(resultSet));
		}
		for (Item item : itens) {
			Vector<String> campos = new Vector<String>();
			campos.add("placeholder");
			DefaultMutableTreeNode defautItem = new DefaultMutableTreeNode(item);
			if (item.getTipo().equals("TABLE")) {
				this.tabelas.add(defautItem);
			} else if (item.getTipo().equals("VIEW")) {
				this.views.add(defautItem);
			}
			for (String campo : campos) {
				DefaultMutableTreeNode novoCampo = new DefaultMutableTreeNode(campo);
				defautItem.add(novoCampo);
			}
		}
	}

	protected void abrirMenu(MouseEvent evento) {
		if (evento.isPopupTrigger()) {
			TreePath item = arvoreDeNavegacao.getClosestPathForLocation(evento.getX(), evento.getY());
			Rectangle contornoDoItem = arvoreDeNavegacao.getUI().getPathBounds(arvoreDeNavegacao, item);
			if (item != null && contornoDoItem.contains(evento.getX(), evento.getY())) {
				arvoreDeNavegacao.setSelectionPath(item);
				int level = arvoreDeNavegacao.getSelectionPath().getPathCount();
				if (level == 3) {
					criarPopUP(item);
					menu.show(arvoreDeNavegacao, evento.getX(), evento.getY());
				}
			}
		}
	}

	public void criarPopUP(TreePath item) {
		DefaultMutableTreeNode no = (DefaultMutableTreeNode) item.getLastPathComponent();
		Object objeto = no.getUserObject();
		if (objeto.getClass().getName().equals("br.com.query.model.Item")) {
			itemSelecionado = (Item) objeto;

			JMenuItem menuItemSelect = new JMenuItem("SELECT");
			menuItemSelect.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evento) {
					String sql = "SELECT * FROM " + itemSelecionado.toString() + ";";
					if (!principal.getTxtSQL().getText().equals("")) {
						principal.getTxtSQL().append("\n");
					}
					escreverSQL(sql);
				}
			});
			JMenuItem menuItemInsert = new JMenuItem("INSERT");
			menuItemInsert.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evento) {
					String sql = "INSERT INTO " + itemSelecionado.toString() + " VALUES(" + NavegacaoHelper.listarColunas(itemSelecionado, false) + ");";
					principal.getTxtSQL().append("\n");
					escreverSQL(sql);
				}
			});
			JMenuItem menuItemUpdate = new JMenuItem("UPDATE");
			menuItemUpdate.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evento) {
					String sql = "UPDATE " + itemSelecionado.toString() + " \r\nSET " + NavegacaoHelper.listarColunas(itemSelecionado, true) + "\r\n WHERE Condicoes;";
					principal.getTxtSQL().append("\n");
					escreverSQL(sql);
				}
			});
			JMenuItem menuItemDelete = new JMenuItem("DELETE");
			menuItemDelete.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evento) {
					String sql = "DELETE FROM " + itemSelecionado.toString() + ";";
					if (!principal.getTxtSQL().getText().equals("")) {
						principal.getTxtSQL().append("\n");
					}
					escreverSQL(sql);
				}
			});
			menu = new JPopupMenu();
			menu.add(menuItemSelect);
			menu.add(menuItemInsert);
			menu.add(menuItemUpdate);
			menu.add(menuItemDelete);
		}

	}

	public static void escreverSQL(String sql) {
		if(!"".equals(sql)){
			RSyntaxTextArea txtSQL = principal.getTxtSQL();
			txtSQL.requestFocus();
			txtSQL.append(sql);
			if(txtSQL.getCaretPosition() != posicaoDoCusor){
				txtSQL.setCaretPosition(posicaoDoCusor);
			}
			txtSQL.setSelectionStart(txtSQL.getCaretPosition());
			txtSQL.setSelectionEnd(txtSQL.getDocument().getLength());
			posicaoDoCusor = txtSQL.getDocument().getLength();
		}
	}
	
	public void popularCampos(TreeExpansionEvent evento) {
		double inicio = System.currentTimeMillis();
		if (!this.expandindo) {
			TreePath origem = evento.getPath();
			DefaultMutableTreeNode no = (DefaultMutableTreeNode) origem.getLastPathComponent();
			Object objeto = no.getUserObject();

			if (objeto.getClass().getName().equals("br.com.query.model.Item")) {
				Item item = (Item) objeto;
				if (no.getChildCount() == 1) {
					no.remove(0);
					try {

						NavegacaoHelper.carregarIndices(no, item);
						NavegacaoHelper.carregarColunas(no, item);

					} catch (SQLException se) {
						JOptionPane.showMessageDialog(this, "Sql exception: " + se.getMessage());
					}
				}
				setCursor(new Cursor(0));
				this.treeModel.reload(no);
				this.expandindo = true;
				this.arvoreDeNavegacao.expandPath(origem);
			}
			this.expandindo = false;
			double fim = System.currentTimeMillis();
			System.out.println("Executado em: " + (fim - inicio) / 1000 + "s");
		}
	}
	
}
