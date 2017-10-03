package com.archimatetool.costing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.archimatetool.costing.wizard.IllegalPropertyValue;
import com.archimatetool.costing.wizard.NoPercentageSign;
import com.archimatetool.costing.wizard.NoUnderscore;
import com.archimatetool.costing.wizard.PercentageCheck;
import com.archimatetool.editor.diagram.DiagramEditorInput;
import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.diagram.editparts.diagram.NoteEditPart;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.DiagramModelArchimateObject;

/**
 * Handles the costing plug_in as well as the Enter Costing Data Menu Option
 * Implements PropertyChangeListner to update the calculations when a property is changed
 * Implements SelectionListener to add labels on the screen when various elements are pressed
 * @author Billy Hinard
 *
 */
public class Costing extends AbstractHandler implements PropertyChangeListener, ISelectionListener{
	//Global Calculate object to calculate the costing strategy of the model
	Calculate c; 
	//The previous note holding information about an element that should be deleted
	IDiagramModelNote previous_note; 
	//The previous model containing the previous_note
	IDiagramModel previous_model; 
	//The list of previous relationship labels
	ArrayList<PriceLabel> previous_labels;
	//The list of previous relations containing labels 
	ArrayList<IDiagramModelArchimateConnection> previous_relations; 
	
	//This method is called when the menu option is clicked
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//if the user is turning the plugin on or off
		//initializing the variables; 
		previous_note = null; 
		previous_labels = new ArrayList<PriceLabel>();
		previous_relations = new ArrayList<IDiagramModelArchimateConnection>();
		//Retrieve the command of event
		Command command = event.getCommand();
		
		//Changes the state (true/false) of the command, retrieves the previous false
	    boolean oldValue = HandlerUtil.toggleCommandState(command);
	    
	    //The user has just turned on the command
	    if(!oldValue){
	    	//create a new Calculate object
	    	c = new Calculate(); 
	    	//perform the initial calculations
	    	c.start();
	    	
	    	//create a PropertyChangeListener, will call propertyChange() whenever something in the model is changed
	    	IEditorModelManager.INSTANCE.addPropertyChangeListener(this);
	    	
	    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()[0].getPage().addSelectionListener(this);
	    }
	    //The user just turned off the command
	    else{
	    	//Remove the PropertyChangeListner
	    	IEditorModelManager.INSTANCE.removePropertyChangeListener(this);
	    	close();
	    }   
	
