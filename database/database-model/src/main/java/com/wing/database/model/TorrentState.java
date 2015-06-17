package com.wing.database.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum TorrentState {
	QUEUED, VALIDATING,DOWNLOADING, ERROR, DONE;
}