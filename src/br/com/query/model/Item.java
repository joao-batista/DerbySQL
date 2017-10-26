package br.com.query.model;

public class Item implements Comparable<Object> {
    
    private String tabela;
    private String schema;
    private String catalogo;
    private String tipo;
    private String icone;

    public Item(String tabela, String schema, String catalog, String tipo, String icone) {
	this.tabela = tabela;
	this.schema = schema;
	this.catalogo = catalog;
	this.tipo = tipo;
	this.icone = icone;
    }

    public int compareTo(Object paramObject) {
	String str = toString();
	return str.compareTo(paramObject.toString());
    }

    public String getCatalogo() {
	return this.catalogo;
    }

    public String getSchema() {
	return this.schema;
    }

    public String getTabela() {
	return this.tabela;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String toString() {
	String retorno = this.tabela;
	if ((this.schema != null) && (this.schema.length() > 0)) {
	    retorno = this.schema + "." + retorno;
	    if ((this.catalogo != null) && (this.schema != null) && (this.catalogo.length() > 0)) {
		retorno = this.catalogo + "." + retorno;
	    }
	}
	return retorno;
    }
}
