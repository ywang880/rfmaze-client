package com.rfview.maze;

public class User {

    private String id;
    private String password;
    private String firstname;
    private String lastname;
    private String organization;
    private String email;
    private String phone;
    private String assigned_rows;
    private String assigned_col;
    private String expiration;
    private boolean adminPrivileage = false;
	private Group group;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAssigned_rows() {
        return assigned_rows;
    }

    public void setAssigned_rows(String assigned_rows) {
        this.assigned_rows = assigned_rows;
    }

    public String getAssigned_col() {
        return assigned_col;
    }

    public void setAssigned_col(String assigned_col) {
        this.assigned_col = assigned_col;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public boolean isAdminPrivileage() {
        return adminPrivileage;
    }

    public void setAdminPrivileage(boolean adminPrivileage) {
        this.adminPrivileage = adminPrivileage;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", password=" + password + ", firstname="
                + firstname + ", lastname=" + lastname + ", organization="
                + organization + ", email=" + email + ", phone=" + phone
                + ", assigned_rows=" + assigned_rows + ", assigned_col="
                + assigned_col + ", expiration=" + expiration + ", adminPrivileage=" + adminPrivileage + "]";
    }
}
