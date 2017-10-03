package com.archimatetool.costing;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.costing.wizard.IllegalPropertyValue;
import com.archimatetool.costing.wizard.NoUnderscore;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IProperty;

/**
 * This class represents a group of WizardPage elements that share the same line
 * Allows for the dynamic adding and grouping of new properties
 * @author Billy Hinard
 *
 */
public class PropertyGroup {
	//The label that stores the key of the property
	private Label label;
	//The text that stores the key of a new property
	private Text text_1;
	//The text that stores the value of the property
	private Text text;
	//The delete button of the line
	private Button button; 
	//The property that this property group represents
	private IProperty p; 
	
	/**
	 * Constructor to initialize a property group for a property that previous existed
	 * @param label The key of the property
	 * @param text The value of the property
	 * @param button The delete button of the row
	 * @param p the property that this group represents
	 */
	public PropertyGroup(Label label, Text text, Button button, IProperty p){
		//Store all of the variables
		this.label = label; 
		this.text = text; 
		this.button = button;
		this.p = p; 
	}
	
	/**
	 * Constructor to initialize a property group for an empty new property
	 * @param text_1 The empty key of the property
	 * @param text The empty value of the property
	 * @param button The delete button of the row
	 * @param p the property that this group represents
	 */
	public PropertyGroup(Text text_1, Text text, Button button, IProperty p) {
		//Store all of the variables
		this.text_1 = text_1;
		this.text = text; 
		this.button = button;
		this.p = p; 
	}

	/**
	 * Disposes of all of the elements in this group
	 */
	public void delete(){
		//Checks which elements are not null, then disposes them 
		if(label != null)
			label.dispose();
		if(text != null)
			text.dispose();
		if(text_1 != null)
				text_1.dispose();
		if(button != null)
			button.dispose();
	}
	
	/**
	 * When the Wizard is closed, update all the property values based on the user changes
	 * @return The updated property 
	 */
	public IProperty finish(){
		//if the property didn't already exist
		if(p == null){
			//if the user added a property key and value
			if(!(text_1.getText().isEmpty() || text.getText().isEmpty())){
				//create the new property
				IProperty new_p = IArchimateFactory.eINSTANCE.createProperty();
				//Set the key of the property
				new_p.setKey(text_1.getText());
				//set the value of the property
				new_p.setValue(text.getText());
				//return the property 
				return new_p; 
			}
		}
		//if the property already existed
		else{
			//update the property value
			p.setValue(text.getText());
		}
		
		//returns null if there is no new property to add
		return null;
	}
	
	/**
	 * Checks if the number is a valid number
	 * @return False if the number isn't valid, true if it is
	 */
	public boolean check(){
		//if this is a newly added property
		if(text_1 != null){
			//if the property name isn't empty
			if(!(text_1.getText().isEmpty())){
				//if the property name doesnt begin with an underscore
				if(text_1.getText().charAt(0) != '_'){
					//warn the user of this
					NoUnderscore nu = new NoUnderscore(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
					nu.open();
					//don't exit the wizard
					return false;
				}
			}
		}
		//if the property value is empty
		if(text.getText().isEmpty()){
			//okay to exit because it won't be changed in the property list
			return true;
		}
		//if the property value isn't null
		if(text.getText() != null){
			try{
				///try to convert it to a double
				Double.parseDouble(text.getText());
				//if it passes, we can exit the wizard
				return true; 
			}
			//if it fails
			catch(NumberFormatException nfe){
				//warn the user of this
				IllegalPropertyValue ipv = new IllegalPropertyValue(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				ipv.open();
				//don't exit the wizard
				return false; 
			}
		}
		//if the value is null for some reason
		else{
			//warn the user of this
			IllegalPropertyValue ipv = new IllegalPropertyValue(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			ipv.open();
			//don't exit the wizard
			return false;
		} 
	}
}
