package com.example.testapp;

import java.io.Serializable;

public class MarkerInfo implements Serializable {
	private static final long serialVersionUID = 8411335190768743988L;
	int id;
	String title;
	String address;
	String desc;
	double lat;
	double lng;
	String cat;
}
