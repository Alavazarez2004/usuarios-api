package com.ejemplo.usuarios.exception;

import com.ejemplo.usuarios.dto.MensajeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - datos invalidos o incompletos (@Valid en el DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // 400 - email ya registrado
    @ExceptionHandler(EmailYaExisteException.class)
    public ResponseEntity<MensajeResponse> handleEmailDuplicado(EmailYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MensajeResponse(ex.getMessage()));
    }

    // 401 - login fallido
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<MensajeResponse> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MensajeResponse(ex.getMessage()));
    }

    // 404 - usuario no encontrado
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<MensajeResponse> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponse(ex.getMessage()));
    }
}
