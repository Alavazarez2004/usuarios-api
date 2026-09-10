package com.ejemplo.usuarios.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Email o contrasena invalidos");
    }
}
