package com.archimatetool.costing;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IDiagramModelObject;

/**
 * Contains an adjustment method to set the X/Y coordinates of an element
 * Determines the diagram model coordinates of an element in a group
 * @author Billy Hinard
 *
 */
public class GroupAdjust {
	
	
	/**
	 * Adjusts the X/Y coordinates of an element depending on it's container. 
	 * If the container is a group, you adjust accordingly
	 * @param element The element with bounds you want to adjust
	 * @return IBounds of the new bounds of the element
	 */
	public IBounds adjust(IDiagramModelObject element){
		//create new bounds
		IBounds new_bounds = IArchimateFactory.eINSTANCE.createBounds();
		//get the current bounds of the element
		IBounds bounds = element.getBounds();
		//if the element is in a group
		if(element.eContainer() instanceof IDiagramModelGroup){
			//add the X/Y of the group to the X/Y of the element 
			new_bounds.setX(bounds.getX() + ((IDiagramModelGroup) element.eContainer()).getBounds().getX()); 
			new_bounds.setY(bounds.getY() + ((IDiagramModelGroup) element.eContainer()).getBounds().getY()); 
		}
		//Otherwise
		else{
			//just copy over the X/Y
			new_bounds.setX(bounds.getX());
			new_bounds.setY(bounds.getY());
		}
		//Set the width and height, they are the same between elements
		new_bounds.setWidth(bounds.getWidth());
		new_bounds.setHeight(bounds.getHeight());
		
		//return the new bounds
		return new_bounds;
	}
}
