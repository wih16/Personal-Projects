package com.archimatetool.costing.wizard;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.wizard.Wizard;

import com.archimatetool.model.IProperty;

public class EnterDataWizard extends Wizard {
	//the name of the selected object
	private String name;
	//the list of properties in the element
	private EList<IProperty> properties;
	//the element page to be added
	private EnterDataWizardPage edwp;
	//the connection page to be added 
	private EnterDataRelationWizardPage edrwp; 
	//determines which page to add
	private boolean page;

	/**
	 * Constructor of the Wizard
	 * @param name the name of the selected object
	 * @param properties the properties of the object
	 * @param page 
	 */
	public EnterDataWizard(String name, EList<IProperty> properties, boolean page) {
		setWindowTitle("Enter Costing Data");
		this.name = name; 
		this.properties = properties; 
		this.page = page; 
	}

	@Override
	public void addPages() {
		//add the page 
		if(page){
			edwp = new EnterDataWizardPage(name, properties); 
			addPage(edwp);
		}
		else{
			edrwp = new EnterDataRelationWizardPage(properties);
			addPage(edrwp);
		}
		 
	}

	@Override
	public boolean performFinish() {
		//finish all of the property operations
		if(page)
			if(edwp.finish()){
				return true; 
			}
			else{
				return false; 
			}
		else
			if(edrwp.finish()){
				return true; 
			}
			else{
				return false; 
			}
	}

}
