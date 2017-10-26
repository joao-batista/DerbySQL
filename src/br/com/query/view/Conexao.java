package br.com.query.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import br.com.query.model.ConexaoDoUsuario;
import br.com.query.model.Driver;
import br.com.query.util.Configuracoes;
import br.com.query.util.Constantes;
import br.com.query.util.Recursos;

public class Conexao extends JDialog {

	private static final long serialVersionUID = 1L;
	private JLabel lblNome;
	private JLabel lblURL;
	private JLabel lblUsuario;
	private JLabel lblSenha;
	private JLabel lblStringDeConexao;
	private JTextField txtNome;
	private JComboBox<Driver> comboDrives;
	private JTextField txtURL;
	private JTextField txtUsuario;
	private JPasswordField txtSenha;
	private JList<ConexaoDoUsuario> conexoes;
	private JPopupMenu menu;
	private JMenuItem itemMenuNovo;
	private JMenuItem itemMenuExcluir;
	private JScrollPane scrollConexoes;
	private JButton btnConectar;
	private JButton btnCancelar;
	private JPanel contentPane;

	private static Connection conexao;
	private boolean cancelado = false;
	private Configuracoes configuracoes;

	public Conexao(Principal principal, boolean booleano) {
		super(principal, booleano);
		initializeComponent();
		montarListaDeDrives();
		montarListaDeConexoes();
	}

