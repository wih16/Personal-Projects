package com.archimatetool.costing.wizard;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IProperty;

import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.layout.GridData;

public class EnterDataRelationWizardPage extends WizardPage {
	private Text text;
	private Text text_1;
	private EList<IProperty> properties;
	
	/**
	 * Create the wizard.
	 * @param properties 
	 */
	public EnterDataRelationWizardPage(EList<IProperty> properties) {
		super("wizardPage");
		setTitle("Enter Relationship Percentage");
		setDescription("Enter the price percentage of this relationship");
		this.properties = properties; 
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblProperty = new Label(container, SWT.NONE);
		lblProperty.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblProperty.setText("Property");
		
		Label lblValue = new Label(container, SWT.NONE);
		lblValue.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblValue.setText("Value");
		
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		text_1 = new Text(container, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for(IProperty p : properties){
			if(p.getKey().charAt(0) == '%'){
				text.setText(p.getKey());
				text_1.setText(p.getValue());
				break;
			}
		}
	}

	/**
	 * Called by the finish method of EnterDataWizard
	 * Saves the percentage property of the Relation 
	 */
	public boolean finish() {
		try{
			//if the user entered a value for the percentage
			if(text_1.getText() != null){
				//convert the value into a double
				Double.parseDouble(text_1.getText());
				//continue if it passes
			}
		}
		//if it fails
		catch(NumberFormatException nfe){
			//warn the user of this
			IllegalPropertyValue ipv = new IllegalPropertyValue(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			ipv.open();
			//don't close the wizard
			return false;
		}
		
		//if the property name isn't null
		if(text.getText() != null){
			//if the property name doesn't begin with a percentage sign
			if(text.getText().charAt(0) != '%'){
				//warn the user of this
				NoPercentageSign nps = new NoPercentageSign(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()); 
				nps.open();
				//don't close the wizard
				return false;
			}
		}
		//check to see of a percentage already existed
		boolean check = false; 
		//loop through the properties
		for(Object p : properties.toArray()){
			//if the property is the percentage property
			if(((IProperty) p).getKey().charAt(0) == '%'){
				//update the key
				((IProperty) p).setKey(text.getText());
				//update the value
				((IProperty) p).setValue(text_1.getText());
				check = true; 
				break; 
			}
		}
		//if the percentage property didn't exist
		if(!check){
			///create the property
			IProperty p = IArchimateFactory.eINSTANCE.createProperty();
			p.setKey(text.getText());
			p.setValue(text.getText());
			properties.add(p);
		}
		return true; 
	}

}
