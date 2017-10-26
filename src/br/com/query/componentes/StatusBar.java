package br.com.query.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class StatusBar extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel mensagem;

	public StatusBar() {


		//
		// mensagem
		//
		mensagem = new JLabel();
		mensagem.setMinimumSize(new Dimension(300, 20));
		mensagem.setSize(new Dimension(300, 20));
		mensagem.setFont(new Font("Dialog", Font.PLAIN, 10));
		mensagem.setForeground(Color.black);

		setLayout(new BorderLayout());
		setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		add(mensagem, BorderLayout.CENTER);
	}

	public void setMensagem(String info) {
		mensagem.setText(info);
		paintImmediately(getBounds());
	}

}
