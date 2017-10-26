package br.com.query.helper;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import br.com.query.model.Campo;
import br.com.query.model.CatalogoSchema;
import br.com.query.model.Indice;
import br.com.query.model.Item;
import br.com.query.util.Constantes;
import br.com.query.view.Conexao;

public class NavegacaoHelper {

	private static String[] tipos = { "TABLE", "VIEW" };

	public static ResultSet consultarListaDeTabelas(CatalogoSchema tipo) {
		ResultSet resultSet = null;
		DatabaseMetaData metadata;
		try {
			metadata = Conexao.getConexao().getMetaData();
			Conexao.getConexao().setCatalog(tipo.getNome());

			if (tipo.getTipo() == 1) {
				resultSet = metadata.getTables(tipo.getNome(), null, "%", tipos);
			} else if (tipo.getTipo() == 2) {
				resultSet = metadata.getTables(null, tipo.getNome(), "%", tipos);
			} else {
				resultSet = metadata.getTables(null, null, "%", tipos);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	public static Hashtable<String, String> getPrimaryKeys(Item item) {
		DatabaseMetaData metadata;
		Hashtable<String, String> primary = new Hashtable<String, String>();
		try {
			metadata = Conexao.getConexao().getMetaData();

			try {
				String name = item.getTabela();
				String schema = item.getSchema();
				String catalog = schema == null ? null : metadata.getConnection().getCatalog();

				ResultSet rsPK = metadata.getPrimaryKeys(catalog, schema, name);
				while (rsPK.next()) {
					primary.put(rsPK.getString("COLUMN_NAME").trim(), rsPK.getString("PK_NAME"));
				}
				rsPK.close();
			} catch (SQLException sqle) {
				System.out.println(sqle);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return primary;
	}

	public static void carregarColunas(DefaultMutableTreeNode no, Item item) throws SQLException {
		DatabaseMetaData metadata = Conexao.getConexao().getMetaData();
		ResultSet rsColunas = metadata.getColumns(item.getCatalogo(), item.getSchema(), item.getTabela(), "%");
		Hashtable<String, String> primary = NavegacaoHelper.getPrimaryKeys(item);
		while (rsColunas.next()) {
			DefaultMutableTreeNode campo = new DefaultMutableTreeNode(criarCampo(rsColunas, primary));
			no.add(campo);
		}
		rsColunas.close();
	}

	public static void carregarIndices(DefaultMutableTreeNode no, Item item) throws SQLException {
		DatabaseMetaData metadata = Conexao.getConexao().getMetaData();
		ResultSet rsIndices = metadata.getIndexInfo(item.getCatalogo(), item.getSchema(), item.getTabela(), false, false);
		DefaultMutableTreeNode noIndice = null;
		try {
			HashMap<String, Indice> indexes = new HashMap<String, Indice>();
			while (rsIndices.next()) {
				if (noIndice == null) {
					noIndice = new DefaultMutableTreeNode("Indices");
					no.add(noIndice);
				}
				String nomeIndice = rsIndices.getString("INDEX_NAME");

				Indice indice = (Indice) indexes.get(nomeIndice);
				if (indice == null) {
					indice = criarIndice(rsIndices, nomeIndice);
					if (rsIndices.getString("ASC_OR_DESC") != null) {
						indice.setDirecao(rsIndices.getString("ASC_OR_DESC"));
					}
					indexes.put(nomeIndice, indice);
				} else {
					indice.adicionarColuna(rsIndices.getString("COLUMN_NAME"));
				}
			}

			Iterator<Indice> colunas = indexes.values().iterator();
			while (colunas.hasNext()) {
				Indice indice = (Indice) colunas.next();
				DefaultMutableTreeNode indexChildNode = new DefaultMutableTreeNode(indice);
				noIndice.add(indexChildNode);
				String unique = indice.getUnica() ? "Unique: Yes" : "Unique: No";
				indexChildNode.add(new DefaultMutableTreeNode(unique));

				String columnLabel = (indice.getQuantidadeDeColunas() > 1) ? "Columns" : "Column";
				indexChildNode.add(new DefaultMutableTreeNode(columnLabel + ": " + indice.getColunas()));

				if (indice.getDirecao() != null) {
					if (indice.getDirecao().equals("A")) {
						indexChildNode.add(new DefaultMutableTreeNode("Direction: Ascending"));
					} else {
						indexChildNode.add(new DefaultMutableTreeNode("Direction: Descending"));
					}
				}
				indexChildNode.add(new DefaultMutableTreeNode("Cardinality: " + indice.getCardinalidade()));
			}

		} catch (SQLException idxEx) {
			no.remove(noIndice);
		}
	}

	public static Item criarItem(ResultSet resultSet) throws SQLException {
		Item item = new Item(resultSet.getString("TABLE_NAME"), resultSet.getString("TABLE_SCHEM"), resultSet.getString("TABLE_CAT"), resultSet.getString("TABLE_TYPE"), Constantes.TABELA);
		return item;
	}

	public static Indice criarIndice(ResultSet rsIndices, String nomeIndice) throws SQLException {
		Indice indice = new Indice(nomeIndice, rsIndices.getString("COLUMN_NAME"), rsIndices.getBoolean("NON_UNIQUE"), rsIndices.getInt("CARDINALITY"), Constantes.INDICE);
		return indice;
	}

	public static Campo criarCampo(ResultSet rsColunas, Hashtable<String, String> primary) throws SQLException {
		boolean chave = (primary.get(rsColunas.getString("COLUMN_NAME")) != null);
		String icone = chave ? Constantes.CHAVE : Constantes.COLUNA;
		Campo campo = new Campo(rsColunas.getString("COLUMN_NAME"), rsColunas.getString("TYPE_NAME"), rsColunas.getString("COLUMN_SIZE"), rsColunas.getInt("NULLABLE"), icone, chave);
		return campo;
	}

	public static String listarColunas(Item item, boolean parametros) {
		DatabaseMetaData metadata;
		ResultSet rsColunas;
		try {
			metadata = Conexao.getConexao().getMetaData();
			rsColunas = metadata.getColumns(item.getCatalogo(), item.getSchema(), item.getTabela(), "%");
			StringBuilder retorno = new StringBuilder();
			String parametro = "";
			if (parametros) {
				parametro = " = ? ";
			}
			String texto = "";
			while (rsColunas.next()) {
				texto = "\r\n\t" + rsColunas.getString("COLUMN_NAME") + parametro + ",";
				retorno.append(texto);
			}
			if (null != retorno && retorno.length() > 0) {
				int endIndex = retorno.lastIndexOf(",");
				if (endIndex != -1) {
					texto = retorno.substring(0, endIndex);
				}
			}

			if (retorno.length() == 0) {
				return null;
			} else {
				return texto;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ResultSet listarSchemas() {
		ResultSet rsSchemas = null;
		if (Conexao.getConexao() != null) {
			DatabaseMetaData metadata;
			try {
				metadata = Conexao.getConexao().getMetaData();
				if (existeSchema(metadata)) {
					rsSchemas = metadata.getSchemas();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rsSchemas;
	}

	public static ResultSet listarCatalogos() {
		ResultSet rsCatalogos = null;
		if (Conexao.getConexao() != null) {
			DatabaseMetaData metadata;
			try {
				metadata = Conexao.getConexao().getMetaData();
				if (existeCatalogo(metadata)) {
					rsCatalogos = metadata.getCatalogs();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rsCatalogos;
	}

	public static boolean existeSchema(DatabaseMetaData metadata) throws SQLException {
		String schema = metadata.getSchemaTerm();
		return schema != null && schema.length() > 0;
	}

	public static boolean existeCatalogo(DatabaseMetaData metadata) throws SQLException {
		String catalogo = metadata.getCatalogTerm();
		return catalogo != null && catalogo.length() > 0;
	}
}
