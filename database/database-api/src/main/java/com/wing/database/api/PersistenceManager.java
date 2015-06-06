package com.wing.database.api;

import java.util.List;

public interface PersistenceManager<T> {

	public T retrieve(final String key) throws Exception;

	public void save(final String key, final T value) throws Exception;

	public void delete(final String key);

	public List<T> list() throws Exception;
}
