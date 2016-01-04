package com.wing.provider.utorrent.downloader;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ccil.cowan.tagsoup.Parser;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.wing.database.model.Torrent;
import com.wing.manager.service.ManagerService;

@RunWith(MockitoJUnitRunner.class)
public class UTorrentDownloaderTest {

	@Spy
	@InjectMocks
	private UTorrentDownloader cut;

	@Mock
	private ManagerService managerService;

	@Test
	public void verifyParsingTorrentList() throws Exception {
		final String source = IOUtils.toString(getClass().getResourceAsStream("/list.json"));
		final JSONObject object = new JSONObject(source);
		final List<Torrent> list = cut.parseTorrentList(object);
		Assert.assertNotNull(list);
		final Torrent torrent = list.get(0);
		Assert.assertNotNull(torrent);
		Assert.assertEquals("7885CCA0B81F7BAEA3A02A4DC13E2A870AED4FBF", torrent.getHash());
	}

	@Test
	public void verifyParsingToken() throws Exception {
		final SAXReader htmlReader = new SAXReader(new Parser());
		final Document document = htmlReader.read(getClass().getResourceAsStream("/token.html"));
		final String token = cut.parseToken(document);
		Assert.assertEquals("CgQLB9D4Jh1XXBkeNdyqgi68J__gvSvrKUIPemgB_-LBh7MZSD_wMT8uh1UAAAAA", token);
	}
}
