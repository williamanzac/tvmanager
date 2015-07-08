package com.wing.database.model;

import java.io.File;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuration {
	@XmlElement
	public File torrentDestination;
	@XmlElement
	public String torrentUsername;
	@XmlElement
	public String torrentPassword;
	@XmlElement
	public File showDestination;
}