	private void initializeComponent() {

		configuracoes = new Configuracoes();
		//
		// lblNome
		//
		lblNome = new JLabel();
		lblNome.setText("Nome: ");
		//
		// lblURL
		//
		lblURL = new JLabel();
		lblURL.setText("URL: ");
		//
		// lblUsuario
		//
		lblUsuario = new JLabel();
		lblUsuario.setText("Usuario:");
		//
		// lblSenha
		//
		lblSenha = new JLabel();
		lblSenha.setText("Senha:");
		//
		// lblStringDeConexao
		//
		lblStringDeConexao = new JLabel();
		lblStringDeConexao.setText("Driver:");
		//
		// txtURL
		//
		txtNome = new JTextField();
		//
		// comboDrives
		//
		comboDrives = new JComboBox<Driver>();
		//
		// txtURL
		//
		txtURL = new JTextField();
		//
		// txtUsuario
		//
		txtUsuario = new JTextField();
		//
		// txtSenha
		//
		txtSenha = new JPasswordField();
		//
		// listaDeDrives
		//
		conexoes = new JList<ConexaoDoUsuario>();
		conexoes.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

				Component retValue = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				((JLabel) retValue).setIcon((ImageIcon) Recursos.carregarIcone(Constantes.PLUGAR));

				return retValue;
			}
		});
		conexoes.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selecionarConexao(e);
			}

		});
		conexoes.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evento) {
				Conexao.this.abrirMenu(evento);
			}
		});
		//
		// menu
		//
		menu = new JPopupMenu();
		//
		// itemMenuNovo
		//
		itemMenuNovo = new JMenuItem();
		itemMenuNovo.setText("novo");
		itemMenuNovo.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evento) {
				Conexao.this.novaConexao();
			}
		});
		//
		// itemMenuExcluir
		//
		itemMenuExcluir = new JMenuItem();
		itemMenuExcluir.setText("excluir");
		itemMenuExcluir.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evento) {
				Conexao.this.excluirConexaoDoUsuario();
			}
		});
		menu.add(itemMenuNovo);
		menu.addSeparator();
		menu.add(itemMenuExcluir);
		//
		// scrollConexoes
		//
		scrollConexoes = new JScrollPane();
		scrollConexoes.setViewportView(conexoes);
		//
		// btnConectar
		//
		btnConectar = new JButton();
		btnConectar.setText("Conectar");
		btnConectar.setIcon(Recursos.carregarIcone(Constantes.OK));
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelado = false;
				conectar();
			}

		});
		//
		// btnCancelar
		//
		btnCancelar = new JButton();
		btnCancelar.setText("Cancelar");
		btnCancelar.setIcon(Recursos.carregarIcone(Constantes.FECHAR));
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelado = true;
				Conexao.this.setVisible(false);
			}

		});
		//
		// contentPane
		//
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(null);
		addComponent(contentPane, lblNome, 200, 18, 45, 18);
		addComponent(contentPane, lblURL, 200, 100, 45, 18);
		addComponent(contentPane, lblUsuario, 200, 140, 45, 18);
		addComponent(contentPane, lblSenha, 200, 180, 45, 18);
		addComponent(contentPane, lblStringDeConexao, 200, 66, 45, 18);
		addComponent(contentPane, comboDrives, 250, 60, 300, 22);
		addComponent(contentPane, txtNome, 250, 20, 300, 22);
		addComponent(contentPane, txtURL, 250, 100, 300, 22);
		addComponent(contentPane, txtUsuario, 250, 140, 300, 22);
		addComponent(contentPane, txtSenha, 250, 180, 300, 22);
		addComponent(contentPane, scrollConexoes, 5, 5, 180, 270);
		addComponent(contentPane, btnConectar, 250, 230, 120, 28);
		addComponent(contentPane, btnCancelar, 430, 230, 120, 28);
		//
		// Conexao_
		//
		this.setTitle("Conexao_ - extends JFrame");
		this.setLocation(new Point(0, 0));
		this.setSize(new Dimension(573, 308));
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}

	protected void abrirMenu(MouseEvent evento) {
		if (evento.isPopupTrigger()) {
			conexoes.setSelectedIndex(conexoes.locationToIndex(evento.getPoint()));
			menu.show(conexoes, evento.getX(), evento.getY());
		}
	}

	private void addComponent(Container container, Component c, int x, int y, int width, int height) {
		c.setBounds(x, y, width, height);
		container.add(c);
	}

	private void selecionarConexao(ListSelectionEvent evento) {
		if (!evento.getValueIsAdjusting()) {
			ConexaoDoUsuario conexaoDoUsuario = conexoes.getSelectedValue();
			txtNome.setText(((conexaoDoUsuario == null) ? "" : conexaoDoUsuario.getNome()));
			comboDrives.setSelectedItem(comboDrives.getItemAt(getIndiceDaConexaoPela((conexaoDoUsuario == null) ? "" : conexaoDoUsuario.getStringDeConexao())));
			txtURL.setText(((conexaoDoUsuario == null) ? "" : conexaoDoUsuario.getUrl()));
			txtUsuario.setText(((conexaoDoUsuario == null) ? "" : conexaoDoUsuario.getUsuario()));
			txtSenha.setText(((conexaoDoUsuario == null) ? "" : conexaoDoUsuario.getSenha()));
		}
	}

	protected void novaConexao() {
		conexoes.clearSelection();
		txtNome.setText("");
		comboDrives.setSelectedItem(comboDrives.getItemAt(0));
		txtURL.setText("");
		txtUsuario.setText("");
		txtSenha.setText("");
	}

	private void montarListaDeDrives() {
		Vector<Driver> drivers = new Vector<Driver>();

		drivers.add(new Driver("Apache Derby Client", "org.apache.derby.jdbc.ClientDriver"));
		drivers.add(new Driver("Apache Derby Embedded", "org.apache.derby.jdbc.EmbeddedDriver"));

		for (Driver driver : drivers) {
			comboDrives.addItem(driver);
		}
	}

	private int getIndiceDaConexaoPela(String stringDeConexao) {
		for (int i = 0; i < comboDrives.getItemCount(); i++) {
			if (stringDeConexao.equals(comboDrives.getItemAt(i).getStringDeConexao())) {
				return i;
			}
		}
		return 0;
	}

	private void montarListaDeConexoes() {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				Conexao.this.setTitle("Aguarde...");
				habilitarComponentes(false);

				listarConexoes();

				Conexao.this.setTitle("Conexão");
				habilitarComponentes(true);
			}
		};
		Thread thread = new Thread(run);
		thread.start();
	}

	private void conectar() {
		try {
			fecharConexao();

			String nome = txtNome.getText();
			String url = this.txtURL.getText();
			if (nome != null && !"".equals(nome.trim()) || url != null && !"".equals(url.trim())) {
				Driver driverSelecionado = (Driver) this.comboDrives.getSelectedItem();
				String driver = driverSelecionado.getStringDeConexao();
				String usuario = this.txtUsuario.getText();
				String senha = new String(this.txtSenha.getPassword());

				Class.forName(driver);
				conexao = DriverManager.getConnection(url, usuario, senha);
				adicionarConexaoDoUsuario(new ConexaoDoUsuario(nome, driver, url, usuario, senha));
				setVisible(false);
			} else {
				JOptionPane.showMessageDialog(this, "Campo(s) obrigatório(s) não informado(s)", "Campo(s) não Informado(s)", 0);
			}

		} catch (ClassNotFoundException localClassNotFoundException) {
			JOptionPane.showMessageDialog(this, "Driver não Selecionado ou não compatível com a url", "Erro ao carregar Driver", 0);
		} catch (SQLException localSQLException) {
			JOptionPane.showMessageDialog(this, localSQLException.getMessage());
		}
	}

	private void adicionarConexaoDoUsuario(ConexaoDoUsuario conexaoDoUsuario) {
		configuracoes.inserirConexao(conexaoDoUsuario);
	}

	private void excluirConexaoDoUsuario() {
		configuracoes.excluirConexao(conexoes.getSelectedValue());
		listarConexoes();
	}

	private void listarConexoes() {
		conexoes.setListData(configuracoes.listarConexoes());
	}

	public void habilitarComponentes(boolean habilitar) {
		this.txtNome.setEnabled(habilitar);
		this.txtSenha.setEnabled(habilitar);
		this.txtURL.setEnabled(habilitar);
		this.txtUsuario.setEnabled(habilitar);
		this.comboDrives.setEnabled(habilitar);
		this.btnConectar.setEnabled(habilitar);
		this.btnCancelar.setEnabled(habilitar);
	}

	public boolean conectado() {
		return !this.cancelado;
	}

	public static Connection getConexao() {
		return conexao;
	}

	public String getTxtDriver() {
		return this.lblStringDeConexao.getText();
	}

	public String getTxtURL() {
		return this.txtURL.getText();
	}

	public String getTxtUsuario() {
		return this.txtUsuario.getText();
	}

	public void setTxtUsuario(String txtUsuario) {
		this.txtUsuario.setText(txtUsuario);
	}

	public String getTxtSenha() {
		return new String(this.txtSenha.getPassword());
	}

	public void setTxtSenha(String txtSenha) {
		this.txtSenha.setText(txtSenha);
	}

	public static void fecharConexao() {
		try {
			if (conexao != null) {
				conexao.close();
				conexao = null;
			}
		} catch (SQLException se) {
		}
	}

}
