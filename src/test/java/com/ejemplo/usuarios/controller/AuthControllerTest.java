package com.ejemplo.usuarios.controller;

import com.ejemplo.usuarios.dto.RegisterRequest;
import com.ejemplo.usuarios.dto.UserResponse;
import com.ejemplo.usuarios.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void deberiaRetornar201YNoFugarContrasenaEnJSON() throws Exception {
        RegisterRequest request = new RegisterRequest("Carlos", "carlos@test.com", "PasswordSeguro123");
        UserResponse responseDto = new UserResponse(1L, "Carlos", "carlos@test.com", "ACTIVO");

        when(usuarioService.registrar(any(RegisterRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.datos.nombre").value("Carlos"))
                .andExpect(jsonPath("$.datos.email").value("carlos@test.com"))
                .andExpect(jsonPath("$.datos.password").doesNotExist());
    }
}
