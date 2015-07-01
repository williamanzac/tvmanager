package com.wing.search.ui;

import java.awt.BorderLayout;
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

import com.wing.database.model.Show;
import com.wing.manager.service.ManagerService;
import com.wing.search.ui.model.ShowTableModel;

public class SearchDialog extends JDialog {

	private static final long serialVersionUID = 117691005972291282L;

	private JTable table;
	JTextField textField;
	private JLabel lblNewLabel;
	private ShowTableModel tableModel = new ShowTableModel();
	JButton btnNewButton;

	private final ManagerService managerService;
	private Show selectedShow;

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final String command = event.getActionCommand();
			switch (command) {
			case "OK":
				selectedShow = tableModel.getShows().get(table.getSelectedRow());
				setVisible(false);
				break;
			case "Cancel":
				selectedShow = null;
				setVisible(false);
				break;
			case "Search":
				try {
					final String show = textField.getText();
					final List<Show> shows = managerService.searchShow(show);
					tableModel.setShows(shows);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Create the dialog.
	 */
	public SearchDialog(final ManagerService managerService) {
		this.managerService = managerService;
		final ButtonActions buttonActions = new ButtonActions();
		setBounds(100, 100, 450, 300);
		setModal(true);
		setTitle("TV Show Searcher");
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
