package com.wing.manager.ui.components;

import java.util.List;

import javax.swing.AbstractListModel;

import com.wing.database.model.Show;
import com.wing.manager.service.ManagerService;

public class ShowListModel extends AbstractListModel<Show> {

	private static final long serialVersionUID = 2409502622261843596L;

	private final ManagerService managerService;
	private final List<Show> list;

	public ShowListModel(final ManagerService managerService) throws Exception {
		this.managerService = managerService;
		list = managerService.listShows();
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public Show getElementAt(final int index) {
		return index < 0 ? null : list.get(index);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public void add(final Show show) throws Exception {
		final int i = list.size();
		list.add(show);
		managerService.saveShow(show);
		fireIntervalAdded(this, i, i);
	}

	public Show remove(final int index) throws Exception {
		final Show show = list.remove(index);
		managerService.removeShow(show);
		fireIntervalRemoved(this, index, index);
		return show;
	}
}
