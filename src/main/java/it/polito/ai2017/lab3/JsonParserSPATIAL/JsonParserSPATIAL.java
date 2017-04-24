package it.polito.ai2017.lab3.JsonParserSPATIAL;

import java.sql.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

 
public class JsonParserSPATIAL {
    public static void main(String[] args )
    {
    	String jsonFilename = null;
    	String schemaFilename = null;
    	try {
    		jsonFilename = args[0];
    		schemaFilename = args[1];
    		parseLines(jsonFilename,schemaFilename);
    	} catch (IndexOutOfBoundsException ioe) {
    		System.out.println("No Json File Provided. Exiting...");
    	} 
    	System.out.println( "All done!" );
    }
    
    public static void parseLines (String jsonFilename,String schemaFilename) {
        ObjectMapper mapper = new ObjectMapper();
        try {
			FileInputStream jsonInput = new FileInputStream(jsonFilename);
			System.out.println("Loading json file...");
			RootNode rootNode = mapper.readValue(jsonInput, RootNode.class);

			System.out.println("Loading lines...");
			List<Line> lines = rootNode.getLines();
			/*
			for (Line element : lines ) {
				System.out.println("current line ->"+element.getLine());
				System.out.println("Stops -> "+element.getStops());
			}
			*/
			
			System.out.println("Loading stops...");
			List<Stop> stops = rootNode.getStops();
			/*
			for (Stop element : stops ) {
				System.out.println("current stop ->"+element.getId());
				System.out.println("Lating -> "+element.getLatLng());
				System.out.println("Lines -> "+element.getLines());
			}
			*/
			System.out.println("Updateing database...");
			uploadDB(lines,stops,schemaFilename);
			
        } catch (IOException e) {
			System.out.println("[ERROR] - "+ jsonFilename+" is not a valid file");
			e.printStackTrace();
		}   
    }
    
    public static void uploadDB (List<Line> lines, List<Stop> stops,String schemaFilename) {
    	String connectionUrl = "jdbc:postgresql://localhost:5432/trasporti";
    	try {
    		Class.forName("org.postgresql.Driver");
    		Connection conn = DriverManager.getConnection(connectionUrl,"postgres","ai-user-password"); 
    		
    		Statement stmt = conn.createStatement();
    		
    		/******************* ONLY FOR DEBUGGING PURPOSES **/
    		stmt.executeUpdate("drop table if exists buslinestop");
    		stmt.executeUpdate("drop table if exists busstop");
    		stmt.executeUpdate("drop table if exists busline");
    		/**************************************************/
    		
    		/* Insert schema as a text file */
    		/* CHANGE HERE */
    		FileReader fr = new FileReader(schemaFilename);
    		
    		BufferedReader br = new BufferedReader (fr);
    		String tmp = null;
    		String content = null;
    		while ((tmp = br.readLine())!=null) {
    			content=content+tmp;
    		} 
    		content = content.substring(4); // ignore null
    		br.close();
    		stmt.executeUpdate(content);
    		stmt.close();
    		
    		conn.setAutoCommit(false);
    		
    		// insert bus lines
    		// It can be optimized by doing everything in a single statement
    		PreparedStatement ps1 = null;
    		PreparedStatement ps2 = null;
    		PreparedStatement ps3 = null;
    		
			try {
				// insert into busline
    			String query = "INSERT INTO BUSLINE(line,description) VALUES(?,?)";
    			ps1 = conn.prepareStatement(query);
    			
				System.out.println("Insert busline..");

	    		for (Line element : lines) {
	    			ps1.clearParameters();
	    			ps1.setString(1, element.getLine());
	    			ps1.setString(2, element.getDesc());
	    			ps1.execute();
	    		}
	    		
	    		// insert int busstop
    			query = "INSERT INTO BUSSTOP(id,name,latlng) VALUES(?,?,ST_GeographyFromText(?))";
    			ps2 = conn.prepareStatement(query);
    			
				System.out.println("Insert busstop..");

	    		for (Stop element : stops) {
	    			//String test = "ST_GeographyFromText('SRID=4326; POINT(45.0 7.0)')";
	    			ps2.clearParameters();
	    			ps2.setString(1, element.getId());
	    			ps2.setString(2, element.getName());
	    			ps2.setString(3, "SRID=4326; POINT("+element.getLatLng().get(0).toString()+" "+element.getLatLng().get(1).toString()+")");
	    			ps2.execute();
	    		}
	    		
	    		// insert into buslinestop
				query = "INSERT INTO BUSLINESTOP(stopid,lineid,sequencenumber) VALUES(?,?,?)";
				ps3 = conn.prepareStatement(query);
				
				System.out.println("Insert linestop..");
	    		for (Line element : lines) {
	    			int i = 0;
	    			for (String element2 : element.getStops()) {
	    				i++;
	    				ps3.clearParameters();
	    				ps3.setString(1, element2);
	    				ps3.setString(2,element.getLine());
	    				ps3.setInt(3,i);
	    				ps3.execute();
	    				
	    			}
	    		}
	    		
	    		ps1.close();
	    		ps2.close();
	    		ps3.close();
	    		System.out.println("Commit...");
	    		conn.commit();
	    		
	    		/*
	    		Statement s = conn.createStatement();
	    		
				System.out.println("BUS LINE LIST:" );
	    		ResultSet rs1 = s.executeQuery("select * from busline");
	    		while(rs1.next()) System.out.println(rs1.getString(1)+" : "+rs1.getString(2));
	    		System.out.println("-----------");
	    		
	    		System.out.println("BUS STOP LIST:" );
	    		ResultSet rs2 = s.executeQuery("select id,name,ST_AsText(latlng) from busstop");
	    		while(rs2.next())
	        		System.out.println(rs2.getString(1)+" : "+rs2.getString(2)+" : "+rs2.getString(3));
	    		System.out.println("-----------");
	    		
	    		System.out.println("BUS LINESTOP LIST:" );
	    		ResultSet rs3 = s.executeQuery("select * from buslinestop");
	    		while(rs3.next()) System.out.println(rs3.getString(1)+" : "+rs3.getString(2));
	    		System.out.println("-----------");
	    		
	    		s.close();*/
			}
			catch (SQLException se)
			{
				se.printStackTrace();
				conn.rollback();
				
			} finally {
				if(ps1!=null) ps1.close();
				if(ps2!=null) ps2.close();
				if(ps3!=null) ps3.close();
				
				if(conn!=null) conn.close();
			}
    	} catch (Exception e) {
    		
    		e.printStackTrace();
    		System.exit(1);
    	}
    }
}
