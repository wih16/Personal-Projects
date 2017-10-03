package com.archimatetool.merge;

import org.eclipse.jface.wizard.Wizard;

public class MasterElement_Model extends Wizard {
	String one, two;
	int selection;
	MasterElementPage_Model mep;

	public MasterElement_Model(String one, String two) {
		setWindowTitle("Master Model");
		this.one = one; 
		this.two = two;
		selection = 0; 
		mep = null;
	}

	@Override
	public void addPages() {
		mep = new MasterElementPage_Model(one, two);
		addPage(mep);
	}
	public int getSelection(){ return selection;}

	@Override
	public boolean performFinish() {
		selection = mep.getSelection();
		return true;
	}

}
