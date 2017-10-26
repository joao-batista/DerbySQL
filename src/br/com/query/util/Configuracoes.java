package br.com.query.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import br.com.query.model.ConexaoDoUsuario;

public class Configuracoes {

	private static Connection conexao;

	public Configuracoes() {
		try {
			if (conexao == null) {
				criarConexao();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void criarConexao() throws ClassNotFoundException, SQLException {

		String userName = "sa";
		String password = "";
		String hostUrl = "jdbc:sqlite:" + Constantes.DIRETORIO_DA_BASE;
		Class.forName("org.sqlite.JDBC");

		conexao = DriverManager.getConnection(hostUrl, userName, password);

		criarEstruturaInicial();
	}

	
	
	private void criarEstruturaInicial() throws SQLException {

		String sql = "CREATE TABLE IF NOT EXISTS CONEXOES_DO_USUARIO (ID INTEGER PRIMARY KEY, NOME VARCHAR(100) NOT NULL, STRINGDECONEXAO VARCHAR(200) NOT NULL, URL VARCHAR(100) NOT NULL, USUARIO VARCHAR(200) NOT NULL, SENHA VARCHAR(100) NOT NULL)";

		try {
			Statement stmt = conexao.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
		}
	}

	public Vector<ConexaoDoUsuario> listarConexoes() {

		Vector<ConexaoDoUsuario> listaDeConexoes = new Vector<ConexaoDoUsuario>();

		String sql = "SELECT * FROM CONEXOES_DO_USUARIO";

		try {
			Statement stmt = conexao.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				ConexaoDoUsuario conexaoDoUsuario = criarConexaoDoUsuario(resultSet);
				listaDeConexoes.add(conexaoDoUsuario);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return listaDeConexoes;
	}

	public void inserirConexao(ConexaoDoUsuario conexaoDoUsuario) {

		if (!existeConexao(conexaoDoUsuario)) {

			String sql = "INSERT INTO CONEXOES_DO_USUARIO(NOME, STRINGDECONEXAO, URL, USUARIO, SENHA) VALUES (?, ?, ?, ?, ?)";

			try {
				PreparedStatement stmt = conexao.prepareStatement(sql);

				stmt.setString(1, conexaoDoUsuario.getNome());
				stmt.setString(2, conexaoDoUsuario.getStringDeConexao());
				stmt.setString(3, conexaoDoUsuario.getUrl());
				stmt.setString(4, conexaoDoUsuario.getUsuario());
				stmt.setString(5, conexaoDoUsuario.getSenha());

				stmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void excluirConexao(ConexaoDoUsuario conexaoDoUsuario) {

		String sql = "DELETE FROM CONEXOES_DO_USUARIO WHERE NOME = ? ";

		try {
			PreparedStatement stmt = conexao.prepareStatement(sql);

			stmt.setString(1, conexaoDoUsuario.getNome());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private ConexaoDoUsuario criarConexaoDoUsuario(ResultSet resultSet) throws SQLException {
		return new ConexaoDoUsuario(resultSet.getString("NOME"), resultSet.getString("STRINGDECONEXAO"), resultSet.getString("URL"), resultSet.getString("USUARIO"), resultSet.getString("SENHA"));
	}

	public boolean existeConexao(ConexaoDoUsuario conexaoDoUsuario) {

		boolean retorno = false;
		String sql = "SELECT * FROM CONEXOES_DO_USUARIO WHERE NOME = '" + conexaoDoUsuario.getNome() + "'";

		try {
			Statement stmt = conexao.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			if (resultSet.next()) {
				retorno = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return retorno;
	}

}
