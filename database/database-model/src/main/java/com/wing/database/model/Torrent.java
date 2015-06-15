package com.wing.database.model;

import java.net.URL;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Torrent {
	private String title;
	private int seeds;
	private int leechers;
	private long size;
	private String hash;
	private URL url;
	private Date pubDate;
	private Set<String> categories;

	// <link>https://torrentproject.se/1d395843f46ec51fa1f23cf233f8e3fe75839d00/Bones-S10E20-720p-HDTV-X264-DIMENSION-rarbg-torrent.html</link>
	// <guid>https://torrentproject.se/1d395843f46ec51fa1f23cf233f8e3fe75839d00/Bones-S10E20-720p-HDTV-X264-DIMENSION-rarbg-torrent.html</guid>
	// <pubDate>Fri, 29 May 2015 05:01:05 +0000</pubDate>

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSeeds() {
		return seeds;
	}

	public void setSeeds(int seeds) {
		this.seeds = seeds;
	}

	public int getLeechers() {
		return leechers;
	}

	public void setLeechers(int leechers) {
		this.leechers = leechers;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public Set<String> getCategories() {
		return categories;
	}

	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}
}
