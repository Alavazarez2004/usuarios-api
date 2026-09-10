package com.ejemplo.usuarios.controller;

import com.ejemplo.usuarios.dto.MensajeResponse;
import com.ejemplo.usuarios.dto.UpdateUsuarioRequest;
import com.ejemplo.usuarios.dto.UsuarioResponse;
import com.ejemplo.usuarios.entity.Usuario;
import com.ejemplo.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /api/v1/users
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodos()
                .stream()
                .map(UsuarioResponse::new)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    // GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(new UsuarioResponse(usuario));
    }

    // PUT /api/v1/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateUsuarioRequest request) {
        Usuario actualizado = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(new UsuarioResponse(actualizado));
    }

    // DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Usuario desactivado correctamente"));
    }
}
