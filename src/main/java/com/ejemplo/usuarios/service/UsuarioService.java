package com.ejemplo.usuarios.service;

import com.ejemplo.usuarios.dto.RegisterRequest;
import com.ejemplo.usuarios.dto.UpdateUsuarioRequest;
import com.ejemplo.usuarios.entity.EstadoUsuario;
import com.ejemplo.usuarios.entity.Usuario;
import com.ejemplo.usuarios.exception.CredencialesInvalidasException;
import com.ejemplo.usuarios.exception.EmailYaExisteException;
import com.ejemplo.usuarios.exception.UsuarioNoEncontradoException;
import com.ejemplo.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailYaExisteException(request.getEmail());
        }

        String passwordHasheado = passwordEncoder.encode(request.getPassword());
        Usuario usuario = new Usuario(request.getNombre(), request.getEmail(), passwordHasheado);
        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new CredencialesInvalidasException();
        }

        return usuario;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    public Usuario actualizar(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = obtenerPorId(id);

        // Si cambia el email, validar que el nuevo no este en uso por otro usuario
        if (!usuario.getEmail().equalsIgnoreCase(request.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailYaExisteException(request.getEmail());
        }

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        return usuarioRepository.save(usuario);
    }

    // Se desactiva la cuenta en vez de borrarla fisicamente (soft delete)
    public void eliminar(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuarioRepository.save(usuario);
    }
}
