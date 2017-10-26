package br.com.query.model;

public class Indice {
    private String nome;
    private String colunas;
    private boolean unica;
    private int cardinalidade;
    private String direcao;
    private int quantidadeDeColunas;
    private String icone;

    public Indice(String nome, String colunas, boolean unica, int cardinalidade, String icone) {
	this.nome = nome;
	this.colunas = colunas;
	this.unica = unica;
	this.cardinalidade = cardinalidade;
	this.direcao = null;
	this.quantidadeDeColunas = 1;
	this.icone = icone;
    }

    public void adicionarColuna(String coluna) {
	this.colunas = (this.colunas + ", " + coluna);
	this.quantidadeDeColunas += 1;
    }

    public String getDirecao() {
	return this.direcao;
    }

    public void setDirecao(String direcao) {
	this.direcao = direcao;
    }

    public String getNome() {
	return this.nome;
    }

    public String getColunas() {
	return this.colunas;
    }

    public int getQuantidadeDeColunas() {
	return this.quantidadeDeColunas;
    }

    public boolean getUnica() {
	return this.unica;
    }

    public int getCardinalidade() {
	return this.cardinalidade;
    }

    public String getIcone() {
	return icone;
    }

    public void setIcone(String icone) {
	this.icone = icone;
    }
}
