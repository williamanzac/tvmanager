package com.wing.configuration.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.wing.database.model.Configuration;
import com.wing.manager.service.ManagerService;

public class ConfigurationDialog extends JDialog {
	private static final long serialVersionUID = -28891122052152460L;
	private final JTextField txtTorrentDestination;
	private final JTextField txtTorrentUsername;
	private final JPasswordField txtTorrentPassword;
	private final JTextField txtShowDestination;

	private final ManagerService managerService;

	private final Configuration configuration;

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final String command = event.getActionCommand();
			switch (command) {
			case "OK":
				configuration.torrentDestination = new File(txtTorrentDestination.getText());
				configuration.torrentUsername = txtTorrentUsername.getText();
				configuration.torrentPassword = new String(txtTorrentPassword.getPassword());
				configuration.showDestination = new File(txtShowDestination.getText());
				try {
					managerService.saveConfiguration(configuration);
				} catch (final Exception e) {
					e.printStackTrace();
				}
				setVisible(false);
				break;
			case "Cancel":
				setVisible(false);
				break;
			case "browseDestination":
			case "browseShowDestination":
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.showOpenDialog(ConfigurationDialog.this);
				final File targetDir = chooser.getSelectedFile();
				if (targetDir != null && targetDir.isDirectory()) {
					if (command.equals("browseDestination")) {
						txtTorrentDestination.setText(targetDir.getAbsolutePath());
					} else {
						txtShowDestination.setText(targetDir.getAbsolutePath());
					}
				}
				break;
			}
		}
	}

	/**
	 * Create the dialog.
	 *
	 * @throws IOException
	 */
	public ConfigurationDialog(final ManagerService managerService) throws Exception {
		this.managerService = managerService;
		configuration = managerService.loadConfiguration();
		setTitle("Configuration");
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		final ButtonActions buttonActions = new ButtonActions();

		final JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		final JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(buttonActions);
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		final JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(buttonActions);
		buttonPane.add(cancelButton);

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		final JPanel torrentPanel = new JPanel();
		tabbedPane.addTab("Torrent", null, torrentPanel, null);
		torrentPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.BUTTON_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		txtTorrentDestination = new JTextField();

		final JLabel lblTorrentDestination = new JLabel("Torrent Destination:");
		lblTorrentDestination.setLabelFor(txtTorrentDestination);
		torrentPanel.add(lblTorrentDestination, "2, 2, right, default");

		torrentPanel.add(txtTorrentDestination, "4, 2, fill, default");
		txtTorrentDestination.setColumns(10);
		File destination = configuration.torrentDestination;
		if (destination != null) {
			txtTorrentDestination.setText(destination.getAbsolutePath());
		}

		final JButton btnTorrentDestination = new JButton("Browse");
		btnTorrentDestination.setActionCommand("browseDestination");
		btnTorrentDestination.addActionListener(buttonActions);
		torrentPanel.add(btnTorrentDestination, "6, 2");

		txtTorrentUsername = new JTextField();

		final JLabel lblTorrentUsername = new JLabel("Torrent Username:");
		lblTorrentUsername.setLabelFor(txtTorrentUsername);
		torrentPanel.add(lblTorrentUsername, "2, 4, right, default");

		torrentPanel.add(txtTorrentUsername, "4, 4, fill, default");
		txtTorrentUsername.setColumns(10);
		txtTorrentUsername.setText(configuration.torrentUsername);

		txtTorrentPassword = new JPasswordField();

		final JLabel lblTorrentPassword = new JLabel("Torrent Password:");
		lblTorrentPassword.setLabelFor(txtTorrentPassword);
		torrentPanel.add(lblTorrentPassword, "2, 6, right, default");

		torrentPanel.add(txtTorrentPassword, "4, 6, fill, default");
		txtTorrentPassword.setColumns(10);
		txtTorrentPassword.setText(configuration.torrentPassword);

		final JPanel showPanel = new JPanel();
		tabbedPane.addTab("Show", null, showPanel, null);
		showPanel.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.BUTTON_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		txtShowDestination = new JTextField();

		final JLabel lblShowDestination = new JLabel("Show Destination:");
		lblShowDestination.setLabelFor(txtShowDestination);
		showPanel.add(lblShowDestination, "2, 2, right, default");

		showPanel.add(txtShowDestination, "4, 2, fill, default");
		txtShowDestination.setColumns(10);
		destination = configuration.showDestination;
		if (destination != null) {
			txtShowDestination.setText(destination.getAbsolutePath());
		}

		final JButton btnShowDestination = new JButton("Browse");
		btnShowDestination.setActionCommand("browseShowDestination");
		btnShowDestination.addActionListener(buttonActions);
		showPanel.add(btnShowDestination, "6, 2");
	}
}
