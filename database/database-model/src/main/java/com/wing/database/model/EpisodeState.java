package com.wing.database.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum EpisodeState {
	QUEUED, DOWNLOADING, DOWNLOADED, COPYING, COPYIED, WATCHED;
}
