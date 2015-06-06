package com.wing.provider.priatebay.proxy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ccil.cowan.tagsoup.Parser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ProxyService {
	private static final String proxybayURL = "http://proxybay.github.io";

	private final SAXReader xmlReader = new SAXReader();
	private final SAXReader htmlReader = new SAXReader(new Parser());

	protected InputStream getResponse(final String url) throws Exception {
		final HttpClient client = new HttpClient();
		final GetMethod method = new GetMethod(url);
		// method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		client.executeMethod(method);
		return method.getResponseBodyAsStream();
	}

	public List<String> listProxies() throws Exception {
		final InputStream response = getResponse(proxybayURL);
		final Document document = htmlReader.read(response);
		return parseXML(document);
	}

	@SuppressWarnings("unchecked")
	private List<String> parseXML(final Document document) {
		//		System.out.println(document.asXML());
		final List<Element> linkNodes = document
				.selectNodes("//html:a[@shape='rect' and @class='t1' and @rel='nofollow']");
		final List<String> links = new ArrayList<>();
		for (final Element linkNode : linkNodes) {
			final String value = linkNode.attributeValue("href");
			links.add(value);
		}
		return links;
	}
}
