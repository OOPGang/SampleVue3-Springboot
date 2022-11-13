package oop.io.demo.exception;

public class EmailFailToSendException extends Exception{
    public EmailFailToSendException(String message) {
        super(message);
    }
}
