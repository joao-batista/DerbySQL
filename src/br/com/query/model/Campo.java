package br.com.query.model;

public class Campo {
    
    private String nome;
    private String tipo;
    private String tamanho;
    private int nulo;
    private String icone;
    private boolean chave;

    public Campo(String nome, String tipo, String tamanho, int nulo, String icone, boolean chave) {
	this.nome = nome;
	this.tipo = tipo;
	this.tamanho = tamanho;
	this.nulo = nulo;
	this.icone = icone;
	this.chave = chave;
    }

    public String getNome() {
	return this.nome;
    }

    public String getTipo() {
	return this.tipo;
    }

    public String getTamanho() {
	return this.tamanho;
    }

    public boolean equals(Object objeto) {
	return true;
    }

    public String toString() {
	String retorno = this.nome + " := " + this.tipo + "(" + this.tamanho + ")";
	String nullStr = "";
	switch (this.nulo) {
	case 0:
	    nullStr = " not null";
	    break;
	case 1:
	    nullStr = " null";
	    break;
	case 2:
	    nullStr = " null unknown";
	}
	retorno = retorno + nullStr;
	return retorno;
    }

    public int getNulo() {
	return this.nulo;
    }

    public void setNulo(int nulo) {
	this.nulo = nulo;
    }

    public String getIcone() {
	return icone;
    }

    public void setIcone(String icone) {
	this.icone = icone;
    }

    public boolean isChave() {
        return chave;
    }

    public void setChave(boolean chave) {
        this.chave = chave;
    }
}
