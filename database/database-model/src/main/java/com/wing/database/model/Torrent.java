package com.wing.database.model;

import java.net.URL;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
	private TorrentState state;
	private float percentComplete;

	// <link>https://torrentproject.se/1d395843f46ec51fa1f23cf233f8e3fe75839d00/Bones-S10E20-720p-HDTV-X264-DIMENSION-rarbg-torrent.html</link>
	// <guid>https://torrentproject.se/1d395843f46ec51fa1f23cf233f8e3fe75839d00/Bones-S10E20-720p-HDTV-X264-DIMENSION-rarbg-torrent.html</guid>
	// <pubDate>Fri, 29 May 2015 05:01:05 +0000</pubDate>

	@XmlElement
	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@XmlTransient
	public int getSeeds() {
		return seeds;
	}

	public void setSeeds(final int seeds) {
		this.seeds = seeds;
	}

	@XmlTransient
	public int getLeechers() {
		return leechers;
	}

	public void setLeechers(final int leechers) {
		this.leechers = leechers;
	}

	@XmlElement
	public long getSize() {
		return size;
	}

	public void setSize(final long size) {
		this.size = size;
	}

	@XmlElement
	public String getHash() {
		return hash;
	}

	public void setHash(final String hash) {
		this.hash = hash;
	}

	@XmlElement
	public URL getUrl() {
		return url;
	}

	public void setUrl(final URL url) {
		this.url = url;
	}

	@XmlElement
	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(final Date pubDate) {
		this.pubDate = pubDate;
	}

	@XmlElement
	public Set<String> getCategories() {
		return categories;
	}

	public void setCategories(final Set<String> categories) {
		this.categories = categories;
	}

	@XmlElement
	public TorrentState getState() {
		return state;
	}

	public void setState(final TorrentState state) {
		this.state = state;
	}

	@XmlElement
	public float getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(final float percentComplete) {
		this.percentComplete = percentComplete;
	}
}
