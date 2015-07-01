package com.wing.torrent.searcher;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import com.wing.database.model.Torrent;
import com.wing.manager.service.ManagerService;
import com.wing.torrent.searcher.components.TorrentTableModel;

public class SearchDialog extends JDialog {

	private static final long serialVersionUID = -1547848258879068521L;

	private JTable table;
	private final ManagerService managerService;
	private TorrentTableModel tableModel;
	private JTextField textField;

	private Torrent selectedTorrent;

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final String command = event.getActionCommand();
			switch (command) {
			case "OK":
				selectedTorrent = tableModel.getTorrents().get(table.getSelectedRow());
				setVisible(false);
				break;
			case "Cancel":
				selectedTorrent = null;
				setVisible(false);
				break;
			case "Search":
				EventQueue.invokeLater(() -> {
					try {
						final List<Torrent> list = managerService.searchFor(textField.getText());
						tableModel.setTorrents(list);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				});
			}
		}
	}

	/**
	 * Create the dialog.
	 */
	public SearchDialog(final ManagerService managerService) {
		this.managerService = managerService;
		setModal(true);
		setTitle("Torrent Searcher");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		final ButtonActions buttonActions = new ButtonActions();
		{
			{
				tableModel = new TorrentTableModel();
				table = new JTable(tableModel);
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.setFillsViewportHeight(true);
			}
			final JScrollPane scrollPane = new JScrollPane(table);
			getContentPane().add(scrollPane);
		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(buttonActions);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				final JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(buttonActions);
				buttonPane.add(cancelButton);
			}
		}
		{
			final JToolBar toolBar = new JToolBar();
			toolBar.setFloatable(false);
			getContentPane().add(toolBar, BorderLayout.NORTH);
			final JLabel lblNewLabel = new JLabel("Name:");
			toolBar.add(lblNewLabel);
			textField = new JTextField();
			lblNewLabel.setLabelFor(textField);
			toolBar.add(textField);
			textField.setColumns(10);
			final JButton btnNewButton = new JButton("Search");
			btnNewButton.setActionCommand("Search");
			btnNewButton.addActionListener(buttonActions);
			toolBar.add(btnNewButton);

		}
	}

	public void searchFor(final String name, final int season, final int episode) throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				final List<Torrent> list = managerService.searchForEpisode(name, season, episode);
				tableModel.setTorrents(list);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
		setVisible(true);
	}

	public Torrent getSelectedTorrent() {
		return selectedTorrent;
	}
}
