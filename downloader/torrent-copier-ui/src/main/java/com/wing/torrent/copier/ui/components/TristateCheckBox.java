package com.wing.torrent.copier.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;

public class TristateCheckBox extends JCheckBox {
	private static final long serialVersionUID = 3023815086085033163L;

	/** This is a type-safe enumerated type */
	public static class State {
		private State() {
		}
	}

	public static final State NOT_SELECTED = new State();
	public static final State SELECTED = new State();
	public static final State DONT_CARE = new State();

	private final TristateDecorator model;

	public TristateCheckBox(final String text, final Icon icon, final State initial) {
		super(text, icon);
		// Add a listener for when the mouse is pressed
		super.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				grabFocus();
				model.nextState();
			}
		});
		// Reset the keyboard action map
		final ActionMap map = new ActionMapUIResource();
		map.put("pressed", new AbstractAction() {
			private static final long serialVersionUID = 9153158302425105578L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				grabFocus();
				model.nextState();
			}
		});
		map.put("released", null);
		SwingUtilities.replaceUIActionMap(this, map);
		// set the model to the adapted model
		model = new TristateDecorator(getModel());
		setModel(model);
		setState(initial);
	}

	public TristateCheckBox(final String text, final State initial) {
		this(text, null, initial);
	}

	public TristateCheckBox(final String text) {
		this(text, DONT_CARE);
	}

	public TristateCheckBox() {
		this(null);
	}

	/** No one may add mouse listeners, not even Swing! */
	@Override
	public void addMouseListener(final MouseListener l) {
	}

	/**
	 * Set the new state to either SELECTED, NOT_SELECTED or DONT_CARE. If state == null, it is treated as DONT_CARE.
	 */
	public void setState(final State state) {
		model.setState(state);
	}

	/**
	 * Return the current state, which is determined by the selection status of the model.
	 */
	public State getState() {
		return model.getState();
	}

	@Override
	public void setSelected(final boolean b) {
		if (b) {
			setState(SELECTED);
		} else {
			setState(NOT_SELECTED);
		}
	}

	/**
	 * Exactly which Design Pattern is this? Is it an Adapter, a Proxy or a Decorator? In this case, my vote lies with
	 * the Decorator, because we are extending functionality and "decorating" the original model with a more powerful
	 * model.
	 */
	private class TristateDecorator implements ButtonModel {
		private final ButtonModel other;

		private TristateDecorator(final ButtonModel other) {
			this.other = other;
		}

		private void setState(final State state) {
			if (state == NOT_SELECTED) {
				other.setArmed(false);
				setPressed(false);
				setSelected(false);
			} else if (state == SELECTED) {
				other.setArmed(false);
				setPressed(false);
				setSelected(true);
			} else { // either "null" or DONT_CARE
				other.setArmed(true);
				setPressed(true);
				setSelected(true);
			}
		}

		/**
		 * The current state is embedded in the selection / armed state of the model.
		 *
		 * We return the SELECTED state when the checkbox is selected but not armed, DONT_CARE state when the checkbox
		 * is selected and armed (grey) and NOT_SELECTED when the checkbox is deselected.
		 */
		private State getState() {
			if (isSelected() && !isArmed()) {
				// normal black tick
				return SELECTED;
			} else if (isSelected() && isArmed()) {
				// don't care grey tick
				return DONT_CARE;
			} else {
				// normal deselected
				return NOT_SELECTED;
			}
		}

		/** We rotate between NOT_SELECTED, SELECTED and DONT_CARE. */
		private void nextState() {
			final State current = getState();
			if (current == NOT_SELECTED) {
				setState(SELECTED);
			} else if (current == SELECTED) {
				setState(DONT_CARE);
			} else if (current == DONT_CARE) {
				setState(NOT_SELECTED);
			}
		}

		/** Filter: No one may change the armed status except us. */
		@Override
		public void setArmed(final boolean b) {
		}

		/**
		 * We disable focusing on the component when it is not enabled.
		 */
		@Override
		public void setEnabled(final boolean b) {
			setFocusable(b);
			other.setEnabled(b);
		}

		/**
		 * All these methods simply delegate to the "other" model that is being decorated.
		 */
		@Override
		public boolean isArmed() {
			return other.isArmed();
		}

		@Override
		public boolean isSelected() {
			return other.isSelected();
		}

		@Override
		public boolean isEnabled() {
			return other.isEnabled();
		}

		@Override
		public boolean isPressed() {
			return other.isPressed();
		}

		@Override
		public boolean isRollover() {
			return other.isRollover();
		}

		@Override
		public void setSelected(final boolean b) {
			other.setSelected(b);
		}

		@Override
		public void setPressed(final boolean b) {
			other.setPressed(b);
		}

		@Override
		public void setRollover(final boolean b) {
			other.setRollover(b);
		}

		@Override
		public void setMnemonic(final int key) {
			other.setMnemonic(key);
		}

		@Override
		public int getMnemonic() {
			return other.getMnemonic();
		}

		@Override
		public void setActionCommand(final String s) {
			other.setActionCommand(s);
		}

		@Override
		public String getActionCommand() {
			return other.getActionCommand();
		}

		@Override
		public void setGroup(final ButtonGroup group) {
			other.setGroup(group);
		}

		@Override
		public void addActionListener(final ActionListener l) {
			other.addActionListener(l);
		}

		@Override
		public void removeActionListener(final ActionListener l) {
			other.removeActionListener(l);
		}

		@Override
		public void addItemListener(final ItemListener l) {
			other.addItemListener(l);
		}

		@Override
		public void removeItemListener(final ItemListener l) {
			other.removeItemListener(l);
		}

		@Override
		public void addChangeListener(final ChangeListener l) {
			other.addChangeListener(l);
		}

		@Override
		public void removeChangeListener(final ChangeListener l) {
			other.removeChangeListener(l);
		}

		@Override
		public Object[] getSelectedObjects() {
			return other.getSelectedObjects();
		}
	}
}