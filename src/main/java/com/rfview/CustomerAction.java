package com.rfview;

import java.util.logging.Logger;

import com.opensymphony.xwork2.ActionSupport;

public class CustomerAction extends ActionSupport {

    private static final long serialVersionUID = 3257905400827766452L;
    private String name;
    private Integer age;
    private String email;
    private String telephone;
    private Logger logger = Logger.getLogger(CustomerAction.class.getName());

    public CustomerAction() {
        logger.info("CustomerAction");
    }

    public String addCustomer() {
        return SUCCESS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

}
