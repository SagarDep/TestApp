package com.example.testapp.contactitem;

import java.io.Serializable;

public class ContactPerson extends ContactItem implements Serializable {
	private static final long serialVersionUID = 5541733153038294998L;
	
	public String name;
	public String phone;
	public String title;
	
	public ContactPerson() {
		super(ContactItem.TYPE_CONTACT_PERSON);
	}
}
