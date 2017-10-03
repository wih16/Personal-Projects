package com.archimatetool.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateConcept;
import com.archimatetool.model.impl.ArchimateDiagramModel;
import com.archimatetool.model.impl.ArchimateModel;
import com.archimatetool.model.impl.ArchimateRelationship;

public class MergeModels implements IHandler {
	//the LogHTML class used to write the log file
	private LogHTML lh;
	//used when duplicate elements are present and need to be merged
	private MergeElements me;
	//the master and lesser archimate models being merged
	private ArchimateModel lesser, master;
	//an array of all of the duplicate elements between the two models
	private ArrayList<MergePairs> duplicates;
	//an array of all of the merged with exception pairs
	private ArrayList<MergePairs> exceptions;
	//a hashmap to store all the elements of the master models current folder
	private HashMap<String, ArchimateConcept> hash;
	//a hashmap to store the id's of the merged pairs
	private HashMap<String, Boolean> merges;
	//the current folder type
	private FolderType type;
	//stores the statistics of merges
	int successful, exception, instances; 
	
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//Initializing the popup dialog warning the user that the process is working
		PopupDialog pd = new PopupDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), PopupDialog.INFOPOPUP_SHELLSTYLE, true, false, false, false, false, "Merging Models", "\n\n\n\t\t\tPlease wait, process is working\t\t\t\n\n\n\n");
		//initializing variables
		me = new MergeElements();
		duplicates = new ArrayList<MergePairs>(); 
		hash = new HashMap<String, ArchimateConcept>();
		merges = new HashMap<String, Boolean>();
		exceptions = new ArrayList<MergePairs>();
		successful = 0; 
		exception = 0; 
		instances = 0; 
		
		//get the selections of the current tree
		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		
		//check that the selections are not empty 
		if(sel.isEmpty()){
			return false;
		}
		//An array of the current selections
		TreePath[] tp = ((TreeSelection) sel).getPaths();
		
		//checking that there are two selections
		if(tp.length != 2) return false;   
		
		//create a new wizard with the names of the elements 
		MasterElement_Model me = new MasterElement_Model(((ArchimateModel) tp[0].getFirstSegment()).getName(), ((ArchimateModel) tp[1].getFirstSegment()).getName());
		
		//create a new wizard dialog 
		WizardDialog wd = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), me){
			//this method sets the size of the wizard
			@Override
			protected void configureShell(Shell newShell){
				super.configureShell(newShell);
				newShell.setSize(496, 222);
			}
		};
		if(wd.open() == Window.CANCEL){
			return null;
		}
		
		//opening the popup dialog before the work begins
		pd.open();
		
		//the int representing the users master selection
		int check = me.getSelection();
		
		if(check == 1){
			//a will store the first model 
			master = (ArchimateModel) tp[0].getFirstSegment();
			//b stores the second model 
			lesser = (ArchimateModel) tp[1].getFirstSegment();
		}
		else{
			//a will store the first model 
			lesser = (ArchimateModel) tp[0].getFirstSegment();
			//b stores the second model 
			master = (ArchimateModel) tp[1].getFirstSegment();
		}
		
		//starting the log file
		lh = new LogHTML();
		lh.makeFile(master.getFile());
		lh.start(master.getName(), lesser.getName());
		
		//for each folder, copy over the information
		type = FolderType.STRATEGY;
		master = copyFolder(lesser.getFolder(FolderType.STRATEGY), master);
		type = FolderType.BUSINESS;
		master = copyFolder(lesser.getFolder(FolderType.BUSINESS), master);
		type = FolderType.APPLICATION;
		master = copyFolder(lesser.getFolder(FolderType.APPLICATION), master);
		type = FolderType.TECHNOLOGY;
		master = copyFolder(lesser.getFolder(FolderType.TECHNOLOGY), master);
		type = FolderType.MOTIVATION;
		master = copyFolder(lesser.getFolder(FolderType.MOTIVATION), master);
		type = FolderType.IMPLEMENTATION_MIGRATION;
		master = copyFolder(lesser.getFolder(FolderType.IMPLEMENTATION_MIGRATION), master);
		type = FolderType.OTHER;
		master = copyFolder(lesser.getFolder(FolderType.OTHER), master);
		//copying the relations folder using its special method
		master = copyFolderRelations(lesser.getFolder(FolderType.RELATIONS), master);
		//copying the diagrams folder using its special method
		master = copyFolderDiagrams(lesser.getFolder(FolderType.DIAGRAMS), master); 
		
		
		//getting the main model folder
		try{
			//save the model
			IEditorModelManager.INSTANCE.saveModel(master);
		}
		catch (IOException ioe){ //need the catch statement since saveModel() contains an IO operation
			System.out.println("IOException error");
		}
		
		//close the model 
		try{
			IEditorModelManager.INSTANCE.closeModel(lesser);
		}
		catch(IOException ioe){
			System.out.println("Error closing the model");
		}
		
		//finishing the process of merging
		finish();
		//finishing the log file
		lh.finish(successful, exception, instances);
		//closing the popup menu, the process is completed
		pd.close();
		return null;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * 	No check needs to be performed on the relationships, so simply just copy every relation from lesser to master. 
	 * @param a the Relations folder of the lesser element
	 * @param b the master element
	 * @return the updated master model
	 * <!-- end-user-doc -->
	 */
	private ArchimateModel copyFolderRelations(IFolder a, ArchimateModel b){
		//start the relations section of the log file
		lh.startFolder("RELATIONS");
		//loop through all the elements not in a nested folder
		for(Object e : a.getElements().toArray()){
			if(e instanceof ArchimateRelationship){
				//add the elements to the Relations folder of b
				b.getFolder(FolderType.RELATIONS).getElements().add((EObject) e);
				//write this relation to the log file
				if(!(merges.isEmpty())){
					if(!(merges.get(((ArchimateRelationship) e).getSource().getId()) || merges.get(((ArchimateRelationship) e).getTarget().getId()))){
						lh.writeElements((ArchimateConcept) e, "New Instance Created" );
					}
				}
			}
		}
		lh.finishFolder();
		//loop through all the nested folders of the Relations folder
		for(Object f : a.getFolders().toArray()){
			if(f instanceof IFolder){
				//add the folders to the Relations folder of b
				b.getFolder(FolderType.RELATIONS).getFolders().add((IFolder) f);
				//the folders were copied, but we want to write to the log file the elements in these folders
				writeRelations((IFolder)f);
			}
		}
		
		//return the master model
		return b;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Write the elements of the nested folders of the Relations folder to the log file. Recurses through infinite folder depths. 
	 * @param f the nested folder
	 * <!-- end-user-doc -->
	 */
	private void writeRelations(IFolder f){
		//start the nested folder
		lh.startNested(f.getName());
		//loop through each element in this folder
		for(Object e : f.getElements().toArray()){
			if(e instanceof ArchimateRelationship){
				//write this relationship to the log file
				if(!(merges.containsKey(((ArchimateRelationship) e).getSource().getId()) || merges.containsKey(((ArchimateRelationship) e).getTarget().getId()))){
					lh.writeElements((ArchimateConcept) e, "New Instance Created" );
				}
			}
		}
		lh.finishFolder();
		//loop through each folder in this folder
		for(Object o : f.getFolders().toArray()){
			if(o instanceof IFolder){
				//recurse with each folder
				writeRelations((IFolder)o);
			}
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * 	No check needs to be performed on the diagrams, so simply just copy every diagram from lesser to master. 
	 * @param a the Diagrams folder of the lesser element
	 * @param b the master element
	 * @return the updated master model
	 * <!-- end-user-doc -->
	 */
	private ArchimateModel copyFolderDiagrams(IFolder a, ArchimateModel b){
		//starting the Diagrams section of the log file
		lh.startFolder("DIAGRAMS");
		//looping through each element in the folder
		for(Object e : a.getElements().toArray()){
			if(e instanceof ArchimateDiagramModel){
				//add the diagram to the Diagram folder of b
				b.getFolder(FolderType.DIAGRAMS).getElements().add((EObject) e);
				//write this diagram to the log file
				lh.writeElements((ArchimateDiagramModel) e, "Diagram Copied");
			}
		}
		lh.finishFolder();
		//loop through each of the folders in this folder
		for(Object f : a.getFolders().toArray()){
			if(f instanceof IFolder){
				//add the folders to the Diagram folder of b
				b.getFolder(FolderType.DIAGRAMS).getFolders().add((IFolder) f);
				//need to write the elements in these folders to the log file
				writeDiagrams((IFolder) f);
			}
		}
		return b;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Write the elements of the nested folders of the Diagrams folder to the log file. Recurses through infinite folder depths. 
	 * @param f the nested folder
	 * <!-- end-user-doc -->
	 */
	private void writeDiagrams(IFolder f){
		//starting the nested folder's section in the log file
		lh.startNested(f.getName());
		//loop through each element in this folder
		for(Object e : f.getElements().toArray()){
			if(e instanceof ArchimateDiagramModel){
				//write the diagram to the log folder
				lh.writeElements((ArchimateDiagramModel) e, "Diagram Copied" );
			}
		}
		lh.finishFolder();
		//loop through the folders of this folder
		for(Object o : f.getFolders().toArray()){
			if(o instanceof IFolder){
				//recurse for each nested folder
				writeDiagrams((IFolder)o);
			}
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * The elements not in the relations or diagrams folder need to be checked against the elements of the master model. 
	 * This method checks each lesser element against the master elements and then adds or removes the elements accordingly.
	 * @param a the folder from the lesser model being added to the master
	 * @param b the master model
	 * @return the updated master model
	 * <!-- end-user-doc -->
	 */
	private ArchimateModel copyFolder(IFolder a, ArchimateModel b){
		//store all of the elements in the folder into an array
		EObject[] iter = (EObject[]) a.getElements().toArray();
		
		//store all of the folders in the folder into an array
		EObject[] f_iter = (EObject[]) a.getFolders().toArray();
		
		//making the HashMap for all of the elements in B, but only if B has elements
		if((iter.length != 0 || f_iter.length != 0)){
			//clear the hashMap before hand
			hash.clear();
			//method that makes the hash, stored in a global variable
			makeHash(b.getFolder(type));
		}
		
		//starting this folder's section of the log file
		lh.startFolder(type.toString().toUpperCase());
		
		//if this folder has elements
		if(iter.length != 0){
						
			//for each element in the array, copy it over to the other model if it doesn't already exist 
			for(EObject obj : iter){
				if(obj instanceof ArchimateConcept){
					ArchimateConcept less = (ArchimateConcept) obj;
					//if the master model already contains this element
					if(hash.containsKey(((ArchimateConcept) obj).getId())){	
						ArchimateConcept mast = hash.get(less.getId());
						//create a new pair
						MergePairs mp = new MergePairs(mast, less);
						//add the pair to the array
						duplicates.add(mp);
						merges.put(mast.getId(), true);
						//write this to the log file
						if(checkProperties(mast, less) && mast.getName().equals(less.getName())){
							//The elements are the same, so it will be a successful merge
							lh.writeElements((ArchimateConcept) obj, "<td style='background-color: #a7ff8b'>Successfully Merged</td>");
							successful++;
						}
						else{
							//the elements are different, merge with exceptions
							lh.writeElements((ArchimateConcept) obj, "<td style='background-color: #f9e77d'>Merged with Exceptions</td>");
							mp = new MergePairs(mast, less, mast.getProperties().toArray(), less.getProperties().toArray());
							exceptions.add(mp);
							exception++;
						}
					}
					//the master model did not contain this element
					else{
						//add the element to the appropriate folder
						b.getFolder(type).getElements().add(obj);
						//write this to the log file
						lh.writeElements((ArchimateConcept) obj, "<td style='background-color: #7db1f9'>New Instance Created</td>");
						instances++; 
					}
				}
			}
		}
		
		//loop through all of the folders in this array
		for(Object obj : f_iter){
			if(obj instanceof IFolder){
				//call the recursive nested function that checks for duplicates
				copyNested((IFolder) obj);
				//add the nested folder to the folder in b
				b.getFolder(type).getFolders().add((IFolder) obj);
			}
		}	
		
		lh.finishFolder();
		return b;
	}
	
	private boolean checkProperties(ArchimateConcept a, ArchimateConcept b){
		for(IProperty ip : a.getProperties()){
			for(IProperty p : b.getProperties()){
				if(!(ip.getValue().equals(p.getValue()))){
					return false;
				}
			}
		}
		return true; 
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Loops through the nested folder and checks if the elements exist in the master model already 
	 * It does not add or remove any elements, just marks if the will in the future be removed by finish()
	 * @param f the nested folder
	 * <!-- end-user-doc -->
	 */
	private void copyNested(IFolder f){
		//starting the nested folder's section in the log file
		lh.startNested(f.getName());
		//loop through all of the elements in this folder
		for(Object obj : f.getElements().toArray()){
			if(obj instanceof ArchimateConcept){
				ArchimateConcept less = (ArchimateConcept) obj;
				//if the master model already contains this element
				if(hash.containsKey(((ArchimateConcept) obj).getId())){	
					ArchimateConcept mast = hash.get(less.getId());
					//create a new pair
					MergePairs mp = new MergePairs(mast, less);
					//add the pair to the array
					duplicates.add(mp);
					merges.put(mast.getId(), true);
					//write this to the log file
					if(checkProperties(mast, less)){
						//The elements are the same, so it will be a successful merge
						lh.writeElements((ArchimateConcept) obj, "<td style='background-color: #a7ff8b'>Successfully Merged</td>");
						successful++;
					}
					else{
						//the elements are different, merge with exceptions
						lh.writeElements((ArchimateConcept) obj, "<td style='background-color: #f9e77d'>Merged with Exceptions</td>");
						mp = new MergePairs(mast, less, mast.getProperties().toArray(), less.getProperties().toArray());
						exceptions.add(mp);
						exception++;
					}
				}
				//the master model did not contain this element
				else{
					//write this to the log file
					lh.writeElements((ArchimateConcept) obj, "<td style='background-color: #7db1f9'>New Instance Created</td>");
					instances++; 
				}
			}
		}
		lh.finishFolder();
		
		//loop through all of the folders in this folder
		for(Object obj : f.getFolders().toArray()){
			if(obj instanceof IFolder){
				//recurse through each folder
				copyNested((IFolder) obj);
			}
		}	
	}

	/**
	 * <!-- begin-user-doc -->
	 *  Creates the HashMap for each folder in B. This allows for efficient searching of the elements of the master element
	 *  @param f the folder whose elements are being added to the HashMap
	 * <!-- end-user-doc -->
	 */
	private void makeHash(IFolder f){
		//loop through each element in this folder
		for(Object e : f.getElements().toArray()){
			if(e instanceof ArchimateConcept){
				//add the element to the HashMapo
				hash.put(((ArchimateConcept) e).getId(), (ArchimateConcept) e);
			}
		}
		
		//loop through each folder in this folder
		for(Object o : f.getFolders().toArray()){
			if(o instanceof IFolder){
				//recurse through each folder
				makeHash((IFolder) o);
			}
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * Finishes the process by merging all of the duplicate elements
	 * <!-- end-user-doc -->
	 */
	private void finish(){
		//sets the log file
		me.setLH(lh);
		//starts the merge section of the log file
		lh.startMerge();
		//loops through all of the duplicates
		for(MergePairs mp : duplicates){
			//merge the duplicates
			me.execute_from_master(mp.master, mp.lesser, master);
		}
		lh.startMergeExceptions();
		for(MergePairs mp : exceptions){
			writeExceptions(mp);
		}
	}
	
	/**
	 * Writes all of the "Merged with Exceptions" exceptions to the log file
	 * @param mp the pair of elements that were merged with an exception
	 */
	private void writeExceptions(MergePairs mp){
		ArchimateConcept lesser = mp.lesser; 
		ArchimateConcept master = mp.master; 
		
		lh.startException(master, lesser);
		
		if(!(master.getName().equals(lesser.getName()))){
			lh.startName(master.getName(), lesser.getName());
		}
		if(mp.master_p != null && mp.lesser_p != null){
			if(!(mp.master_p.toString().equals(mp.lesser_p.toString()))){
				lh.startProperty();
				for(Object p : mp.master_p){
					for(Object q : mp.lesser_p){
						if(((IProperty) p).getKey().equals(((IProperty) q).getKey())){
							if(!(((IProperty) p).getValue().equals(((IProperty) q).getValue()))){
								lh.addProperty(((IProperty) p).getKey(), ((IProperty) p).getValue(), ((IProperty) q).getValue());
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		
		//sel returns an array of everything that is selected
		//just check that the array is just two archimate models (do this by checking class type)
		if(sel.isEmpty()){
			return false;
		}
		TreePath[] tp = ((TreeSelection) sel).getPaths();
		
		if(tp.length != 2) return false;   
		
		ArchimateModel a = (ArchimateModel) tp[0].getFirstSegment();
		ArchimateModel b = (ArchimateModel) tp[1].getFirstSegment();
		
		if(a.getFile() == null) return false; 
		if(b.getFile() == null) return false; 
		
		if(a.getFile().getAbsolutePath().equals(b.getFile().getAbsolutePath())){
			 return false; 
		}	

		return true;
	}

	@Override
	public boolean isHandled() {
		
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		
	}

}
