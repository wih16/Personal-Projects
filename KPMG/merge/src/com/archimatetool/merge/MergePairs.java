package com.archimatetool.merge;

import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateConcept;

public class MergePairs {
	ArchimateConcept master, lesser;
	IProperty[] master_p, lesser_p;
	
	public MergePairs(ArchimateConcept master, ArchimateConcept lesser){
		this(master, lesser, null, null);
	}
	
	public MergePairs(ArchimateConcept master, ArchimateConcept lesser, Object[] master_p, Object[] lesser_p){
		this.master = master; 
		this.lesser = lesser;
		if(master_p != null && lesser_p != null){
			this.master_p = new IProperty[master_p.length];
			int i = 0;
			for(Object obj : master_p){
				this.master_p[i++] = (IProperty) obj;
			}
			this.lesser_p = new IProperty[lesser_p.length];
			i = 0;
			for(Object obj : lesser_p){
				this.lesser_p[i++] = (IProperty) obj;
			}
		}
		
	}
}
