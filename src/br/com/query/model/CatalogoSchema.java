package br.com.query.model;

public class CatalogoSchema {

	private String nome;
	private int tipo;

	public CatalogoSchema(String nome, int tipo) {
		this.nome = nome;
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public int getTipo() {
		return tipo;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return nome;
	}
}
