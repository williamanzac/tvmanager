package com.wing.search.service;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public abstract class HttpShowSearchService implements ShowSearchService {

	protected InputStream getResponse(final String url) throws Exception {
		final HttpClient client = new HttpClient();
		final GetMethod method = new GetMethod(url);
		client.executeMethod(method);
		return method.getResponseBodyAsStream();
	}
}
