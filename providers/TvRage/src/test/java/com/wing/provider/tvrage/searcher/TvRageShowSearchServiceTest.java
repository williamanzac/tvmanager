package com.wing.provider.tvrage.searcher;

import java.io.File;
import java.util.List;

import javax.swing.text.html.parser.Parser;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.wing.database.model.Show;

public class TvRageShowSearchServiceTest {
	private TvRageShowSearchService cut;

	@Before
	public void setup() {
		cut = new TvRageShowSearchService();
	}

	@Test
	public void getBuffy() throws Exception {
		List<Show> list = cut.searchShow("buffy");
		Assert.assertNotNull(list);
	}

	@Test
	public void parseXML() throws Exception {
		final SAXReader reader = new SAXReader();
		final Document document = reader.read(getClass().getResourceAsStream("/search.xml"));
		List<Show> list = cut.processShowXML(document);
		Assert.assertNotNull(list);
	}

}
