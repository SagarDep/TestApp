package com.dev.pagge.biobollen.contactitem;

import java.io.Serializable;

public class ContactTitle extends ContactItem implements Serializable{
	private static final long serialVersionUID = -3228115984001291652L;

	public String title;
	
	public ContactTitle() {
		super(ContactItem.TYPE_CONTACT_TITLE);
	}
}
