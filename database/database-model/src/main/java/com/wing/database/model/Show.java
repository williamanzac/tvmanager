package com.wing.database.model;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "show")
public class Show {
	private int id;
	private String name;
	private String link;
	private String country;
	private String started;
	private String ended;
	private int seasons;
	private String status;
	private String classification;
	private List<String> genres = Collections.emptyList();

	// private List<Episode> episodeList = new ArrayList<>();

	// <showid>2930</showid>
	// <name>Buffy the Vampire Slayer</name>
	// <link>http://www.tvrage.com/Buffy_The_Vampire_Slayer</link>
	// <country>US</country>
	// <started>1997</started>
	// <ended>2003</ended>
	// <seasons>7</seasons>
	// <status>Ended</status>
	// <classification>Scripted</classification>
	// <genres><genre>Action</genre><genre>Adventure</genre><genre>Comedy</genre><genre>Drama</genre><genre>Horror/Supernatural</genre><genre>Mystery</genre><genre>Sci-Fi</genre></genres>

	@XmlElement(name = "showid")
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	@XmlElements({ @XmlElement(name = "name"), @XmlElement(name = "showname") })
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@XmlElement
	public String getLink() {
		return link;
	}

	public void setLink(final String link) {
		this.link = link;
	}

	@XmlElement
	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	@XmlElement
	public String getStarted() {
		return started;
	}

	public void setStarted(final String started) {
		this.started = started;
	}

	@XmlElement
	public String getEnded() {
		return ended;
	}

	public void setEnded(final String ended) {
		this.ended = ended;
	}

	@XmlElement
	public int getSeasons() {
		return seasons;
	}

	public void setSeasons(final int seasons) {
		this.seasons = seasons;
	}

	@XmlElement
	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	@XmlElement
	public String getClassification() {
		return classification;
	}

	public void setClassification(final String classification) {
		this.classification = classification;
	}

	@XmlElement(name = "genre")
	@XmlElementWrapper(name = "genres")
	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(final List<String> genres) {
		this.genres = genres;
	}

	//
	// @XmlTransient
	// public List<Episode> getEpisodeList() {
	// return episodeList;
	// }
	//
	// public void setEpisodeList(final List<Episode> episodeList) {
	// this.episodeList = episodeList;
	// }

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}