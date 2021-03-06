package com.wing.file.manager.ui;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.wing.database.model.Configuration;
import com.wing.file.manager.ui.components.FileTaskListModel;
import com.wing.file.manager.ui.components.FileTaskListRenderer;
import com.wing.file.manager.ui.components.UIButton;
import com.wing.manager.service.ManagerService;
import com.wing.torrent.copier.CopyTask;
import com.wing.torrent.copier.DeleteTask;
import com.wing.torrent.copier.FileManager;
import com.wing.torrent.copier.FileTask;
import com.wing.torrent.copier.MoveTask;

public class FileManagerUI extends JFrame {
	private static final long serialVersionUID = 3341828669621715070L;

	private JTextField sourceField;
	private JTextField targetField;
	private JList<FileTask> actionList;
	private FileTaskListModel listModel;

	private UIButton copyButton;
	private UIButton moveButton;
	private UIButton deleteButton;

	private final FileManager torrentCopier;
	private final ManagerService managerService;

	private final class FileLocationListener implements DocumentListener {
		@Override
		public void removeUpdate(final DocumentEvent e) {
			update();
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			update();
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
			update();
		}

		private void update() {
			final boolean source = isNotBlank(sourceField.getText());
			final boolean target = isNotBlank(targetField.getText());
			copyButton.setEnabled(source && target);
			moveButton.setEnabled(source && target);
			deleteButton.setEnabled(source && !target);
		}
	}

	private class ButtonActions implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final String command = e.getActionCommand();
			switch (command) {
			case "browseSource":
			case "browseTarget":
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				try {
					final Configuration configuration = managerService.loadConfiguration();
					if (command.equals("browseSource")) {
						chooser.setCurrentDirectory(configuration.torrentDestination);
					} else {
						chooser.setCurrentDirectory(configuration.showDestination);
					}
				} catch (final Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				chooser.showOpenDialog(FileManagerUI.this);
				final File target = chooser.getSelectedFile();
				if (target != null && target.exists()) {
					if (command.equals("browseSource")) {
						sourceField.setText(target.getAbsolutePath());
					} else {
						targetField.setText(target.getAbsolutePath());
					}
				}
				break;
			case "copyFile":
			case "moveFile":
				try {
					final File source = new File(sourceField.getText());
					final File dest = new File(targetField.getText());
					if ("moveFile".equals(command)) {
						System.out.println("moving: " + source + " to " + dest);
						addMoveTask(source, dest);
					} else {
						System.out.println("copying: " + source + " to " + dest);
						addCopyTask(source, dest);
					}
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
				break;
			case "deleteFile":
				try {
					final File file = new File(sourceField.getText());
					System.out.println("deleting: " + file);
					addDeleteTask(file);
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
				break;
			}
		}
	}

	private class WindowActions extends WindowAdapter {
		@Override
		public void windowClosing(final WindowEvent e) {
			try {
				torrentCopier.stop();
			} catch (final Exception e1) {
				e1.printStackTrace();
			}
			super.windowClosing(e);
		}
	}

	/**
	 * Create the dialog.
	 * 
	 * @throws Exception
	 */
	public FileManagerUI(final FileManager torrentCopier, final ManagerService managerService) throws Exception {
		this.torrentCopier = torrentCopier;
		this.managerService = managerService;
		setTitle("File Actions");
		setBounds(100, 100, 700, 300);
		getContentPane().setLayout(new BorderLayout());
		addWindowListener(new WindowActions());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(new ImageIcon(getClass().getResource("main.png")).getImage());
		final FileLocationListener fileLocationListener = new FileLocationListener();
		final ButtonActions buttonActions = new ButtonActions();
		{
			final JToolBar toolBar = new JToolBar();
			toolBar.setFloatable(false);
			getContentPane().add(toolBar, BorderLayout.NORTH);
			{
				sourceField = new JTextField();
				sourceField.setToolTipText("Source File Location");
				toolBar.add(sourceField);
				sourceField.setColumns(10);
				sourceField.getDocument().addDocumentListener(fileLocationListener);
			}
			{
				final UIButton sourceButton = new UIButton("../browse.png");
				sourceButton.setActionCommand("browseSource");
				sourceButton.setToolTipText("Browse for source file");
				sourceButton.addActionListener(buttonActions);
				toolBar.add(sourceButton);
			}
			toolBar.addSeparator();
			{
				targetField = new JTextField();
				targetField.setToolTipText("Target Location");
				toolBar.add(targetField);
				targetField.setColumns(10);
				targetField.getDocument().addDocumentListener(fileLocationListener);
			}
			{
				final UIButton targetButton = new UIButton("../browse.png");
				targetButton.setActionCommand("browseTarget");
				targetButton.setToolTipText("Browse for target location");
				targetButton.addActionListener(buttonActions);
				toolBar.add(targetButton);
			}
			toolBar.addSeparator();
			{
				copyButton = new UIButton("../copy.png");
				copyButton.setActionCommand("copyFile");
				copyButton.addActionListener(buttonActions);
				copyButton.setEnabled(false);
				toolBar.add(copyButton);
			}
			{
				moveButton = new UIButton("../move.png");
				moveButton.setActionCommand("moveFile");
				moveButton.addActionListener(buttonActions);
				moveButton.setEnabled(false);
				toolBar.add(moveButton);
			}
			{
				deleteButton = new UIButton("../delete.png");
				deleteButton.setActionCommand("deleteFile");
				deleteButton.addActionListener(buttonActions);
				deleteButton.setEnabled(false);
				toolBar.add(deleteButton);
			}
		}
		{
			{
				listModel = new FileTaskListModel(torrentCopier);
				actionList = new JList<>(listModel);
				actionList.setCellRenderer(new FileTaskListRenderer());
				actionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				// actionList.setEnabled(false);
				actionList.setBackground(getContentPane().getBackground());
				actionList.setForeground(getContentPane().getForeground());
			}
			final JScrollPane scrollPane = new JScrollPane(actionList);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			getContentPane().add(scrollPane, BorderLayout.CENTER);
		}
		torrentCopier.start();
	}

	public void addCopyTask(final File source, final File target) throws Exception {
		final CopyTask task = new CopyTask(source, target);
		listModel.add(task);
	}

	public void addMoveTask(final File source, final File target) throws Exception {
		final MoveTask task = new MoveTask(source, target);
		listModel.add(task);
	}

	public void addDeleteTask(final File file) throws Exception {
		final DeleteTask task = new DeleteTask(file);
		listModel.add(task);
	}
}
