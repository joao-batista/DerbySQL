package br.com.query.model;

public class ConexaoDoUsuario {

    private Integer id;
    private String nome;
    private String stringDeConexao;
    private String url;
    private String usuario;
    private String senha;

    public ConexaoDoUsuario(String nome, String stringDeConexao, String url, String usuario, String senha) {
	this.nome = nome;
	this.stringDeConexao = stringDeConexao;
	this.url = url;
	this.usuario = usuario;
	this.senha = senha;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getNome() {
	return nome;
    }

    public String getStringDeConexao() {
	return stringDeConexao;
    }

    public String getUrl() {
	return url;
    }

    public String getUsuario() {
	return usuario;
    }

    public String getSenha() {
	return senha;
    }

    public void setNome(String nome) {
	this.nome = nome;
    }

    public void setStringDeConexao(String stringDeConexao) {
	this.stringDeConexao = stringDeConexao;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public void setUsuario(String usuario) {
	this.usuario = usuario;
    }

    public void setSenha(String senha) {
	this.senha = senha;
    }
    
    public String toString() {
	return getNome();
    }

}
