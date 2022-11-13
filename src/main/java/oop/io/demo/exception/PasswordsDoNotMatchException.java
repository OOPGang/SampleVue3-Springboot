package oop.io.demo.exception;

public class PasswordsDoNotMatchException extends Exception {
    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
