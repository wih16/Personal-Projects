package com.archimatetool.merge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateConcept;
import com.archimatetool.model.impl.ArchimateDiagramModel;

public class LogHTML {
	//date for the saved file
	private final DateFormat df = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
	//the file that is going to be saved to 
	private File log_file;
	private int merge;
	
	//default constructor
	public LogHTML(){
		merge = 0;
	} 
	
	/**
	 * <!-- begin-user-doc -->
	 * Makes the log_file based on the name of the master model's file 
	 * @param file the file of the master model
	 * <!-- end-user-doc -->
	 */
	public void makeFile(File file){
		//initialize the String Builder
		StringBuilder sb = new StringBuilder();
		
		//name of the master file
		String filename = file.getName();
		
		//trimming the .archimate off of the end of the file, leaving no more than 5 characters of the file name
		if(filename.length() < 15){ //if the filename is shorter than 5 characters, just trim .archimate
			filename = filename.substring(0, filename.length() - 10);
		}
		else{ //if the file name is more than 5 characters, take the first 5 characters
			filename = filename.substring(0, 5);
		}
		
		//add the filename to the string builder
		sb.append(filename);
		
		//initialize the date and add it to the string builder
		Date date = new Date(); 
		sb.append(df.format(date));
		
		//append the file ending
		sb.append(".html");
		
		//this operation retrieves the path to the folder where the master file is stored
		//this was the log file can appear in the same location for the user to find
		String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - file.getName().length());
		
		//make a file from the path and the filename
		log_file = new File(path + sb.toString());
		
		/*
		 * checks if the file has been created or not
		 * if the filename is taken (HIGHLY UNLIKELY), it adds the classic (number) extension 
		 */
		
