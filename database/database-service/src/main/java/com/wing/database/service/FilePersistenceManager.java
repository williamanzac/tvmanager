package com.wing.database.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.PathParam;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.wing.database.api.PersistenceManager;

public abstract class FilePersistenceManager<T> implements PersistenceManager<T> {

	abstract protected Class<T> forClass();

	protected File baseDir() {
		final File dir = new File(forClass().getSimpleName().toLowerCase());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T retrieve(@PathParam("key") final String key) throws Exception {
		final File file = new File(baseDir(), key + ".xml");
		if (file.exists()) {
			final JAXBContext ctx = JAXBContext.newInstance(new Class[] { forClass() });
			final Unmarshaller um = ctx.createUnmarshaller();
			return (T) um.unmarshal(file);
		}
		return null;
	}

	@Override
	public void save(@PathParam("key") final String key, final T value) throws Exception {
		final JAXBContext ctx = JAXBContext.newInstance(new Class[] { forClass() });
		final Marshaller um = ctx.createMarshaller();
		um.marshal(value, new File(baseDir(), key + ".xml"));
	}

	@Override
	public List<T> list() throws Exception {
		final File baseDir = baseDir();
		final String[] files = baseDir.list((dir, name) -> name.endsWith(".xml"));
		final List<T> list = new ArrayList<>();
		if (files != null && files.length > 0) {
			for (final String name : files) {
				list.add(retrieve(name.substring(0, name.indexOf("."))));
			}
		}
		return list;
	}

	@Override
	public void delete(final String key) {
		final File baseDir = baseDir();
		final File file = new File(baseDir, key + ".xml");
		file.delete();
	}
}
