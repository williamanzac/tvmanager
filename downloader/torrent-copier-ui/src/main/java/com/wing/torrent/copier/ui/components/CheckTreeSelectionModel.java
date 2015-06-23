package com.wing.torrent.copier.ui.components;

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class CheckTreeSelectionModel extends DefaultTreeSelectionModel {
	private static final long serialVersionUID = -7151043450209964182L;

	private final TreeModel model;

	public CheckTreeSelectionModel(TreeModel model) {
		this.model = model;
		setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	}

	// tests whether there is any unselected node in the subtree of given path
	public boolean isPartiallySelected(TreePath path) {
		if (isPathSelected(path, true)) {
			return false;
		}
		final TreePath[] selectionPaths = getSelectionPaths();
		if (selectionPaths == null) {
			return false;
		}
		for (final TreePath selectionPath : selectionPaths) {
			if (isDescendant(selectionPath, path)) {
				return true;
			}
		}
		return false;
	}

	// tells whether given path is selected.
	// if dig is true, then a path is assumed to be selected, if
	// one of its ancestor is selected.
	public boolean isPathSelected(TreePath path, boolean dig) {
		if (!dig) {
			return super.isPathSelected(path);
		}
		while (path != null && !super.isPathSelected(path)) {
			path = path.getParentPath();
		}
		return path != null;
	}

	// is path1 descendant of path2
	private boolean isDescendant(TreePath path1, TreePath path2) {
		final Object obj1[] = path1.getPath();
		final Object obj2[] = path2.getPath();
		for (int i = 0; i < obj2.length; i++) {
			if (obj1[i] != obj2[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void setSelectionPaths(TreePath[] pPaths) {
		throw new UnsupportedOperationException("not implemented yet!!!");
	}

	@Override
	public void addSelectionPaths(TreePath[] paths) {
		// unselect all descendants of paths[]
		for (final TreePath path : paths) {
			final TreePath[] selectionPaths = getSelectionPaths();
			if (selectionPaths == null) {
				break;
			}
			final ArrayList<TreePath> toBeRemoved = new ArrayList<>();
			for (final TreePath selectionPath : selectionPaths) {
				if (isDescendant(selectionPath, path)) {
					toBeRemoved.add(selectionPath);
				}
			}
			super.removeSelectionPaths(toBeRemoved.toArray(new TreePath[0]));
		}

		// if all siblings are selected then unselect them and select parent recursively
		// otherwize just select that path.
		for (final TreePath path2 : paths) {
			TreePath path = path2;
			TreePath temp = null;
			while (areSiblingsSelected(path)) {
				temp = path;
				if (path.getParentPath() == null) {
					break;
				}
				path = path.getParentPath();
			}
			if (temp != null) {
				if (temp.getParentPath() != null) {
					addSelectionPath(temp.getParentPath());
				} else {
					if (!isSelectionEmpty()) {
						removeSelectionPaths(getSelectionPaths());
					}
					super.addSelectionPaths(new TreePath[] { temp });
				}
			} else {
				super.addSelectionPaths(new TreePath[] { path });
			}
		}
	}

	// tells whether all siblings of given path are selected.
	private boolean areSiblingsSelected(TreePath path) {
		final TreePath parent = path.getParentPath();
		if (parent == null) {
			return true;
		}
		final Object node = path.getLastPathComponent();
		final Object parentNode = parent.getLastPathComponent();

		final int childCount = model.getChildCount(parentNode);
		for (int i = 0; i < childCount; i++) {
			final Object childNode = model.getChild(parentNode, i);
			if (childNode == node) {
				continue;
			}
			if (!isPathSelected(parent.pathByAddingChild(childNode))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void removeSelectionPaths(TreePath[] paths) {
		for (final TreePath path : paths) {
			if (path.getPathCount() == 1) {
				super.removeSelectionPaths(new TreePath[] { path });
			} else {
				toggleRemoveSelection(path);
			}
		}
	}

	// if any ancestor node of given path is selected then unselect it
	// and selection all its descendants except given path and descendants.
	// otherwise just unselect the given path
	private void toggleRemoveSelection(TreePath path) {
		final Stack<TreePath> stack = new Stack<>();
		TreePath parent = path.getParentPath();
		while (parent != null && !isPathSelected(parent)) {
			stack.push(parent);
			parent = parent.getParentPath();
		}
		if (parent != null) {
			stack.push(parent);
		} else {
			super.removeSelectionPaths(new TreePath[] { path });
			return;
		}

		while (!stack.isEmpty()) {
			final TreePath temp = stack.pop();
			final TreePath peekPath = stack.isEmpty() ? path : (TreePath) stack.peek();
			final Object node = temp.getLastPathComponent();
			final Object peekNode = peekPath.getLastPathComponent();
			final int childCount = model.getChildCount(node);
			for (int i = 0; i < childCount; i++) {
				final Object childNode = model.getChild(node, i);
				if (childNode != peekNode) {
					super.addSelectionPaths(new TreePath[] { temp.pathByAddingChild(childNode) });
				}
			}
		}
		super.removeSelectionPaths(new TreePath[] { parent });
	}
}