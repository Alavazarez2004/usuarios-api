package com.ejemplo.usuarios.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}
