package br.com.query.model;

public class Driver {

    private String nome;
    private String stringDeConexao;

    public Driver(String nome, String stringDeConexao) {
	this.nome = nome;
	this.stringDeConexao = stringDeConexao;
    }

    public String getNome() {
	return nome;
    }

    public String getStringDeConexao() {
	return stringDeConexao;
    }

    public void setNome(String nome) {
	this.nome = nome;
    }

    public void setStringDeConexao(String stringDeConexao) {
	this.stringDeConexao = stringDeConexao;
    }

    public String toString() {
	return getNome();
    }

}
