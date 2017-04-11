package com.usc.hanafuda.screens;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class HowToScreen extends JFrame{
	public HowToScreen(){
		super("How to play Hanafuda");

		JPanel jp = new JPanel();
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
	    try {
			editorPane.setPage(new URL("http://www.hanafuda.com/rules"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    JScrollPane jsp = new JScrollPane(editorPane);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    jp.add(jsp, BorderLayout.CENTER);

	    JPanel jp2 = new JPanel();
	    JEditorPane editorPane2 = new JEditorPane();
		editorPane2.setEditable(false);
	    try {
			editorPane2.setPage(new URL("http://www.hanafuda.com/cards"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    JScrollPane jsp2 = new JScrollPane(editorPane2);
		jsp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jp2.add(jsp2, BorderLayout.CENTER);

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("General Rules of Hanafuda", jsp);
    tabbedPane.addTab("Deck and Card Descriptions", jsp2);

    add(tabbedPane);

		setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		setSize(800,800);
		setLocation(400, 200);
		setVisible(true);
		setResizable(true);
	}

}
