package com.archimatetool.costing;

import org.eclipse.ui.PartInitException;


import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;

/**
 * Contains information about labels in the costing view
 * Labels for a DiagramModelArchimateObject show the price of the element
 * Labels for a DiagramModelArchimateConnection show the percentage of the relationship
 * @author Billy Hinard
 *
 */
public class PriceLabel{
	//the Note of this class
	private IDiagramModelNote note;
	//the width of the note
	private int NOTE_WIDTH;
	//the fixed width of the connection note
	private final int NOTE_CONNECTION_WIDTH = 55;; 
	//The fixed height of the price note
	private final int NOTE_HEIGHT = 30;
	//the fixed height of the connection note
	private final int NOTE_CONNECTION_HEIGHT =  NOTE_HEIGHT;
	//if the note is connected to a DMAO, it is stored here
	private IDiagramModelArchimateObject dmao; 
	//if the note is connection to a DMAC, it is stored here
	private IDiagramModelArchimateConnection dmac;
	//Group adjust variable to adjust the bounds of the note
	private GroupAdjust ga;
	
	public PriceLabel(){
		//initialize the variables
		note = null;
		NOTE_WIDTH = 0; //dynamic based on price
		dmao = null;
		dmac = null; 
		ga = new GroupAdjust();
	}	
	
	/**
	 * Makes a label for an IDiagramModelArchimateObject
	 * @param dmao the IDMAO to make a label for 
	 */
	public void makeLabel(IDiagramModelArchimateObject dmao){
		this.dmao = dmao; 
		IBounds bounds = dmao.getBounds();
		//get the x position of the dmao
		int master_x = bounds.getX();
		//get the y position of the dmao
		int master_y = bounds.getY();
		//get the right most x position of the dmao, plus 5
		int x = master_x + bounds.getWidth() + 5;
		//find the price of the element
		String price = findPrice(dmao);
		//if the price is null, return 
		if(price == null) return;
		//determine the width based on the total price
		NOTE_WIDTH = (price.length() * 10); 
		//create the note
		note = IArchimateFactory.eINSTANCE.createDiagramModelNote();
		note.setBounds(x, master_y, NOTE_WIDTH, NOTE_HEIGHT);
		note.setContent(price);
		note.setFillColor(findColor(dmao));
		note.setAdapter(null, dmao);
		note.setName("Note ");
		//if the container is a diagram model
		if(dmao.eContainer() instanceof IDiagramModel){
			//add the note to the children of the model
			dmao.getDiagramModel().getChildren().add(note);
		}
		//if the container is a diagram model group
		else if (dmao.eContainer() instanceof IDiagramModelGroup){
			//add the note to the group's children 
			((IDiagramModelGroup) dmao.eContainer()).getChildren().add(note);
		}
	}
	
	/**
	 * Determines what color the note will be set to
	 * @param dmao The IDiagramModelArchimateObject to get the color from
	 * @return The string representation of the color of the note
	 */
	private String findColor(IDiagramModelArchimateObject dmao){
		//If the user did not select a fill color
		if(dmao.getFillColor() == null){
			//get the folder of the Archimate Concept
			IFolder folder = ((IFolder) dmao.getArchimateConcept().eContainer());
			//if the element was nested in user created folder, loop until the archimate created folder
			while(folder.getType().equals(FolderType.USER)){
				folder = (IFolder) folder.eContainer();
			}
			//switch statement based on the type of folder, returns the default color of the elements in the folder
			switch(folder.getType().getValue()){
				case FolderType.APPLICATION_VALUE:
					return "#b5ffff";
				case FolderType.BUSINESS_VALUE:
					return "#ffffb5";
				case FolderType.IMPLEMENTATION_MIGRATION_VALUE:
					return "#ffe0e0";
				case FolderType.MOTIVATION_VALUE: 
					return "#ccccff";
				case FolderType.OTHER_VALUE: 
					return "#fbb875";
				case FolderType.STRATEGY_VALUE: 
					return "#f5deaa";
				case FolderType.TECHNOLOGY_VALUE:
					return "#c9e7b7";
			}
			//returns null if the element is somehow in another folder
			return null;
		}
		//if the user selected a fill color
		else{
			//return that fill color
			return dmao.getFillColor();
		}
	}
	
