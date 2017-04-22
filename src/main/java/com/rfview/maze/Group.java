package com.rfview.maze;

import java.util.LinkedList;
import java.util.List;

public class Group {

	private String id;
    private String name;
    private AccessControlLevel acl ;

    private final List<User> users = new LinkedList<>();
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AccessControlLevel getAcl() {
		return acl;
	}

	public void setAcl(AccessControlLevel acl) {
		this.acl = acl;
	}

	public List<User> getUsers() {
		return users;
	}    
}
