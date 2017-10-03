package com.archimatetool.merge;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MasterElementPage extends WizardPage {

	String radio_1, radio_2;
	int selection; 
	/**
	 * Create the wizard.
	 */
	public MasterElementPage(String one, String two) {
		super("wizardPage");
		setTitle("Select Master Element");
		setDescription("The master element will maintain its name and description");
		radio_1 = one; 
		radio_2 = two;
		selection = 1;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(6, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Select Master Element");
		
		Button btnRadioButton = new Button(container, SWT.RADIO);
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selection = 1;
			}
		});
		btnRadioButton.setText(radio_1);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnRadioButton_1 = new Button(container, SWT.RADIO);
		btnRadioButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selection = 2;
			}
		});
		btnRadioButton_1.setText(radio_2);

	}

	public int getSelection(){
		return selection; 
	}
}
