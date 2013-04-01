package com.example.testapp.contactitem;

import java.io.Serializable;

public class ContactSep extends ContactItem implements Serializable {
	private static final long serialVersionUID = 312465842968506727L;

	public ContactSep() {
		super(ContactItem.TYPE_CONTACT_SEP);
	}

}
