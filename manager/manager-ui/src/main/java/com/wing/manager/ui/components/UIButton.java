package com.wing.manager.ui.components;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class UIButton extends JButton {
	private static final long serialVersionUID = -9071264942677707620L;

	public UIButton(final String resource) {
		super(new ImageIcon(UIButton.class.getResource(resource)));
	}
}
