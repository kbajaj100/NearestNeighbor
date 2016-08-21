package com.neighbor.near;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import com.sun.rowset.CachedRowSetImpl;

public class Neighbor {

	private int wordmax;
	private String word;
	private String start_word;
	private String end_word;
	private int final_level;
	
	private DBConn myconn = new DBConn();
	
	private String dbUrl ="";
	private String SQL; 
	
	private WordArr[] mywordpatharr;
	
	public void createlist() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		
		initializedb();
		
	}

	private void initializedb() throws FileNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		
		myconn.setDBConn("C:/Props/Neighbor/DBprops.properties");
		
		SQL = "select count(word) count from dbo.wordlist";
		
		wordmax = myconn.execSQL_returnint(SQL);
		System.out.println("wordmax is: " + wordmax);
	}

	public void create_nearest(int i) {
		// TODO Auto-generated method stub
		
		determineSQL(i);		
		myconn.execSQL(SQL);
	}

	private void determineSQL(int i) {
		// TODO Auto-generated method stub
		
		SQL = "insert into dbo.nearest( " +
				"prim, neighbor, neighbor_score) " +
				"select a11.word, a12.word, " + i +
				"from wordlist a11 " +
				"join wordlist a12 on " +
				"(a11.first ";
		
		if (i == 1){
			SQL = SQL + "<> a12.first and " +
			"a11.second = a12.second and " +
			"a11.third = a12.third and " +
			"a11.fourth = a12.fourth " + 
			")" +
			"order by a11.word, a12.word";
		}
		else if (i == 2)
		{
			SQL = SQL + "= a12.first and " +
			"a11.second <> a12.second and " +
			"a11.third = a12.third and " +
			"a11.fourth = a12.fourth " + 
			")" +
			"order by a11.word, a12.word";
		}
		else if (i ==3){
			SQL = SQL + "= a12.first and " +
			"a11.second = a12.second and " +
			"a11.third <> a12.third and " +
			"a11.fourth = a12.fourth " + 
			")" +
			"order by a11.word, a12.word";
		}
		else
		{
			SQL = SQL + "= a12.first and " +
			"a11.second = a12.second and " +
			"a11.third = a12.third and " +
			"a11.fourth <> a12.fourth " + 
			")" +
			"order by a11.word, a12.word";
		}

	}

	public void getnearest() {
		// TODO Auto-generated method stub


		/*
		 * 1. Take the start word and find all nearest neighbors. store the neighbor words in a table
		 * 2. Is the nearest neighbor the target word
		 * 3. If not, take the neighbors and find the neighbors where the neighbor is not the father word
		 * 4. Is the nearest neighbor the target word
		 * 5. Loop
		 * 6. If the target word is found, how to back track?
		 * 7. With the help of the grandfather word
		 * 8. At each level create a table, 
		 */
		
		String grandfather;
		
		grandfather = start_word;

		for(int i = 0; i <= 40; ++i){

			if(Check_For_Match(i))
			{
				backtrack(i);
				displayResult(i);
				break;
			}
			
			if (i == 0)
				++i;
			
			SQL = "create table dbo.find_nearest_level_" + i + 
				  "(TABLE_ID int identity, prim varchar(255), neighbor varchar(255), level int, grandfather varchar(255), neighbor_score int)";

			myconn.execSQL(SQL);

			if (i == 1)
			{
				SQL = "insert into dbo.find_nearest_level_" + i + " " +
					  "(prim, neighbor, level, grandfather, neighbor_score) " +
					  "select prim, neighbor, " + i + ", prim, neighbor_score " +
					  "from dbo.nearest " + 
					  "where prim = '" + start_word + "'";
			}
			else
			{
				SQL = "insert into dbo.find_nearest_level_" + i + " " +
					  "(prim , neighbor , level , grandfather , neighbor_score ) " +
					  "select a11.neighbor, a12.neighbor, " + i + ", a11.prim, a12.neighbor_score " + 
					  "from dbo.find_nearest_level_" + (i-1) + " " + "a11 " + 
					  "join dbo.nearest a12 on " + 
					  "(a11.neighbor = a12.prim) " + 
					  "where a11.neighbor_score <> a12.neighbor_score " + 
					  "order by a11.neighbor, a12.neighbor";
				
				System.out.println(SQL);
			}
			
			myconn.execSQL(SQL);
			
		}
		
	}

	private boolean Check_For_Match(int i) {
		//Is there a neighbor that matches the end_word?
		
		if (i == 0)
		{
			SQL = "select count(neighbor) count " +
				  "from dbo.nearest " +
				  "where prim = '" + start_word + "' and " +
				  "neighbor = '" + end_word + "'";
		}
		else 
		{
			SQL = "select count(neighbor) count " +
				  "from dbo.find_nearest_level_" + (i-1) + " " +
				  "where neighbor = '" + end_word + "'"; 
		}
		
		System.out.println(SQL);
		
		if (myconn.execSQL_returnint(SQL) > 0)
			return true;
		else return false;
		
	}

	private void backtrack(int i) {
		// TODO Auto-generated method stub
		
		--i;
		System.out.println("Level is: " + i);
		
		initalizearr(i);
		populatearr(i);	
		
	}

	public void setword(String string1, String string2) {
		// TODO Auto-generated method stub
		start_word = string1;
		end_word = string2;
		
	}

	private void initalizearr(int j) {
		// TODO Auto-generated method stub
		
		mywordpatharr = new WordArr[j+1];
		for (int i = 0; i <=j; ++i)
			mywordpatharr[i] = new WordArr();
		
	}
	
	
	private void populatearr(int level){
		
		String prim = "", neighbor = "";
		
		int i = level;
		
		for (;i > 0; --i){
			
			if (i == level) 
			{
				SQL = "select top 1 prim, neighbor " + 
					  "from dbo.find_nearest_level_" + i + " " + "a11 " +
					  "where neighbor = '" + end_word + "'";
			}
			else
			{
				SQL = "select top 1 prim, neighbor " + 
					  "from dbo.find_nearest_level_" + i + " " + "a11 " +
					  "where neighbor = '" + mywordpatharr[i+1].getprim() + "'";
			}
			
			if(!myconn.execSQL_crs(SQL))
				System.exit(1);
			
		    try {

		    	CachedRowSetImpl crs = new CachedRowSetImpl();
		    	crs = myconn.getRowSet();

		    	while (crs.next()) {
		    		prim = crs.getString(1);
		    		System.out.println("prim: " + prim);

		    		neighbor = crs.getString(2);
		    		System.out.println("neighbor: " + neighbor);
		    	
		    	}
		    } catch (SQLException se){
		    	se.printStackTrace();
		    }
			
			mywordpatharr[i].setprim(prim);
			mywordpatharr[i].setneighbor(neighbor);
			mywordpatharr[i].setlevel(i);
		}
		
	}
	
	private void populatearr_defunct() {
		//get list of words and insert into WordArr
		
		SQL = "select word from wordlist order by TABLE_ID";
		
		if(!myconn.execSQL_crs(SQL))
			System.exit(1);
		
		int i = 0;
		
		// create CachedRowSet and output
	    try {

	    	CachedRowSetImpl crs = new CachedRowSetImpl();
	    	crs = myconn.getRowSet();

	    	while (crs.next()) {
	    		word = crs.getString(1);
	    		System.out.println("Name: " + word);
	    		mywordpatharr[i].setword(word);
	    		++i;
	      }
	    } catch (SQLException se){
	    	se.printStackTrace();
	    }
	}

	private void displayResult(int i) {
		// TODO Auto-generated method stub

		for(int j = 1; j < i; ++j){
			System.out.println("Level: " + j);
			System.out.println("Prim: " + mywordpatharr[j].getprim());
			System.out.println("Neighbor: " + mywordpatharr[j].getneighbor());
			System.out.println("");
		}
		
		final_level = i;
	}

	public void cleanup() {
		// TODO Auto-generated method stub
		
		for (int i = 1; i < final_level; ++i)
		{
			SQL = "drop table dbo.find_nearest_level_" + i;
			System.out.println(SQL);
			myconn.execSQL(SQL);
		}
	}
}
