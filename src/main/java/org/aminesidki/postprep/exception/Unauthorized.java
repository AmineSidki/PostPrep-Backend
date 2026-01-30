package org.aminesidki.postprep.exception;

public class Unauthorized extends RuntimeException {
    public Unauthorized(String message) {
        super(message);
    }
}