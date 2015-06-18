package com.wing.database.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "episode")
public class Episode implements Comparable<Episode> {
	private int number;
	private int season;
	private int epnum;
	private Date airdate;
	private String link;
	private String title;
	private EpisodeState state;
	private String torrentHash;

	// <epnum>1</epnum>
	// <seasonnum>01</seasonnum>
	// <prodnum>4V01</prodnum>
	// <airdate>1997-03-10</airdate>
	// <link>http://www.tvrage.com/Buffy_The_Vampire_Slayer/episodes/28077</link>
	// <title>Welcome to the Hellmouth (1)</title>

	@XmlElement
	public int getNumber() {
		return number;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	@XmlElement
	public int getSeason() {
		return season;
	}

	public void setSeason(final int season) {
		this.season = season;
	}

	@XmlElement
	public Date getAirdate() {
		return airdate;
	}

	public void setAirdate(final Date airdate) {
		this.airdate = airdate;
	}

	@XmlElement
	public String getLink() {
		return link;
	}

	public void setLink(final String link) {
		this.link = link;
	}

	@XmlElement
	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public int compareTo(final Episode that) {
		final int compare = Integer.compare(epnum, that.epnum);
		return compare;
	}

	@XmlElement
	public int getEpnum() {
		return epnum;
	}

	public void setEpnum(final int epnum) {
		this.epnum = epnum;
	}

	@XmlElement
	public EpisodeState getState() {
		return state;
	}

	public void setState(final EpisodeState state) {
		this.state = state;
	}

	@XmlElement
	public String getTorrentHash() {
		return torrentHash;
	}

	public void setTorrentHash(final String torrentHash) {
		this.torrentHash = torrentHash;
	}
}