package org.example;

public class InformationIncompleteException extends Exception {
    public InformationIncompleteException() {
        super("Credentials are missing.");
    }
}