package it.polito.ai2017.lab3.JsonParserSPATIAL;

import java.util.ArrayList;
import java.util.List;

class Line {
	private String line;
	private String desc;
	private List<String> stops;
	
	Line () {
		stops = new ArrayList<String>();
	}
	
	public String getLine() {
		return line;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public List<String> getStops () {
		return stops;
	}
	
	public void setName (String line) {
		this.line=line;
	}
	
	public void setDesc (String desc) {
		this.desc=desc;
	}
	
	public void setStops (List<String> stops) {
		this.stops = stops;
	}
	
}