	    //method must return null
	    return null;
	}

	//This method is called when the PropertyChangeListener exists and something in the model is changed
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		//Ensures the the property change occurred in the model
    	if(IEditorModelManager.PROPERTY_ECORE_EVENT.equals(evt.getPropertyName())) {
			//retrieve that notification value of the event
			Notification msg = (Notification)evt.getNewValue();
			//Retrieves the feature value of the notification
			Object feature = msg.getFeature();
			 
			//if an elements bounds changed in the model
			if(feature == IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__BOUNDS){
				//if the object that was changed was an IDiagramModelArchimateObject
				if(msg.getNotifier() instanceof IDiagramModelArchimateObject){
					try {
						//adjust the total price note 
						c.adjust((IDiagramModelArchimateObject) msg.getNotifier());
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
			 
			//if the property value of one of the elements was changed
			if(feature == IArchimatePackage.Literals.PROPERTY__VALUE) {
				//retrieve the property that was changed
				IProperty property = (IProperty)msg.getNotifier();
				//if the property value needs to be a number
				if(!(property.getKey().toLowerCase().equals("consumption metric"))){
					//check if the property can convert to a number 
					try{
						Double.parseDouble(property.getValue());
					}
					catch(NumberFormatException nfe){
						IllegalPropertyValue ipv = new IllegalPropertyValue(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
						ipv.open();
						return;
					}
				}
				//remove the PropertyChangeListener, otherwise you will enter an endless loop of changing properties 
				IEditorModelManager.INSTANCE.removePropertyChangeListener(this);
				//if the element in which the property changed was a Relationship
				if(property.eContainer() instanceof IArchimateRelationship){
					//if the property key isn't empty
					if(!(property.getKey().isEmpty())){
						//if the property key doesnt start with a %
						if(property.getKey().charAt(0) != '%'){
							//warn the user of this
							NoPercentageSign nps = new NoPercentageSign(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
							nps.open();
							return; 
						}
					}
					//update the prices based on the source of the relationship
					c.calc((IArchimateConcept) ((IArchimateRelationship) property.eContainer()).getSource());
				}
				//if the element in which the property changed was a Concept
				else{
					//if the property key isn't empty
					if(!(property.getKey().isEmpty())){
						//if the property key doesn't start with an underscore
						if(property.getKey().charAt(0) != '_'){
							//warn the user of this
							NoUnderscore nu = new NoUnderscore(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
							nu.open();
							return; 
						}
					}
					//update the prices based on the concept
					c.calc((IArchimateConcept) property.eContainer());
				}
				//If the container was a relationship and the property was the percentage
				if(property.eContainer() instanceof IArchimateRelationship && property.getKey().charAt(0) == '%'){
					//Ensure that the total percentages of the source element equal 100%
					checkPercentages(((IArchimateRelationship) property.eContainer()).getSource()); 
				}
				//Re-add the PropertyChangeListener
				IEditorModelManager.INSTANCE.addPropertyChangeListener(this);
			}
        }
	}
	
	/**
	 * Check that the percentages of relationships leaving an element sum to 100%
	 * @param ac the element to check 
	 */
	private void checkPercentages(IArchimateConcept ac){
		//the total percentage value
		double total = 0; 
		//for each source relationship of the element
		for(Object o : ac.getSourceRelationships().toArray()){
			if(o instanceof IArchimateRelationship){
				//for each property in the relationship
				for(Object ob : ((IArchimateRelationship) o).getProperties()){
					if(ob instanceof IProperty){
						//if the relationship is a percentage
						if(((IProperty) ob).getKey().charAt(0) == '%'){
							//add it to the total
							total += Double.parseDouble(((IProperty) ob).getValue());
							break;
						}
					}
				}
			}
		}
		//if the total isn't equal to 100%
		if(total != 100){
			//create a warning dialog 	    	
			PercentageCheck pc = new PercentageCheck(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), ac);
			pc.open();
		}
	}
	private void close(){
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
							for(Object ob : ((DiagramModelArchimateObject) o).getTargetConnections().toArray()){
								int i1 = 0; 
								for(Object obj : ((IDiagramModelConnection) ob).getTargetConnections().toArray()){
									if(((IDiagramModelConnection) obj).getDocumentation().equals("delete")){ 
										((IDiagramModelConnection) ob).getTargetConnections().remove(i1);
										//break;
									}
									else{
										i1++;
									}
								}
							}
						}
						//if it is a model group
						if(o instanceof IDiagramModelGroup){
							//recurse with this model group
							removeGroups((IDiagramModelGroup) o);
						}
						//If it is a Note
						if(o instanceof IDiagramModelNote){
							//if it is a note created by the code and not the user
							if((((IDiagramModelNote) o).getContent().charAt(((IDiagramModelNote) o).getContent().length() - 1) == '%') || ((IDiagramModelNote) o).getName().equals("Note ") || ((IDiagramModelNote) o).getContent().substring(0, 5).equals("Direc")){								
								int i1 = 0; 
								for(Object ob : ((IDiagramModelNote) o).getSourceConnections().toArray()){
									if(((IDiagramModelConnection)ob).getDocumentation().equals("delete")){
										((IDiagramModelNote) o).getSourceConnections().remove(i1--);
									}
									i1++;
								}
								//Retrieve the index of the note
								int index =((IDiagramModelNote) o).getDiagramModel().getChildren().indexOf(o);
								//delete the note
								((IDiagramModelNote) o).getDiagramModel().getChildren().remove(index);
							}
						}
					}
				}
			}
			catch (PartInitException e){
				e.printStackTrace();
			}
		}
	}
	
	private void removeGroups(IDiagramModelGroup dmg) throws PartInitException{
		//loop through all of the group's children
		for(Object o : dmg.getChildren().toArray()){
			//if it is an archimate object
			if(o instanceof DiagramModelArchimateObject){
				for(Object ob : ((DiagramModelArchimateObject) o).getTargetConnections().toArray()){
					int i1 = 0; 
					for(Object obj : ((IDiagramModelConnection) ob).getTargetConnections().toArray()){
						if(((IDiagramModelConnection) obj).getDocumentation().equals("delete")){ 
							((IDiagramModelConnection) ob).getTargetConnections().remove(i1);
							//break;
						}
						i1++;
					}
				}
			}
			//if it is a model group
			if(o instanceof IDiagramModelGroup){
				//recurse with this model group
				removeGroups((IDiagramModelGroup) o);
			}
			//If it is a Note
			if(o instanceof IDiagramModelNote){
				//if it is a note created by the code and not the user
				if((((IDiagramModelNote) o).getContent().charAt(((IDiagramModelNote) o).getContent().length() - 1) == '%') || ((IDiagramModelNote) o).getName().equals("Note ") || ((IDiagramModelNote) o).getContent().substring(0, 5).equals("Direc")){
					int i1 = 0; 
					for(Object ob : ((IDiagramModelNote) o).getSourceConnections().toArray()){
						if(((IDiagramModelConnection)ob).getDocumentation().equals("delete")){
							((IDiagramModelNote) o).getSourceConnections().remove(i1);
							//break;
						}
						i1++;
					}
					//Retrieve the index of the note
					int index =((IDiagramModelGroup) ((IDiagramModelNote) o).eContainer()).getChildren().indexOf(o);
					//delete the note
					((IDiagramModelGroup) ((IDiagramModelNote) o).eContainer()).getChildren().remove(index);
				}
			}
		}
	}
	/*
	 * Method is called when the selection in the model changes
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		//if selection is a StructureSelection, this is anything within the model view
		if(selection instanceof IStructuredSelection){
			//if there is actaully something selected
			if(((IStructuredSelection) selection).toArray().length != 0){
				//delete all of the previous notes as long as a different note is not selected
				//delete all of the previous relations as long as a different note is not selected
				if(!(((IStructuredSelection) selection).toArray()[0] instanceof NoteEditPart)){
					//delete the previous note
					if(previous_model != null && previous_note != null)
						previous_model.getChildren().remove(previous_note);
					
					//delete the previous labels
					for(PriceLabel pl : previous_labels){
						pl.delete();
					}
					//delete all of the connections in the label
					for(IDiagramModelArchimateConnection ar : previous_relations){
						//index counter
						int i1 = 0; 
						//loop through the target connections
						for(Object obj : ar.getTargetConnections().toArray()){
							//if the connection is one we want to delete
							if(((IDiagramModelConnection) obj).getDocumentation().equals("delete")){
								//remove the connection
								ar.getTargetConnections().remove(i1--);
							}
							else{
								i1++;
							}
						}
					}
				}
				//if the selection is a note
				if(((IStructuredSelection) selection).toArray()[0] instanceof NoteEditPart){
					//retrieve the NoteEditPart
					NoteEditPart nep = (NoteEditPart) ((IStructuredSelection) selection).toArray()[0];
					//if the selection is a price label
					if(nep.getModel().getAdapter(null) instanceof IDiagramModelArchimateObject){
						//remove the previous note
						if(previous_model != null && previous_note != null)
							previous_model.getChildren().remove(previous_note);
						//create a new note
						objectNote((IDiagramModelArchimateObject) nep.getModel().getAdapter(null), (IDiagramModelNote) nep.getModel());
					}
					//if the selection is a percentage label
					else if(nep.getModel().getAdapter(null) instanceof IDiagramModelArchimateConnection){
						//remove the previous note
						if(previous_model != null && previous_note != null)
							previous_model.getChildren().remove(previous_note);
					}
				}
				//if the selection is an archimate element
				else if(((IStructuredSelection) selection).toArray()[0] instanceof ArchimateElementEditPart){
					//clear the previous labels list
					previous_labels.clear();
					//retrieve the EditPart
					ArchimateElementEditPart aeep = (ArchimateElementEditPart) ((IStructuredSelection) selection).toArray()[0];
					//loop through the source connection
					for(Object o : aeep.getModel().getSourceConnections()){
						//create a new price label
						PriceLabel pl = new PriceLabel();
						pl.makeLabel((IDiagramModelArchimateConnection) o);
						previous_labels.add(pl);
						previous_relations.add((IDiagramModelArchimateConnection) o);
					}
					//loop through the target connections
					for(Object o : aeep.getModel().getTargetConnections()){
						//create a new price label
						PriceLabel pl = new PriceLabel();
						pl.makeLabel((IDiagramModelArchimateConnection) o);
						previous_labels.add(pl);
						previous_relations.add((IDiagramModelArchimateConnection) o);
					}
				}
			}
		}
	}
	
	private void objectNote(IDiagramModelArchimateObject dmao, IDiagramModelNote note){
		GroupAdjust ga = new GroupAdjust(); 
		//finding the location to place the new note
		IBounds note_bounds = ga.adjust(note);
		int x = note_bounds.getX() + note_bounds.getWidth() + 5; 
		int y = note_bounds.getY();
		
		//total price of the dmao
		double total = 0; 
		//total allocated price 
		double allocated = 0; 
		//the direct costs of the element
		double direct = 0; 
		//consumption metric 
		String metric = null; 
		//an array of the names of the relationships allocating price
		String[] names = new String[dmao.getArchimateConcept().getTargetRelationships().toArray().length];
		//an array of the prices of the relationships
		double[] prices = new double[names.length];
		//an array of the percentages of the relationships
		double[] percents = new double[names.length];
		//counter for the above mentioned arrays
		int i = 0; 
		//size of the longest name
		int longest_name = 0; 
		//size of the longest price
		int longest_price = 0; 
		//format of the longest price
		String format; 
		
		//loop the properties
		for(Object o : dmao.getArchimateConcept().getProperties().toArray()){
			if(o instanceof IProperty){
				//if the property is the total price
				if(((IProperty) o).getKey().toLowerCase().equals("total price")){
					//save the total price
					total = Double.parseDouble(((IProperty) o).getValue());
				}
				//if the property is the input price
				else if(((IProperty) o).getKey().toLowerCase().equals("input price")){
					//save the input price
					allocated = Double.parseDouble(((IProperty) o).getValue());
				}
				//if the property is a direct cost
				else if(((IProperty) o).getKey().charAt(0) == '_'){
					//add to the direct cost total 
					direct += Double.parseDouble(((IProperty) o).getValue());
				}
				//if the property is the consumption metric
				else if(((IProperty) o).getKey().toLowerCase().equals("consumption metric")){
					//save the consumption metric
					metric = ((IProperty) o).getValue();
				}
			}
		}
		
		//loop through the target relationships to find the specific input prices
		for(Object o : dmao.getArchimateConcept().getTargetRelationships().toArray()){
			if(o instanceof IArchimateRelationship){
				//loop through the properties of the relationship
				for(Object ob : ((IArchimateRelationship) o).getProperties()){
					//if the property is the total price
					if(((IProperty) ob).getKey().toLowerCase().equals("total price")){
						//save the name of the source
						names[i] = defaultName(((IArchimateRelationship) o).getSource());
						//determine longest name
						if(names[i].length() > longest_name)
							longest_name = names[i].length();
						//save the price of the relation
						prices[i] = Double.parseDouble(((IProperty) ob).getValue());
						//determine the longest price
						if((format = String.format("%1$.2f", prices[i])).length() > longest_price){
							longest_price = format.length();
						}
					}
					//save the percents of the relationship
					else if(((IProperty) ob).getKey().charAt(0) == '%'){
						percents[i] = Double.parseDouble(((IProperty) ob).getValue());
					}
				}
				i++;
			}
		}
		
		//make sure the longest name is at least 20
		if(longest_name < 20){
			longest_name = 20;
		}
		//if the consumption metric is known
		if(metric != null){
			//check if the consumption metric is longer than the longest name
			if(longest_name < (17 + metric.length())){
				longest_name = (int) (17 + metric.length() - longest_price); 
			}
		}
		
		//create a new note
		IDiagramModelNote new_note = IArchimateFactory.eINSTANCE.createDiagramModelNote();
		//if this is a bottom node
		if(allocated == 0){
			//the length of the box
			int length = 0; 
			if(metric != null)
				//set a longer length if the consumption metric is known
				length = metric.length();
			//set the bounds of the note
			new_note.setBounds(x, y, (17 + length) * 10, 40);
			//set the content of the note
			new_note.setContent("Direct Costs:   " + String.format("%1$.2f", direct) + "\nConsumption Metric:    " + metric);
		}
		//if this isn't a bottom node
		else{
			//create the bounds of the node
			new_note.setBounds(x, y, (int) (longest_name + longest_price) * 8 , (names.length + 9) * 10);
			//create a new stringbuilder for the contents
			StringBuilder contents = new StringBuilder();
			//add the direct cost to the SB
			contents.append("Direct Costs:   " + String.format("%1$.2f", direct));
			//add the consumption cost to the SB
			contents.append("\nConsumption Costs:   " + String.format("%1$.2f", allocated));
			//for each of the consumption costs, add it to the SB
			for(int j = 0; j < names.length; j++){
				if(names[j] != null){
					contents.append("\n   " + names[j] + " (" + String.format("%1$.2f", percents[j]) + "%):   " + String.format("%1$.2f", prices[j]));
				}
			}
			//add the total cost to the SB
			contents.append("\nTotal Costs:   " + String.format("%1$.2f", total));
			//add the consumption metric to the SB
			contents.append("\nConsumption Metric:    " + metric);
			//add the SB to the contents
			new_note.setContent(contents.toString());
		}
		//align the text to the left
		new_note.setTextAlignment(IDiagramModelNote.TEXT_ALIGNMENT_LEFT);
		
		//add the new note to the diagram model 
		dmao.getDiagramModel().getChildren().add(new_note);
		//set the previous note and previous model 
		previous_note = new_note;
		previous_model = dmao.getDiagramModel();
		
	}
	
	/**
	 * Determines the name of a concept depending on if the user has changed the name
	 * @param ac the concept to determine the name of
	 * @return The users name if the name has been changed, the default type of the element otherwise
	 */
	private String defaultName(IArchimateConcept ac){
		//if the user changed the name, return that name
		if(!(ac.getName().isEmpty())){
			return ac.getName();
		}
		//The unspaced type of the element
		String name = ac.getClass().toString().substring(ac.getClass().toString().lastIndexOf('.'));
		//new stringbuilder for the name
		StringBuilder return_name = new StringBuilder();
		//Every time there is a capital letter, add a space to the name
		//BusinessActor turns into Business Actor
		for(int i = 1; i < name.length(); i++){
			if(name.charAt(i) < 91){
				return_name.append(' ');
				return_name.append(name.charAt(i));
			}
			else{
				return_name.append(name.charAt(i));
			}
		}
		
		return return_name.toString();
	}
}