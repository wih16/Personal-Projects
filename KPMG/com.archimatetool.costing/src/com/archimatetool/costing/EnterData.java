package com.archimatetool.costing;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.costing.wizard.EnterDataWizard;
import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;

/**
 * Handles the menu item "Enter Costing Data" when the user wants to add costs to an element
 * Provides an easier UI to update properties rather than the default Archimate UI
 * @author Billy Hinard
 *
 */
public class EnterData implements IHandler{

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//initializing the archimate concept
		IArchimateConcept ac = null;
		//determines which page to use, true for object, false for connection; 
		boolean page = false;
		//get the array of selected object 
		Object[] array = ((StructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection()).toArray();
		//if the element is a IDiagramModelArchimateObject
		if(array[0] instanceof IDiagramModelArchimateObject){
			//set ac to the corresponding archimate concept
			ac = ((IDiagramModelArchimateObject) array[0]).getArchimateConcept(); 
			page = true;
		}
		//if the element is an ArchimateElementEditPart
		else if(array[0] instanceof ArchimateElementEditPart){
			//set ac to the corresponding archimate concept
			IDiagramModelArchimateObject dmao = ((ArchimateElementEditPart) array[0]).getModel(); 
			ac = dmao.getArchimateConcept();
			page = true;
		}
		//if the element is a IDiagramModelArchimateConnection
		else if(array[0] instanceof IDiagramModelArchimateConnection){
			//set ac to the corresponding archimate concept
			ac = ((IDiagramModelArchimateConnection) array[0]).getArchimateConcept(); 
		}
		//if the element is an ArchimateRelationshipEditPart
		else if(array[0] instanceof ArchimateRelationshipEditPart){
			//set ac to the corresponding archimate concept
			IDiagramModelArchimateConnection dmac = ((ArchimateRelationshipEditPart) array[0]).getModel(); 
			ac = dmac.getArchimateConcept();
		}
		//else, return
		else{
			return null; 
		}
		
		EnterDataWizard edw = new EnterDataWizard(ac.getName(), ac.getProperties(), page); 
		//create a new wizard dialog 
		WizardDialog wd = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), edw);
		//opens the wizard, if it is canceled during execution, return null
		if(wd.open() == Window.CANCEL){
			return null;
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		//get the array of selections
		Object[] array = ((StructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection()).toArray();

		//if there isn't one thing selected, return false
		if(array.length > 1){
			return false; 
		}
		else if(array.length == 0){
			return true;
		}
		
		//if it isn't any of the valid objects, return false
		if(!(array[0] instanceof IDiagramModelArchimateObject || array[0] instanceof ArchimateElementEditPart || array[0] instanceof IDiagramModelArchimateConnection || array[0] instanceof ArchimateRelationshipEditPart)){
			return false; 
		}
		
		//passed tests, return true
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