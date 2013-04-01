package com.example.testapp.contactitem;

import java.io.Serializable;

public abstract class ContactItem implements Comparable<ContactItem>, Serializable {
	private static final long serialVersionUID = -8724971676019998236L;

	public static final int TYPE_CONTACT_TITLE	= 0;
	public static final int TYPE_CONTACT_PERSON	= 1;
	public static final int TYPE_CONTACT_SEP	= 2;
	
	private int type;
	
	public ContactItem(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public int compareTo(ContactItem b) {
		if(type == TYPE_CONTACT_TITLE) {
			ContactTitle me = (ContactTitle) this;
			ContactTitle other = (ContactTitle) b;
			if(me.title.equals("GENERAL") && !other.title.equals("GENERAL"))
				return 1;
			if(other.title.equals("GENERAL") && !me.title.equals("GENERAL"))
				return -1;
			return me.title.compareTo(other.title);
		} else if(type == TYPE_CONTACT_PERSON) {
			ContactPerson me = (ContactPerson) this;
			ContactPerson other = (ContactPerson) b;
			return me.name.compareTo(other.name);
		} else 
			return 0;
	}
}