	/**
	 * Updates the price of the label
	 * @param dmao the DMAO to get the price from
	 */
	public void updatePrice(IDiagramModelArchimateObject dmao){
		note.setContent(findPrice(dmao));
	}
	
	/**
	 * Finds the price based on the properties of the dmao
	 * @param dmao The element whose price you want to find
	 * @return The price in the form of a string
	 */
	private String findPrice(IDiagramModelArchimateObject dmao){
		//Get the Archimate Concept
		IArchimateConcept ac = ((IDiagramModelArchimateObject) dmao).getArchimateConcept();
		
		//for each of the properties in the Archimate Concept
		for(Object p : ac.getProperties().toArray()){
			if(p instanceof IProperty){
				//if the property holds the total price
				if(((IProperty) p).getKey().toLowerCase().equals("total price")){
					//return the total price
					return ((IProperty) p).getValue();
				}
			}
		}
		return null;
	}
	
	/**
	 * Adjusts the position of the label for an IDiagramModelArchimateObject
	 * @param dmao The DMAO to get the bounds from 
	 * @throws PartInitException
	 */
	public void adjust(IDiagramModelArchimateObject dmao) throws PartInitException{
		IBounds bounds = dmao.getBounds();
		//get the x position of the dmao
		int master_x = bounds.getX();
		//get the y position of the dmao
		int master_y = bounds.getY();
		//get the right most x position of the dmao, plus 5
		int x = master_x + bounds.getWidth() + 5;
		note.setBounds(x, master_y, NOTE_WIDTH, NOTE_HEIGHT);
	}
	
	/**
	 * Makes a label for an IDiagramModelArchimateConnection
	 * @param dmac The connection to make a label for
	 */
	public void makeLabel(IDiagramModelArchimateConnection dmac){
		this.dmac = dmac; 
		//find the price of the dmac
		String price = findPrice(dmac);
		//if the price is null, return 
		if(price == null) return;
		//create the new note
		note = IArchimateFactory.eINSTANCE.createDiagramModelNote();
		//set the bounds of the note
		note.setBounds(findLocationX(((IDiagramModelObject) dmac.getSource()), ((IDiagramModelObject) dmac.getTarget())), findLocationY(((IDiagramModelObject) dmac.getSource()), ((IDiagramModelObject) dmac.getTarget())), NOTE_CONNECTION_WIDTH, NOTE_CONNECTION_HEIGHT);
		//set the specifics of the note
		String[] split = price.split("#");
		note.setContent(split[1] + "%");
		note.setName(split[0]);
		note.setFillColor("#bbbbbb");
		note.setBorderType(2);
		note.setAdapter(null, dmac);
		//create a new connection
		IDiagramModelConnection dmc = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
		//set the details of the connections
		dmc.setDocumentation("delete");
		dmc.setSource(note);
		dmc.setTarget(dmac);
		dmc.setLineColor("#bbbbbb");
		//add the note to the model
		((IDiagramModelArchimateObject) dmac.eContainer()).getDiagramModel().getChildren().add(note);
		//add the connection between the note and the DMAC
		note.getSourceConnections().add(dmc);
		dmac.getTargetConnections().add(dmc);
	}
	
	/**
	 * Determines the X location of label
	 * @param source The Bounds of the source element
	 * @param target The Bounds of the target element
	 * @return The X location of the element
	 */
	private int findLocationX(IDiagramModelObject dmo_source, IDiagramModelObject dmo_target){
		IBounds source = ga.adjust(dmo_source); 
		IBounds target = ga.adjust(dmo_target);
		//is the target to the left, middle, or right 
		//left
		if(source.getX() > (target.getX() + target.getWidth())){
			return (target.getX() + target.getWidth()) + ((source.getX() - (target.getX() + target.getWidth()))/2) - 50;
		}
		//right
		else if((source.getX() + source.getWidth()) < target.getX()){
			return (source.getX() + source.getWidth()) + ((target.getX() - (source.getX() + source.getWidth()))/2) + 10;
		}
		//middle
		else{
			return(source.getX() + (source.getWidth() / 2)) + 10;
		}
		
	}
	
