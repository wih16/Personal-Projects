package com.archimatetool.merge;

import org.eclipse.jface.wizard.Wizard;

public class MasterElement extends Wizard {
	String one, two;
	int selection;
	MasterElementPage mep;

	public MasterElement(String one, String two) {
		setWindowTitle("Master Element");
		this.one = one; 
		this.two = two;
		selection = 0; 
		mep = null;
	}

	@Override
	public void addPages() {
		mep = new MasterElementPage(one, two);
		addPage(mep);
	}
	public int getSelection(){ return selection;}

	@Override
	public boolean performFinish() {
		selection = mep.getSelection();
		return true;
	}

}
