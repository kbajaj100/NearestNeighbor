package com.neighbor.near;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class mainClass {

	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub

		Neighbor myneighbor = new Neighbor();
		
		myneighbor.createlist();
		
		myneighbor.setword("fray", "bark");
	
		myneighbor.getnearest();
		
		myneighbor.cleanup();
		
		System.out.println("Finished");
	}

	
	
}