	/**
	 * Determines the Y location of label
	 * @param source The Bounds of the source element
	 * @param target The Bounds of the target element
	 * @return The Y location of the element
	 */
	private int findLocationY(IDiagramModelObject dmo_source, IDiagramModelObject dmo_target){
		IBounds source = ga.adjust(dmo_source); 
		IBounds target = ga.adjust(dmo_target);
		//is the source above or below the target
		if((source.getY()) > (target.getY() + (target.getHeight()/2))){
			//Source is below the target
			int middle = source.getY() - (target.getY() + target.getHeight());
			return source.getY() - (middle/2) - (NOTE_CONNECTION_HEIGHT/2);
		}
		else{
			//Source is above the target
			int middle = target.getY() - (source.getY() + source.getHeight());
			return target.getY() - (middle/2) - (NOTE_CONNECTION_HEIGHT/2);
		}
	}
	
	/**
	 * Finds the total price of the provided IDiagramModelArchimateConncetion
	 * @param dmac The DMAC to find the price of 
	 * @return The total price of the DMAC
	 */

	private String findPrice(IDiagramModelArchimateConnection dmac){
		//Initialize the variables
		String price = null, percentage = null;
		//Get the archimate concept of the connection 
		IArchimateConcept ac = ((IDiagramModelArchimateConnection) dmac).getArchimateConcept();
		//loops through the properties of the concept
		for(Object p : ac.getProperties().toArray()){
			if(p instanceof IProperty){
				//if the property is the total price, store it in "price"
				if(((IProperty) p).getKey().toLowerCase().equals("total price")){
					price = ((IProperty) p).getValue();
				}
				//if the property is the percentage, store it in percent
				else if(((IProperty) p).getKey().charAt(0) == '%'){
					double percent = Double.parseDouble(((IProperty) p).getValue());
					percentage = String.format("%1$.2f", percent); 
				}
			}
		}
		//return the two, separated by a not often used character
		return price + "#" + percentage;
	}
	
	/**
	 * Updates the price of the label
	 * @param dmac The DMAC to get teh prie from 
	 */
	public void updatePrice(IDiagramModelArchimateConnection dmac){
		//find the price
		String price = findPrice(dmac);
		//if the price is null, retun 
		if(price == null) return;
		String[] split = price.split("#");
		note.setContent(split[1] + "%");
		note.setName(split[0]);
	}

	/**
	 * Adjust the position of the label for an IDiagramModelArchimateConnection
	 * @param dmac
	 */
	public void adjust(IDiagramModelArchimateConnection dmac){
		note.setBounds(findLocationX(((IDiagramModelObject) dmac.getSource()), ((IDiagramModelObject) dmac.getTarget())), findLocationY(((IDiagramModelObject) dmac.getSource()), ((IDiagramModelObject) dmac.getTarget())), NOTE_CONNECTION_WIDTH, NOTE_CONNECTION_HEIGHT);
	}
	
	/**
	 * Determines which type of element this note is related to
	 * @return The element the note is connected to
	 */
	public Object getConcept(){
		if(dmac == null){
			return dmao; 
		}
		else{
			return dmac; 
		}
	}
	
	/**
	 * Deletes the associated note
	 */
	public void delete(){
		//if the note is connected to a dmao
		if(dmac == null){
			if(dmao.eContainer() instanceof IDiagramModel){
				//remove it from the children of the model
				dmao.getDiagramModel().getChildren().remove(note);
			}
			//if the container is a diagram model group
			else if (dmao.eContainer() instanceof IDiagramModelGroup){
				//remove it from the children of the model
				((IDiagramModelGroup) dmao.eContainer()).getChildren().remove(note);
			}
			
		}
		//if the note is connected to a dmac
		else{
			int i = 0; 
			//remove it from the model
			((IDiagramModelArchimateObject) dmac.eContainer()).getDiagramModel().getChildren().remove(note);
			//remove the connection to the note
			for(Object ob : note.getSourceConnections().toArray()){
				if(((IDiagramModelConnection)ob).getDocumentation().equals("delete")){
					note.getSourceConnections().remove(i--);
					break;
				}
				i++;
			}
		}
	}
}
