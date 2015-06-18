package com.wing.provider.tvrage.searcher;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Before;
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
		final List<Show> list = cut.searchShow("buffy");
		Assert.assertNotNull(list);
	}

	@Test
	public void parseXML() throws Exception {
		final SAXReader reader = new SAXReader();
		final Document document = reader.read(getClass().getResourceAsStream("/search.xml"));
		final List<Show> list = cut.processShowXML(document);
		Assert.assertNotNull(list);
	}

}
