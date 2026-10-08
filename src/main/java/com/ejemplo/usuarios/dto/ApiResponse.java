package com.ejemplo.usuarios.dto;

public record ApiResponse<T>(
    boolean exito,
    String mensaje,
    T datos
) {
    public static <T> ApiResponse<T> exito(String mensaje, T datos) {
        return new ApiResponse<>(true, mensaje, datos);
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return new ApiResponse<>(false, mensaje, null);
    }
}