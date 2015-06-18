package com.wing.torrent.searcher;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import com.wing.database.model.Torrent;
import com.wing.manager.service.ManagerService;
import com.wing.torrent.searcher.components.TorrentTableModel;

public class SearchDialog extends JDialog {

	private static final long serialVersionUID = -1547848258879068521L;

	private JTable table;
	private final ManagerService managerService;
	private TorrentTableModel tableModel;

	private Torrent selectedTorrent;

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final String command = event.getActionCommand();
			if ("OK".equals(command)) {
				selectedTorrent = tableModel.getTorrents().get(table.getSelectedRow());
				setVisible(false);
			} else if ("Cancel".equals(command)) {
				selectedTorrent = null;
				setVisible(false);
			}
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		try {
			final SearchDialog dialog = new SearchDialog(null);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SearchDialog(final ManagerService managerService) {
		this.managerService = managerService;
		setModal(true);
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
