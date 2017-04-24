package it.polito.ai2017.lab3.JsonParserSPATIAL;

import java.util.ArrayList;
import java.util.List;

class Stop {
	private String id;
	private String name;
	private List<Double> latLng;
	private List<String> lines;
	
	Stop() {
		latLng = new ArrayList<Double>();
		lines = new ArrayList<String>();
	}
	
	public String getId (){
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Double> getLatLng() {
		return latLng;
	}
	
	public List<String> getLines() {
		return lines;
	}
	
	public void setId (String id) {
		this.id=id;
	} 
	
	public void setName(String name) {
		this.name=name;
	}
	
	public void setLatLng (List<Double> latLng) {
		this.latLng=latLng;
	}
	
	public void setLines (List<String> lines) {
		this.lines = lines;
	}
}