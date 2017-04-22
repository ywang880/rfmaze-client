package com.rfview.exceptions;

public class InvalidConfigurationException extends Exception {

    private static final long serialVersionUID = -4845846576192968786L;
    private final String errorMessage;

    public InvalidConfigurationException(String message) {
        errorMessage = message;
    }    

    public String getErrorMessage() {
        return errorMessage;
    }
}
