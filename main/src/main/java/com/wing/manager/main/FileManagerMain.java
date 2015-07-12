package com.wing.manager.main;

import static java.awt.EventQueue.invokeLater;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import javax.swing.JFrame;

import com.wing.file.manager.ui.FileManagerUI;

public class FileManagerMain extends AbstractMain {
	public static void main(final String[] args) throws Exception {
		setLookAndFeel(getSystemLookAndFeelClassName());
		invokeLater(() -> {
			try {
				final FileManagerUI frame = new FileManagerUI(torrentCopier, managerService);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
	}
}
