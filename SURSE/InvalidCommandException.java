package org.example;

public class InvalidCommandException extends Exception {
    public InvalidCommandException() {
        super("Command does not exist!");
    }
}