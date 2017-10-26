package br.com.query.util;

import java.net.URL;

import javax.swing.ImageIcon;

public class Recursos {

	public static ImageIcon carregarIcone(String nome) {
		Class<Recursos> recurso = Recursos.class;
		if (nome != null) {
			return carregarIcone(recurso.getResource(nome));
		}
		return null;
	}

	public static ImageIcon carregarIcone(URL url) {
		return new ImageIcon(url);
	}
}