		try{
			boolean check = false;
			int i = 2; 
			while(!check){
				if(!(log_file.createNewFile())){ //if the file already exists
					//add the ()
					sb.insert(-4, "(" + i++ + ")" );
					//redefine the file
					log_file = new File(path + sb.toString());
				}
				else{ //the file did not exist
					check = true;
				}
			}	
		}
		catch(IOException ioe){
			System.out.print(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * This method is for the merging of elements
	 * Initializes the file to contain the necessary information for an HTML file 
	 * Includes the page title, style of the table, and the header of the web page
	 * @param master the name of the master model
	 * @param lesser the name of the lesser model 
	 * <!-- end-user-doc -->
	 */
	public void startElement(String master, String lesser){
		try{
			//initializing the buffered writer
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			//header of the .html file
			bw.write("<!DOCTYPE html> <head> <title> Log File </title> <style>");
			bw.newLine();
			//style of the table
			bw.write("table, th, td{");
			bw.newLine();
			bw.write("border: 1px solid black;");
			bw.newLine();
			bw.write("border-collapse: collapse;");
			bw.newLine();
			bw.write("padding: 5px;");
			bw.newLine();
			bw.write("}");
			bw.newLine();
			bw.write("table{");
			bw.newLine();
			bw.write("table-layout: fixed;");
			bw.newLine();
			bw.write("width: 75%;");
			bw.newLine();
			bw.write("}</style> </head> <body>");
			//Header of the webpage
			bw.write("<h1> Log file for the merge between " + master + " and " + lesser + "</h1>");
			bw.newLine();
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * This method is for the merging of models
	 * Initializes the file to contain the necessary information for an HTML file 
	 * Includes the page title, style of the table and navigation list, the header of the web page, and the navigation list itself
	 * @param master the name of the master model
	 * @param lesser the name of the lesser model 
	 * <!-- end-user-doc -->
	 */
	public void start(String master, String lesser){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<!DOCTYPE html> <head> <title> Log File </title> <style> #list{");
			bw.newLine();
			//style of the navigation list
			bw.write("display: block;");
			bw.newLine();
			bw.write("position: fixed;");
			bw.newLine();
			bw.write("bottom: 20px;");
			bw.newLine();
			bw.write("right: 30px;");
			bw.newLine();
			bw.write("z-index: 99;");
			bw.newLine();
			bw.write("}");
			bw.newLine();
			//style of the table
			bw.write("table, th, td{");
			bw.newLine();
			bw.write("border: 1px solid black;");
			bw.newLine();
			bw.write("border-collapse: collapse;");
			bw.newLine();
			bw.write("padding: 5px;");
			bw.newLine();
			bw.write("}");
			bw.newLine();
			bw.write("table{");
			bw.newLine();
			bw.write("table-layout: fixed;");
			bw.newLine();
			bw.write("width: 75%;");
			bw.newLine();
			bw.write("}</style>"); 
			bw.newLine();
			bw.write("</head> <body>");
			//Header of the webpage
			bw.write("<h1> Log file for the merge between " + master + " and " + lesser + "</h1>");
			//Legend
			bw.write("<br><br> <h2><u>Legend</u></h2>");
			bw.newLine();
			bw.write("<ol><li> Successfully Merged</li>");
			bw.newLine();
			bw.write("<ol type='a'><li> An element exists in the slave model that has the same Id as an element in the master model</li>");
			bw.newLine();
			bw.write("<li>The elements have the same name and properties</li>");
			bw.newLine();
			bw.write("<li>All instances of the slave element in a view were replaced by the master element</li></ol>");
			bw.newLine();
			bw.write("<li> Merged with Exceptions </li>");
			bw.newLine();
			bw.write("<ol type='a'><li> An element exists in the slave model that has the same Id as an element in the master model</li>");
			bw.newLine();
			bw.write("<li>Either the name or the properties are not the same between the two elements</li>");
			bw.newLine();
			bw.write("<li>For more information, see the 'Merged with Exceptions' section </li>");
			bw.newLine();
			bw.write("</ol><li> New Instance Created </li>");
			bw.newLine();
			bw.write("<ol type='a'><li> An element exists in the slave model that does not have the same Id as an element in the master model</li>");
			bw.newLine();
			bw.write("<li>The element is copied from the slave model to the proper folder in the master model</li></ol>");
			bw.newLine();
			bw.write("<li> Successfully Merged - Relationship</li>");
			bw.newLine();
			bw.write("<ol type='a'><li> A relationship exists in the slave element that has the same Id as a relationship in the master element</li>");
			bw.newLine();
			bw.write("<li>The two relationships have the same name and properties </li></ol>");
			bw.newLine();
			bw.write("<li> New Instance Created - Relationship</li>");
			bw.newLine();
			bw.write("<ol type='a'><li> Option one: A relationship exists in the slave element that has the same Id as a relationship in the master element, but the name or properties are different </li>");
			bw.newLine();
			bw.write("<li>Option two: A relationship exists in the slave element that does not have the same Id as a relationship in the master element</li>");
			bw.newLine();
			bw.write("<li>In both cases, the property is copied from the slave element to the master element</li></ol>");
			bw.newLine();
			bw.write("<li> Successfully Merged - Property</li>");
			bw.newLine();
			bw.write("<ol type='a'><li> A property exists in the slave element that has the same key and value as a property in the master element</li></ol>");
			bw.newLine();
			bw.write("<li> New Instance Created - Property</li>");
			bw.newLine();
			bw.write("<ol type='a'><li> Option one: A property exists in the slave element that has a same key as a relationship in the master model, but not the same value </li>");
			bw.newLine();
			bw.write("<li>Option two: A property exists in the slave element that does not have the same key as a property in the master element</li>");
			bw.newLine();
			bw.write("<li>In both case, the property is copied from the slave element to the master element</li></ol>");
			bw.newLine();
			bw.write("<li> Diagram Copied </li>");
			bw.newLine();
			bw.write("<ol type='a'><li> All Diagrams from the slave model are copied to the master model</li>");
			bw.newLine();
			bw.write("<li>This action simply indicates that the diagram was succesfully copied from slave to master</li></ol>");
			bw.newLine();
			bw.write("<li> Other Notes </li>");
			bw.newLine();
			bw.write("<ol type='a'><li>In the Merge Elements section, if there was nothing of note for the Relationship or Property sections, they are omitted</li>");
			bw.newLine();
			bw.write("<li>Relationships that are merged will be found in the Merged Elements section rather than the Relations section</li></ol></ol>");
			bw.newLine();
			bw.write("<button onclick='statistics()'>View Statistics</button>");
			bw.newLine();
			bw.write("<p id='stats'></p>");
			//Navigation list to jump to the specific folder locations
			bw.write("<ul id = \"list\">");
			bw.write("<li><a href=\"#STRATEGY\"> Strategy </a></li>");
			bw.write("<li><a href=\"#BUSINESS\"> Business </a></li>");
			bw.write("<li><a href=\"#APPLICATION\"> Application </a></li>");
			bw.write("<li><a href=\"#TECHNOLOGY\"> Technology </a></li>");
			bw.write("<li><a href=\"#MOTIVATION\"> Motivation </a></li>");
			bw.write("<li><a href=\"#IMPLEMENTATION_MIGRATION\"> Implentation_Migration </a></li>");
			bw.write("<li><a href=\"#OTHER\"> Other </a></li>");
			bw.write("<li><a href=\"#RELATIONS\"> Relations </a></li>");
			bw.write("<li><a href=\"#DIAGRAMS\"> Diagrams </a></li>");
			bw.write("<li><a href=\"#MERGE\"> Merged Elements </a></li>");
			bw.write("<li><a href=\"#EXCEPTIONS\"> Merged with Exceptions </a></li>");
			bw.write("</ul>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * The method writes the necessary information to the log_file to begin a new Archimate Folder.
	 * Sets the table columns, widths, and name of the table.
	 * The type is important because it allows the navigation list to work.
	 * @param type the fully capitalized type of the folder
	 * <!-- end-user-doc -->
	 */
	public void startFolder(String type){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<h3 id=\"" + type + "\">" + type + "</h3><table>");
			bw.write("<tr><th style='width: 35%;'>Type</th><th style='width: 35%;'>Name</th><th style='width: 20%;'>Action</th><th style='width: 10%;'>ID</th></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * If one of the main Archimate has elements stored in nested folder, these elements are displayed in a new table.
	 * This method closes the previous table and initializes the new table 
	 * The table columns are labeled and their widths are decided
	 * @param name The name of the nested folder
	 * <!-- end-user-doc -->
	 */
	public void startNested(String name){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("</table> <h5>" + name + "</h5> <table>");
			bw.write("<tr><th style='width: 35%;'>Type</th><th style='width: 35%;'>Name</th><th style='width: 20%;'>Action</th><th style='width: 10%;'>ID</th></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Adds the type, name, action, and id of an element from the lesser model that was merged into the master model. 
	 * Specifically, it adds this information to the table of the folder the element is contained in
	 * @param ac The element being merged 
	 * @param action The action performed on the element
	 * <!-- end-user-doc -->
	 */
	public void writeElements(ArchimateConcept ac, String action){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<tr><td>" + trimType(ac.getClass().toString()) + "</td><td>" + ac.getName() + "</td>" + action + "<td>" + ac.getId() + "</td></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Adds the type, name, action, and id of a view from the lesser model that was merged into the master model. 
	 * Specifically, it adds this information to the Diagrams table
	 * @param ac The view being merged 
	 * @param action The action performed on the element
	 * <!-- end-user-doc -->
	 */
	public void writeElements(ArchimateDiagramModel ac, String action){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<tr><td>" + trimType(ac.getClass().toString()) + "</td><td>" + ac.getName() + "</td><td>" + action + "</td><td>" + ac.getId() + "</td></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Closes the table element that was used for an Archimate folder
	 * <!-- end-user-doc -->
	 */
	public void finishFolder(){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("</table>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Writes the label of "Merged Elements" to denote the beginning of the section that shows which elements were merged.
	 * Unlike start folder, this does not initialize a table, that is handled by other methods.
	 * <!-- end-user-doc -->
	 */
	public void startMerge(){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<h3 id=\"MERGE\">MERGED ELEMENTS</h3><br>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Writes the two elements that are being merged to the log file.
	 * @param one One of the elements being merged
	 * @param two One of the elements being merged 
	 * <!-- end-user-doc -->
	 */
	public void startMergeElement(ArchimateConcept one, ArchimateConcept two){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<h2> Merge " + (++merge) + "</h2>");
			bw.write("<table><tr><th colspan='2'>Master Element</th><th colspan='2'>Slave Element</th></tr>");
			bw.write("<tr><td style='width: 25%;'> TYPE:"+ trimType(one.getClass().toString()) + "</td><td style='width: 25%;'> NAME:"+ one.getName() + "</td><td style='width: 25%;'> TYPE:"+ trimType(two.getClass().toString()) + "</td><td style='width: 25%;'> NAME:"+ two.getName() + "</td></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Starts the subsection of an element merge that shows the relationships of the lesser element.
	 * Initializes a table with columns names and widths
	 * <!-- end-user-doc -->
	 */
	public void startMergeRelationship(){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<table><tr><th colspan='6'>Relationships</th></tr><tr><th style='width: 20%;'>Type</th><th style='width: 20%;'>Name</th><th style='width: 15%;'>Source</th><th style='width: 15%;'>Target</th><th style='width: 20%;'>Action</th><th style='width: 10%;'>ID</th></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 *	Writes the type, name, source, target, action, and id of the provided relationship. 
	 * <b>a</b> should be a relationship from the lesser element
	 * @param a the relationship written to the file 
	 * @param action the action performed on a
	 * <!-- end-user-doc -->
	 */
	public void mergeRelationship(IArchimateRelationship a, String action){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<tr><td>" + trimType(a.getClass().toString()) + "</td><td>" + a.getName() + "</td><td>" + a.getSource().getName() + "</td><td>" + a.getTarget().getName() + "</td><td>" + action + "</td><td>" + a.getId() + "</td></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Starts the subsection of an element merge that shows the properties of the lesser element.
	 * Initializes a table with columns names and widths
	 * <!-- end-user-doc -->
	 */
	public void startMergeProperties(){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("</table><table><tr><th colspan='3'>Properties</th></tr><tr><th style='width: 40%;'>Key</th><th style='width: 40%;'>Value</th><th style='width: 20%;'>Action</th></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Write the provided property to the log file. 
	 * <b>a</b> should be an IProperty from the lesser element
	 * @param a property to be written to the log file
	 * @param action the action performed on a 
	 * <!-- end-user-doc -->
	 */
	public void mergeProperty(IProperty a, String action){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<tr><td>" + a.getKey() + "</td><td>" + a.getValue() + "</td><td>" + action +"</td></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Closes all of the open tags from the Merged Elements section of the .html file
	 * <!-- end-user-doc -->
	 */
	public void finishMerge(){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("</table><br><hr style='width: 75%; margin-left: 0px; margin-right: auto;'>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * Starts the Merged with Exceptions Section
	 */
	public void startMergeExceptions(){
		merge = 0;
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<h3 id=\"EXCEPTIONS\">MERGED WITH EXCEPTIONS</h3><br>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * Starts a section in the log file for a new "Merged with Exceptions"
	 * @param one one of the merged ArchimateConcepts
	 * @param two one of the merged ArchimateConcepts
	 */
	public void startException(ArchimateConcept one, ArchimateConcept two){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<h2> Merge Exception " + (++merge) + "</h2>");
			bw.write("<table><tr><th colspan='2'>Master Element</th><th colspan='2'>Slave Element</th></tr>");
			bw.write("<tr><td style='width: 25%;'> TYPE:"+ trimType(one.getClass().toString()) + "</td><td style='width: 25%;'> NAME:"+ one.getName() + "</td><td style='width: 25%;'> TYPE:"+ trimType(two.getClass().toString()) + "</td><td style='width: 25%;'> NAME:"+ two.getName() + "</td></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * Adds a name exception to the current Merge with Exception
	 * @param master the name of the master element
	 * @param lesser the name of the lesser element
	 */
	public void startName(String master, String lesser){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<table><tr><th colspan='2'>Name Exception</th></tr>");
			bw.write("<tr><td style='width: 50%%;'> Master Name: "+ master + "<br> Lesser Name: " + lesser + "</td><td style='width: 50%%;'> Check that the master element is the prefered name </td></tr></table>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * Starts the property exception section
	 */
	public void startProperty(){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<table><tr><th colspan='2'>Property Exception</th></tr>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	public void addProperty(String key, String master_value, String lesser_value){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<tr><td style='width: 50%%;'> Key: "+ key + "<br> Master Value: " + master_value + "<br> Lesser Value: " + lesser_value +"</td><td style='width: 50%%;'>Check that the value associated with this key is correct</td></tr></table>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Closes the body and html tags of the whole file. 
	 * <!-- end-user-doc -->
	 */
	public void finish(int successful, int exceptions, int instances){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
			bw.write("<script>");
			bw.newLine();
			bw.write("function statistics(){");
			bw.newLine();
			bw.write("document.getElementById('stats').innerHTML = \"Number of Successful Merges: " + successful + " <br> Number of Merges with Exceptions: " + exceptions + "<br> Number of New Instances Created: " + instances + "\"");
			bw.newLine();
			bw.write("} </script>");
			bw.newLine();
			bw.write("</body></html>");
			bw.close();
		}
		catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 *	The .getClass() method for the ArchimateConcepts returns the package and class.
	 *	This method trims the package off the returned string
	 * @param type The result of the .getClass() method call
	 * @return The trimmed type of the ArchimateConcept
	 * <!-- end-user-doc -->
	 */
	private String trimType(String type){
		return type.substring((type.lastIndexOf(".") + 1));
	}
}
