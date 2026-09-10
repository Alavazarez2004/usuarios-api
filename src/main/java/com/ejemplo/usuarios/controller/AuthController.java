package com.ejemplo.usuarios.controller;

import com.ejemplo.usuarios.dto.LoginRequest;
import com.ejemplo.usuarios.dto.MensajeResponse;
import com.ejemplo.usuarios.dto.RegisterRequest;
import com.ejemplo.usuarios.dto.UsuarioResponse;
import com.ejemplo.usuarios.entity.Usuario;
import com.ejemplo.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegisterRequest request) {
        Usuario nuevoUsuario = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponse(nuevoUsuario));
    }

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<MensajeResponse> login(@Valid @RequestBody LoginRequest request) {
        usuarioService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new MensajeResponse("Inicio de sesion exitoso"));
    }
}
