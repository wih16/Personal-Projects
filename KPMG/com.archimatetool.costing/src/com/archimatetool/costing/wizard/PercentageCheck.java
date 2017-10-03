package com.archimatetool.costing.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IProperty;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class PercentageCheck extends Dialog {

	private IArchimateConcept ac; 
	private PercentageCheck pc; 
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public PercentageCheck(Shell parentShell, IArchimateConcept ac) {
		super(parentShell);
		this.ac = ac; 
		pc = this; 
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("WARNING: The source relationships of " + ac.getName() + " equal " + percentage() + "%");
		
		Button btnAdjustThePercentages = new Button(container, SWT.NONE);
		btnAdjustThePercentages.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				PercentageChangeWizard wiz = new PercentageChangeWizard(ac, pc);
				//create a new wizard dialog 
				WizardDialog wd = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
				//opens the wizard, if it is canceled during execution, return null
				if(wd.open() != Window.CANCEL){
					
				}
			}
		});
		btnAdjustThePercentages.setText("Adjust the Percentages");

		return container;
	}
	
	private String percentage(){
		double perc = 0; 
		for(Object o : ac.getSourceRelationships().toArray()){
			if(o instanceof IArchimateRelationship){
				for(Object ob : ((IArchimateRelationship) o).getProperties().toArray()){
					if(ob instanceof IProperty){
						if(((IProperty) ob).getKey().charAt(0) == '%')
							perc += Double.parseDouble(((IProperty) ob).getValue());
					}
				}
			}
		} 
		return String.format("%1$,.2f", perc);
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(462, 139);
	}

}
