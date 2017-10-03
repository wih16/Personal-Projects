package com.archimatetool.costing;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.DiagramEditorInput;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.DiagramModelArchimateObject;

/**
 * Used to perform the calculations based on the costing algorithm used
 * Calculates from the bottom up when the plug-in is first turned on
 * Calculates from the changed element up when a specific property is changed
 * @author Billy Hinard
 *
 */
public class Calculate{
	//a collection of concepts at the current level of the graph
	ArrayList<IArchimateConcept> concepts;
	//a collection of the relations of the current element
	ArrayList<IArchimateRelationship> relations; 
	//store the next collection of concepts
	ArrayList<IArchimateConcept> next;
	//A HashMap of the DiagramModelArchimateObject id's and the corresponding total price notes
	HashMap<String, PriceLabel> labels; 
	//A list of the connections in the model
	ArrayList<IDiagramModelArchimateConnection> connections;
	

	
	//Constructor
	public Calculate(){
		//initialize all of the variables
		concepts = new ArrayList<IArchimateConcept>();
		relations = new ArrayList<IArchimateRelationship>();
		next = new ArrayList<IArchimateConcept>();
		labels = new HashMap<String, PriceLabel>();
		connections = new ArrayList<IDiagramModelArchimateConnection>();
	}
	
	/**
	 * When someone turns on the Calculate plug-in, compute to various costs of the diagram
	 */
	public void start(){
		//make sure the array of concepts is clear
		concepts.clear();
		//loop through the current editor references
		for(IEditorReference i: PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()){
			
			//this loops through all open views, should only choose one of them
			try {
				//get the EditorInput
				IEditorInput input = i.getEditorInput();
				//if it is a DiagramEditorInput
				if(input instanceof DiagramEditorInput){
					//loop through the set of elements in the view		
					for(Object o : ((DiagramEditorInput) input).getDiagramModel().getChildren().toArray()){
						//if it is an archimate objects
						if(o instanceof DiagramModelArchimateObject){
							IArchimateConcept ac = ((DiagramModelArchimateObject) o).getArchimateConcept();
							//if it only has relationships exiting the element, aka, it is at the bottom
							if(ac.getTargetRelationships().isEmpty()){
								//add it to the initial list of concepts 
								concepts.add(ac);
							}
						}
						//if it is a model group 
						if(o instanceof IDiagramModelGroup){
							//call the function to add all the elements from the group into the array 
							addGroups((IDiagramModelGroup) o);
						}
					}
					//execute the calculations (0 means that it came from start())
					execute(0);
				}
			}
			catch (PartInitException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adds the ArchimateConcepts from the group to the concepts array if necessary
	 * @param dmg the group to add ArchimateConcepts from if necessary
	 * @throws PartInitException 
	 */
	private void addGroups(IDiagramModelGroup dmg) throws PartInitException{
		//loop through all of the group's children
		for(Object o : dmg.getChildren().toArray()){
			//if it is an archimate object
			if(o instanceof DiagramModelArchimateObject){
				IArchimateConcept ac = ((DiagramModelArchimateObject) o).getArchimateConcept();
				//if it only has relationships exiting the element, aka, it is at the bottom
				if(ac.getTargetRelationships().isEmpty()){
					//add it to the initial list of concepts 
					concepts.add(ac);
				}
			}
			//if it is a model group
			if(o instanceof IDiagramModelGroup){
				//recurse with this model group
				addGroups((IDiagramModelGroup) o);
			}
		}
	}
	
	/**
	 * When someone updates a property in one of the concepts
	 * @param ac the concept whose property was updates
	 */
	public void calc(IArchimateConcept ac){
		//clear the array of concepts
		concepts.clear();
		//add the changed concept
		concepts.add(ac);
		//Execute the calculations (1 means that it came from calc())
		execute(1);
	}
	
	private void execute(int type){
		try{
			int initial = type; 
			while(!(concepts.isEmpty())){ 
				//loop through all of the elements in the concept array
				for(IArchimateConcept ac : concepts){
					type = initial;
					//contains the total cost of the activity
					double totalprice = 0; 
					//contains the index of the total price property, -1 if it doesn't exist yet
					int total = -1;
					//keeps track of current index
					int count = 0;
					//loop through the properties of the concept
					for(IProperty p : ac.getProperties()){
						//if the key starts with a _, meaning it is to be added to the price
						if(p.getKey().charAt(0) == '_'){
							//add this value to the total price
							totalprice += Double.parseDouble(p.getValue());
						}
						else if(p.getKey().toLowerCase().equals("input price")){
							//add this value to the total price
							totalprice += Double.parseDouble(p.getValue());
						}
						//if the properties key is Total Price
						else if(p.getKey().toLowerCase().equals("total price")){
							//save the index of this property 
							total = count; 
						}
						//increase the count 
						count++;
					}
					
					//if a total price property didn't exist
					if(total == -1){
						//create a new property
						IProperty new_prop = IArchimateFactory.eINSTANCE.createProperty();
						//set the key to total price
						new_prop.setKey("Total Price");
						//set the value to the total price
						new_prop.setValue(String.format("%1$.2f", totalprice));
						//add this property to the concepts list of properties 
						ac.getProperties().add(new_prop);
					}
					else{
						//update the total price property to be the new total price 
						ac.getProperties().get(total).setValue(String.format("%1$.2f", totalprice));
					}
					
					//clear the relations array 
					relations.clear();
					
					//add the source relations of the concept 
					for(IArchimateRelationship ar : ac.getSourceRelationships()){
						relations.add(ar);
					}
					
					//for all of the source relations that were just added
					for(IArchimateRelationship ar : relations){
						type = initial;
						//saves the calculated price based off the percentage
						double price_perc = 0;
						//stores the previous total price, used when calc() called execute()
						double previous = 0;
						//resetting total to -1
						total = -1; 
						//resetting count to 0
						count = 0; 
						//loop through the relations properties 
						for(IProperty p : ar.getProperties()){
							//if the key starts with %, meaning it holds the percentage of this relation 
							if(p.getKey().charAt(0) == '%'){
								//calculate the total price going through this relation 
								price_perc = (Double.parseDouble(p.getValue())/100) * totalprice; 
							}
							//if the key stores the total price
							else if(p.getKey().toLowerCase().equals("total price")){
								//save the index
								total = count; 
							}
							//increment count; 
							count++; 
						}


						//if the total price property doesn't exist
						if(total == -1){ 

							//create a new property 
							IProperty new_prop = IArchimateFactory.eINSTANCE.createProperty();
							//Set the key of the property to be "total price" 
							new_prop.setKey("Total Price");
							//set the value to be the calculated price passing through this relation 
							new_prop.setValue(String.format("%1$.2f", price_perc));
							//add this property to the relation 
							ar.getProperties().add(new_prop);
						}
						//if the total price price
						else{
							previous = Double.parseDouble(ar.getProperties().get(total).getValue());
							ar.getProperties().get(total).setValue(String.format("%1$.2f", price_perc));
						}

						boolean exists = false;
						//loop through all of the properties
						if(next.contains(ar.getTarget())){
							type = 2; 
						}
						else{
							next.add(ar.getTarget());
						}
						for(IProperty p : ar.getTarget().getProperties()){
							//look for an input price property
							if(p.getKey().toLowerCase().equals("input price")){
								try{
									//get the value of the input price
									double cur = Double.parseDouble(p.getValue());
									//if the update was called by calc()
									if(type == 1){
										//remove the previous amount
										cur -= previous;
									}
									//if the update was called by start()
									else if(type == 0){
										//reset the total price
										cur = 0; 
										type = 2; 
									}
									
									//add the price to the counter
									cur += price_perc; 
									//format the price
									p.setValue(String.format("%1$.2f", cur));
								}
								//if the input price is incorrect for some reason
								catch(NumberFormatException nfe){
									//set it to the price
									p.setValue(String.format("%1$.2f", price_perc));
								}
								exists = true; 
								break;
							}
						}

						//if the input price property didn't exist
						if(!exists){
							//create a new property
							IProperty new_prop = IArchimateFactory.eINSTANCE.createProperty(); 
							new_prop.setKey("Input Price");
							//add the price
							new_prop.setValue(String.format("%1$.2f", price_perc));
							ar.getTarget().getProperties().add(new_prop);
						}
					}
				}
				//clear the array of concepts
				concepts.clear();
				//add the next set of concepts to the concepts array 
				for(IArchimateConcept ac : next){
					concepts.add(ac); 
				}
				next.clear();
			}
		} catch (NumberFormatException e){
			System.out.println(e);
		}
		//update the price notes
		updateTotalPrice();
	}
	
	/**
	 * Updates the total prices of all of the elements in the view
	 */
	public void updateTotalPrice(){
		//clear the array of connections
		connections.clear();
		for(IEditorReference i: PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()){
			//this loops through all open views, should only choose one of them
			try {
				//get the EditorInput
				IEditorInput input = i.getEditorInput();
				//if it is a DiagramEditorInput
				if(input instanceof DiagramEditorInput){
					//loop through the set of elements in the view			
					for(Object o : ((DiagramEditorInput) input).getDiagramModel().getChildren().toArray()){
						//if it is an archimate objects
						if(o instanceof DiagramModelArchimateObject){
							//if the element already contains the label
							if(labels.containsKey(((IDiagramModelArchimateObject) o).getId())){
								//call the label's adjust method
								labels.get(((IDiagramModelArchimateObject) o).getId()).updatePrice((IDiagramModelArchimateObject) o);
							}
							//if note, create a new one
							else{
								PriceLabel pl = new PriceLabel(); 
								pl.makeLabel((IDiagramModelArchimateObject) o);
								labels.put(((DiagramModelArchimateObject) o).getId(), pl);
							}
							//add all of the connections of this element
							for(Object ob : ((DiagramModelArchimateObject) o).getSourceConnections().toArray())
								connections.add((IDiagramModelArchimateConnection) ob);
							for(Object ob : ((DiagramModelArchimateObject) o).getTargetConnections().toArray())
								connections.add((IDiagramModelArchimateConnection) ob);
						}
						//if it is a model group 
						if(o instanceof IDiagramModelGroup){
							updateGroups((IDiagramModelGroup) o);
						}
					}
				}
			}
			catch (PartInitException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Updates all of the notes of elements in groups
	 * @param group The group to loop through 
	 */
	private void updateGroups(IDiagramModelGroup group){
			
		for(Object o : group.getChildren().toArray()){
			//if it is an archimate objects
			if(o instanceof DiagramModelArchimateObject){
				//if the object already has a label
				if(labels.containsKey(((IDiagramModelArchimateObject) o).getId())){
					//call the label's adjust method
					labels.get(((IDiagramModelArchimateObject) o).getId()).updatePrice((IDiagramModelArchimateObject) o);
				}
				//the element already has a label
				else{
					PriceLabel pl = new PriceLabel(); 
					pl.makeLabel((IDiagramModelArchimateObject) o);
					labels.put(((DiagramModelArchimateObject) o).getId(), pl);
				}
				//add all of the connection 
				for(Object ob : ((DiagramModelArchimateObject) o).getSourceConnections().toArray())
					connections.add((IDiagramModelArchimateConnection) ob);
				for(Object ob : ((DiagramModelArchimateObject) o).getTargetConnections().toArray())
					connections.add((IDiagramModelArchimateConnection) ob);
			}
			//if it is a model group 
			if(o instanceof IDiagramModelGroup){
				updateGroups((IDiagramModelGroup) o);
			}
		}
	}
	/**
	 * Moves the price note when the corresponding object is moved
	 * @param dmao The objects that was moved
	 * @throws PartInitException
	 */
	public void adjust(IDiagramModelArchimateObject dmao) throws PartInitException{
		//if the element is contained in the HashMap
		if(labels.containsKey(dmao.getId())){
			//call the label's adjust method
			labels.get(dmao.getId()).adjust(dmao);
			//loop through all of the source connections
			for(Object o : dmao.getSourceConnections().toArray()){
				if(o instanceof IDiagramModelArchimateConnection){
					//if the relationship already has a label
					if(labels.containsKey(((IDiagramModelArchimateConnection) o).getId()))
						//adjust the position
						labels.get(((IDiagramModelArchimateConnection) o).getId()).adjust((IDiagramModelArchimateConnection) o);
				}
			}
			//loop through all of the target connections
			for(Object o : dmao.getTargetConnections().toArray()){
				if(o instanceof IDiagramModelArchimateConnection){
					//if the relationship already has a label
					if(labels.containsKey(((IDiagramModelArchimateConnection) o).getId()))
						//adjust the position
						labels.get(((IDiagramModelArchimateConnection) o).getId()).adjust((IDiagramModelArchimateConnection) o);
				}
			}
		}
	}
}
