package com.wing.search.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import com.wing.database.model.Show;
import com.wing.search.service.ShowSearchService;
import com.wing.search.ui.model.ShowTableModel;

public class SearchDialog extends JDialog {

	private static final long serialVersionUID = 117691005972291282L;

	JTable table;
	JTextField textField;
	JLabel lblNewLabel;
	ShowTableModel tableModel = new ShowTableModel();
	JButton btnNewButton;

	private final ShowSearchService searchService;
	private Show selectedShow;

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final String command = event.getActionCommand();
			if ("OK".equals(command)) {
				selectedShow = tableModel.getShows()
						.get(table.getSelectedRow());
				SearchDialog.this.setVisible(false);
			} else if ("Cancel".equals(command)) {
				selectedShow = null;
				SearchDialog.this.setVisible(false);
			} else if ("Search".equals(command)) {
				try {
					tableModel.setShows(searchService.searchShow(textField
							.getText()));
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		try {
			final SearchDialog dialog = new SearchDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SearchDialog(final ShowSearchService searchService) {
		this.searchService = searchService;
		final ButtonActions buttonActions = new ButtonActions();
		setBounds(100, 100, 450, 300);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		final JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		lblNewLabel = new JLabel("Name:");
		toolBar.add(lblNewLabel);
		textField = new JTextField();
		lblNewLabel.setLabelFor(textField);
		toolBar.add(textField);
		textField.setColumns(10);
		btnNewButton = new JButton("Search");
		btnNewButton.setActionCommand("Search");
		btnNewButton.addActionListener(buttonActions);
		toolBar.add(btnNewButton);
		final JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(tableModel);
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
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
	}

	public Show getSelectedShow() {
		return selectedShow;
	}
}
