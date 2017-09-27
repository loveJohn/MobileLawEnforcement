package com.chinadci.mel.mleo.ui.fragments.data.model;

public class Poi {
	private String NAME;
	private double LON;
	private double LAT;
	private double CLASS;
	private String GBCODE ;
	private double ID;
	private String SubDistricts;
	
	public Poi(String nAME, double lON, double lAT, double cLASS,
			String gBCODE, double iD, String subDistricts) {
		super();
		NAME = nAME;
		LON = lON;
		LAT = lAT;
		CLASS = cLASS;
		GBCODE = gBCODE;
		ID = iD;
		SubDistricts = subDistricts;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public double getLON() {
		return LON;
	}
	public void setLON(double lON) {
		LON = lON;
	}
	public double getLAT() {
		return LAT;
	}
	public void setLAT(double lAT) {
		LAT = lAT;
	}
	public double getCLASS() {
		return CLASS;
	}
	public void setCLASS(double cLASS) {
		CLASS = cLASS;
	}
	public String getGBCODE() {
		return GBCODE;
	}
	public void setGBCODE(String gBCODE) {
		GBCODE = gBCODE;
	}
	public double getID() {
		return ID;
	}
	public void setID(double iD) {
		ID = iD;
	}
	public String getSubDistricts() {
		return SubDistricts;
	}
	public void setSubDistricts(String subDistricts) {
		SubDistricts = subDistricts;
	}
	
	
}
