package com.archimatetool.merge;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.views.tree.TreeModelView;
import com.archimatetool.editor.views.tree.commands.DeleteCommandHandler;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateConcept;

public class MergeElements implements IHandler {
	private LogHTML master_log;

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//Retrieves the current selections of in the Model Tree View
		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		
		//If nothing is selected, return null
		if(sel.isEmpty()){
			return null;
		}
		
		//Get the array of paths to the two elements 
		TreePath[] tp = ((TreeSelection) sel).getPaths();
		//the names of the two elements for the popup
		String first, last; 
		first = ((ArchimateConcept) tp[0].getLastSegment()).getName();
		last = ((ArchimateConcept) tp[1].getLastSegment()).getName();
		
		//create a new wizard with the names of the elements 
		MasterElement me = new MasterElement(first, last);
		
		//create a new wizard dialog 
		WizardDialog wd = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), me){
			//this method sets the size of the wizard
			@Override
			protected void configureShell(Shell newShell){
				super.configureShell(newShell);
				newShell.setSize(496, 222);
			}
		};
		//opens the wizard, if it is canceled during execution, return null
		if(wd.open() == Window.CANCEL){
			return null;
		}
		//variable to store the decision from the user
		int check = 0; 
		//save the selection of the user
		check = me.getSelection();
		
		//depending on the selection of the user, set the master element and the lesser element 
		ArchimateConcept lesser, master;
		if(check == 1){
			master = (ArchimateConcept) tp[0].getLastSegment(); //master
			lesser = (ArchimateConcept) tp[1].getLastSegment(); //lesser
		}
		else{
			lesser = (ArchimateConcept) tp[0].getLastSegment(); //lesser
			master = (ArchimateConcept) tp[1].getLastSegment(); //master
		}
	
		//initializes the log file
		LogHTML lh = new LogHTML();
		lh.makeFile(master.getArchimateModel().getFile());
		lh.startElement(master.getName(), lesser.getName());
		//sets it as the log file for this merge
		setLH(lh);
		
		//execute the merge
		execute_from_master(master, lesser, (IArchimateModel) tp[0].getFirstSegment());
		
		return null;
	}
	
	/**
	 * This method exists so that it's functionality could be called from other class 
	 * It performs the merge of two Archimate elements of the same type
	 * @param master the master Archimate element
	 * @param lesser the lesser Archimate element
	 * @param model the model of the elements
	 */
	public void execute_from_master(ArchimateConcept master, ArchimateConcept lesser, IArchimateModel model){
		//start the section for this element merge
		master_log.startMergeElement(master, lesser);
		//this boolean is used to determine whether a table should be written for "relationships" or "properties"
		boolean check = true; 
				 
		//a hash of all of the source relationships of the master element
		HashMap<String, IArchimateRelationship> hm_source = new HashMap<String, IArchimateRelationship>();
		for(Object p : master.getSourceRelationships()){
			if(p instanceof IArchimateRelationship){
				//add the relationship to the HashMap
				hm_source.put(((IArchimateRelationship) p).getId(), ((IArchimateRelationship) p));
			}
		}
		
		//this is an array used to delete concepts or relationships 
		//an array is required by the DeleteCommandHandler
		Object[] to_delete = new Object[1]; 
		
		//this is the TreeModelView used by the DeleteCommandHandler
		TreeModelView tmv = (TreeModelView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		
		//has a delete() method to delete elements
		DeleteCommandHandler dch;	
	
		//loop through the lesser element's relationships
		for(Object ar : lesser.getSourceRelationships().toArray()){
			if(ar instanceof IArchimateRelationship){
				if(check){
					//start the section of the merge of relationships
					master_log.startMergeRelationship();
					check = false;
				}
				//done to avoid constant casting
				IArchimateRelationship a = (IArchimateRelationship) ar;
				//if the master element has a relationship of the same id
				if(hm_source.containsKey(a.getId())){
					//get the master elements relationship
					IArchimateRelationship b = hm_source.get(a.getId());
					//if the have the same name and same properties
					if(b.getName().equals(a.getName()) && b.getProperties().toString().equals(a.getProperties().toString())){
						//loop through the diagram connections of the lesser relation
						for(Object o : a.getReferencingDiagramConnections().toArray()){
							if(o instanceof IDiagramModelArchimateConnection){
								//adding the diagram connections to the master element
								b.getReferencingDiagramConnections().add((IDiagramModelArchimateConnection) o);
							}
						}
						
						//looping through the diagrams the lesser element is in
						for(Object ob : DiagramModelUtils.findReferencedDiagramsForArchimateConcept(a).toArray()){
							//looping through all instances of the lesser element in this diagram
							for(Object i : DiagramModelUtils.findDiagramModelComponentsForArchimateConcept((IDiagramModel) ob, a).toArray()){
								if(i instanceof IDiagramModelArchimateComponent){
									//changing the element from lesser to master
									((IDiagramModelArchimateComponent) i).setArchimateConcept(b);
									//adding this diagram to the master's list
									DiagramModelUtils.findDiagramModelComponentsForArchimateConcept((IDiagramModel) ob, b).add((IDiagramModelArchimateComponent) i);
								}
							}
						}
						//writing this to the log file
						master_log.mergeRelationship(a, "Successfully Merged - Relationship");
						//adding the lesser element to the delete array
						to_delete[0] = a;
						
						//creating a new DCH object
						dch = new DeleteCommandHandler(tmv.getViewer(), to_delete);
						//deleting this relation
						dch.delete();
						
						//skip the next couple of lines
						continue;
					}
				}
				//if master doesn't contain the identical relationship, add it to the master
				master.getSourceRelationships().add((IArchimateRelationship) ar);
				//write this to the log file
				master_log.mergeRelationship(a, "New Instance Created - Relationship");
			}
		}
		
		//creating a HashMap of the target relationships of the master element
		HashMap<String, IArchimateRelationship> hm_target = new HashMap<String, IArchimateRelationship>();
		for(Object p : master.getTargetRelationships()){
			if(p instanceof IArchimateRelationship){
				hm_target.put(((IArchimateRelationship) p).getId(), ((IArchimateRelationship) p));
			}
		}
		
		
		//Looping through all  of the target relationships of the lesser element 
		for(Object ar : lesser.getTargetRelationships().toArray()){
			if(ar instanceof IArchimateRelationship){
				if(check){
					//start the section of the merge of relationships
					master_log.startMergeRelationship();
					check = false;
				}
				//to avoid constant casting
				IArchimateRelationship a = (IArchimateRelationship) ar;
				//If a relationship of the same Id is present in the master element 
				if(hm_target.containsKey(a.getId())){
					//get the comparable relationship from the master element
					IArchimateRelationship b = hm_target.get(a.getId());
					//if they have the same name and properties 
					if(b.getName().equals(a.getName()) && b.getProperties().toString().equals(a.getProperties().toString())){
						//looping through the diagram connections of the lesser element
						for(Object o : a.getReferencingDiagramConnections().toArray()){
							if(o instanceof IDiagramModelArchimateConnection){
								//add the diagram connection to the master element
								b.getReferencingDiagramConnections().add((IDiagramModelArchimateConnection) o);
							}
						}
				
						//loop through the diagrams the lesser relationship is present in
						for(Object ob : DiagramModelUtils.findReferencedDiagramsForArchimateConcept(a).toArray()){
							//loop through all of the instances of the lesser relationship in this diagram model
							for(Object i : DiagramModelUtils.findDiagramModelComponentsForArchimateConcept((IDiagramModel) ob, a).toArray()){
								if(i instanceof IDiagramModelArchimateComponent){
									//change it from lesser to master
									((IDiagramModelArchimateComponent) i).setArchimateConcept(b);
									//add the instance in the diagram to the master list
									DiagramModelUtils.findDiagramModelComponentsForArchimateConcept((IDiagramModel) ob, b).add((IDiagramModelArchimateComponent) i);
								}
							}
						}
						//write this to the log file
						master_log.mergeRelationship(a, "Successfully Merged - Relationship");

						//adding the lesser relationship to the delete array
						to_delete[0] = a;
						
						//creating a new DCH object
						dch = new DeleteCommandHandler(tmv.getViewer(), to_delete);
						//deleting the lesser element 
						dch.delete();
						
						continue;
					}
				}
				//if the lesser element is truly unique, add it to the master element
				master.getTargetRelationships().add(a);
				//write this to the log file
				master_log.mergeRelationship(a, "New Instance Created - Relationship");
			}
		}

		//reseting checked to true
		check = true; 
		
		HashMap<String, String> hm = new HashMap<String, String>(); 
		
		//loop through all of the properties of the master element
		for (Object ip : master.getProperties().toArray()){
			if(ip instanceof IProperty){
				//place the key and value into the HashMap
				hm.put(((IProperty) ip).getKey(), ((IProperty) ip).getValue());
			}
		}
		
		//loop through all of the properties of the lesser element 
		for(Object ip : lesser.getProperties().toArray()){
			if(ip instanceof IProperty){
				//start the property header if necessary 
				if(check){
					master_log.startMergeProperties();
					check = false;
				}
				//if the master element doesn't contain the property key
				if(!(hm.containsKey(((IProperty) ip).getKey()))){
					//add the property to the master element 
					master.getProperties().add((IProperty)ip);
					//write it to the log 
					master_log.mergeProperty((IProperty) ip, "New Instance Created - Property");
				}
				//if the master element does contain the property key
				else{
					//if the properties of the same key have the same value 
					if(hm.get(((IProperty) ip).getKey()).equals(((IProperty) ip).getValue())){
						//write this to the log
						master_log.mergeProperty((IProperty) ip, "Successfully Merged - Property");
					}
					//if the properties of the same key do not have the same value 
					else{
						//add it to the master element
						master.getProperties().add((IProperty)ip);
						//write this to the log 
						master_log.mergeProperty((IProperty) ip, "New Instance Created - Property");
					}
				}
			}
		}

		//write the end of this element merge to the log
		master_log.finishMerge();

		//the previous section changed references to the relations by the master element
		//this method changes the relations in the relations folder themselves to have the proper source/target
		copyRelations(master, lesser, model.getFolder(FolderType.RELATIONS));
			
		//loop through all of the diagrams the lesser element is referenced in
		for(Object ob : DiagramModelUtils.findReferencedDiagramsForArchimateConcept(lesser).toArray()){
			//loop through all the instances of lesser in this diagram 
			for(Object i : DiagramModelUtils.findDiagramModelComponentsForArchimateConcept((IDiagramModel) ob, lesser).toArray()){
				if(i instanceof IDiagramModelArchimateComponent){
					//change the components baking Archimate concept
					((IDiagramModelArchimateComponent) i).setArchimateConcept(master);
					//change the id
					((IDiagramModelArchimateComponent) i).setId(master.getId());
					//change the name
					((IDiagramModelArchimateComponent) i).setName(master.getName());
					//add this instance to the master element's list 
					DiagramModelUtils.findDiagramModelComponentsForArchimateConcept((IDiagramModel) ob, master).add((IDiagramModelArchimateComponent) i);
				}
			}
		}
		
		//loop through the diagram objects
		for(Object i : ((IArchimateElement) lesser).getReferencingDiagramObjects()){
			//add them to the master element
			((IArchimateElement) master).getReferencingDiagramObjects().add((IDiagramModelArchimateObject) i);
		}
		
		//add lesser to the delete array 
		to_delete[0] = lesser;
		
		//create a new instance of the DCH
		dch = new DeleteCommandHandler(tmv.getViewer(), to_delete);
		
		//delete the lesser element 
		dch.delete();
		
		try{
			//save the model
			IEditorModelManager.INSTANCE.saveModel(model);
		}
		catch (IOException ioe){ //need the catch statement since saveModel() contains an IO operation
			System.out.println("IOException error");
		}
	}
	
	/**
	 * Recurses through the Relations folder of the model
	 * If a relation's source/target is the lesser element, change it to master
	 * @param master the master element
	 * @param lesser the lesser element
	 * @param f the relations folder of the model 
	 */
	private void copyRelations(ArchimateConcept master, ArchimateConcept lesser, IFolder f){
		//loop through the elements of the folder 
		for(Object o : f.getElements().toArray()){
			if(o instanceof IArchimateRelationship){
				//if the source is the lesser element
				if(((IArchimateRelationship) o).getSource().getId().equals(lesser.getId())){
					//switch it to master
					((IArchimateRelationship) o).setSource(master);
					
				}
				//if the target is the lesser element 
				if(((IArchimateRelationship) o).getTarget().getId().equals(lesser.getId())){
					//switch it to master
					((IArchimateRelationship) o).setTarget(master);
				}
			}
		}
		
		//loop through the folders in the Relations folder
		for(Object o : f.getFolders().toArray()){
			if(o instanceof IFolder){
				//recurse through the folders 
				copyRelations(master, lesser, (IFolder) o);
			}
		}
		
	}
	
	/**
	 * Sets the log file for the element merge
	 * @param l the log file 
	 */
	void setLH(LogHTML l){
		master_log = l; 
	}

	@Override
	public boolean isEnabled() {
		//get the selections of the Tree Model View
		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		
		//If nothing is selected, disable the plugin 
		if(sel.isEmpty()){
			return false;
		}
		TreePath[] tp = ((TreeSelection) sel).getPaths();
		
		if(!(tp[0].getLastSegment() instanceof ArchimateConcept)) return false;
	
		//if the elements are not of the same type, disable the plugin 
		if(!(tp[0].getLastSegment().getClass().equals(tp[1].getLastSegment().getClass()))){
			return false;
		}
		
		//passed all the tests, enable the plugin 
		return true;
	}

	@Override
	public boolean isHandled() {
		//Determines whether this operation can be performed again
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {

	}

}
