package com.wing.manager.main;

import static java.awt.EventQueue.invokeLater;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

import javax.swing.JFrame;

import com.wing.manager.ui.ManagerWindow;

public class TVManagerMain extends AbstractMain {

	public static void main(final String[] args) throws Exception {
		setLookAndFeel(getSystemLookAndFeelClassName());
		invokeLater(() -> {
			try {
				final ManagerWindow frame = new ManagerWindow(managerService);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
	}
}
