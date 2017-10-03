package com.archimatetool.costing.wizard;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IProperty;

public class PercentageChangeWizardPage extends WizardPage {
	
	EList<IArchimateRelationship> relations;
	private Label lblNewLabel;
	private Label lblValues; 
	private int relation_index;
	private int property_index; 
	private int[] array; 
	private Text[] new_values; 
	private Label lblConnection;
	
	/**
	 * Create the wizard.
	 */
	public PercentageChangeWizardPage(IArchimateConcept ac) {
		super("Adjust Percentages");
		setTitle("Adjust the Percentages");
		setDescription("Adjust the percentages for " + ac.getName() + " so that they equal 100%");
		relations = ac.getSourceRelationships();
		relation_index = 0; 
		property_index = 0;
		array = new int[ac.getSourceRelationships().toArray().length];
		new_values = new Text[ac.getSourceRelationships().toArray().length];
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		//setting a 3 column grid layout
		container.setLayout(new GridLayout(3, false));
		
		lblConnection = new Label(container, SWT.NONE);
		lblConnection.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblConnection.setText("Connection");
		
		//The Properties Label
		lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblNewLabel.setText("Properties");
		
		//The Values Label
		lblValues = new Label(container, SWT.NONE);
		lblValues.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblValues.setText("Values");
		
		//Loop through the list of properties
		for(Object ar : relations.toArray()){
			property_index = 0; 
			for(IProperty p : ((IArchimateRelationship) ar).getProperties()){
				if(p.getKey().charAt(0) == '%'){
					//label of the source to target
					Label elementLabel = new Label(container, SWT.NONE);
					elementLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
					elementLabel.setText(defaultName(((IArchimateRelationship) ar).getSource()) + " to " + defaultName(((IArchimateRelationship) ar).getTarget()));

					
					//Label of the property key
					Label addLabel = new Label(container, SWT.NONE);
					addLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
					addLabel.setText(p.getKey());
					
					//Editable text of the property value
					Text addText = new Text(container, SWT.BORDER);
					addText.setText(p.getValue());
					addText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					array[relation_index] = property_index; 
					new_values[relation_index] = addText;
					break;
				}
				array[relation_index] = -1;
				property_index++; 
			}
			relation_index++; 
		}
	}
	
	public void finish(){
		for(int i = 0; i < array.length; i++){
			if(new_values[i] != null){
				if(array[i] != -1){
					relations.get(i).getProperties().get(array[i]).setValue(new_values[i].getText());
				}
			}
		}
		
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
