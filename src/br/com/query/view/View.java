package br.com.query.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import br.com.query.util.Constantes;
import br.com.query.util.Recursos;

/**
 * Summary description for View
 *
 */
public class View extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private JTextArea texto;
	private JScrollPane scroll;
	private Container contentPane;
	private JToolBar barraDeBotoes;
	private JButton btnFechar;
	private Point point;

	public View(Point point) {
		super();
		this.point = point;
		initializeComponent();
		this.setVisible(true);
	}

	private void initializeComponent() {
		contentPane = getContentPane();

		//
		// texto
		//
		texto = new JTextArea();
//		texto.setLineWrap(true);
		//
		// scroll
		//
		scroll = new JScrollPane();
		scroll.setViewportView(texto);
		//
		// btnFechar
		//
		this.btnFechar = new JButton();
		this.btnFechar.setBackground(Color.GRAY);
		this.btnFechar.setFocusPainted(false);
		this.btnFechar.setToolTipText("Fechar");
		this.btnFechar.setIcon(Recursos.carregarIcone(Constantes.FECHAR));
		this.btnFechar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		//
		// barraDeBotoes
		//
		this.barraDeBotoes = new JToolBar();
		this.barraDeBotoes.setBackground(Color.GRAY);
		this.barraDeBotoes.setFloatable(false);
		this.barraDeBotoes.add(btnFechar);
		this.barraDeBotoes.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		//
		// contentPane
		//
		contentPane.setLayout(null);
		addComponent(contentPane, barraDeBotoes, 0, 0, 300, 25);
		addComponent(contentPane, scroll, 0, 25, 300, 195);
		//
		// View
		//
		this.setUndecorated(true);
		this.setLocation(this.point);
		((JComponent) this.getContentPane()).setBorder(new LineBorder(Color.BLACK));
		this.setResizable(false);
		this.setSize(new Dimension(300, 220));
	}

	private void addComponent(Container container, Component c, int x, int y, int width, int height) {
		c.setBounds(x, y, width, height);
		container.add(c);
	}

	private void close() {
		setVisible(false);
	}

	public void setTexto(String conteudo) {
		try {
			if (conteudo.matches("(?s).*(<(\\w+)[^>]*>.*</\\2>|<(\\w+)[^>]*/>).*"))
				texto.setText(format(conteudo));
			else
				texto.setText(conteudo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String format(String xml) throws Exception {

		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(xml)));

		OutputFormat format = new OutputFormat(doc);
		format.setIndenting(true);
		format.setIndent(2);
		format.setOmitXMLDeclaration(true);
		format.setLineWidth(Integer.MAX_VALUE);
		Writer outxml = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(outxml, format);
		serializer.serialize(doc);

		return outxml.toString();

	}
	
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			System.out.println("Failed loading L&F: ");
			System.out.println(ex);
		}
		new View(new Point(100, 100));
	}

}

