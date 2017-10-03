package com.archimatetool.costing.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IProperty;

public class PercentageChangeWizard extends Wizard {
	IArchimateConcept ac; 
	PercentageChangeWizardPage page;
	
	public PercentageChangeWizard(IArchimateConcept ac, PercentageCheck pc){
		setWindowTitle("Adjust the Percentages");
		this.ac = ac;
		pc.close();
	}

	@Override
	public void addPages() {
		page = new PercentageChangeWizardPage(ac);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		page.finish();
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
		return true;
	}

}
