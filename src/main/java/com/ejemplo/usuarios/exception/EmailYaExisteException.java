package com.ejemplo.usuarios.exception;

public class EmailYaExisteException extends RuntimeException {
    public EmailYaExisteException(String email) {
        super("El email '" + email + "' ya esta registrado");
    }
}
