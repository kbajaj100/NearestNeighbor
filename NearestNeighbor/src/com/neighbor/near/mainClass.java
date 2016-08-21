package com.neighbor.near;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class mainClass {

	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub

		Neighbor myneighbor = new Neighbor();
		
		myneighbor.createlist();
		
		int max = 4;
		
		//for (int i = 1; i <= max; ++i)
		//	myneighbor.create_nearest(i);
	
	
		myneighbor.setword("stop", "mail");
	
		myneighbor.getnearest();
		
		myneighbor.cleanup();
		
		System.out.println("Finished");
	}

	
	
}
