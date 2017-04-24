package it.polito.ai2017.lab3.JsonParserSPATIAL;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class RootNode {

	@JsonProperty("stops")
	private List<Stop> stops;
	@JsonProperty("lines")
	private List<Line> lines;
	
	public List<Line> getLines()
	{
		return lines;
	}
	
	public List<Stop> getStops()
	{
		return stops;
	}	
}
