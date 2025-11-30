package com.byron.cudeca.dto;

import com.byron.cudeca.model.CanalPago;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para recibir los datos necesarios para iniciar o finalizar un pedido.
 * El Front-end enviará esta estructura al Backend.
 */

@Data
public class PedidoRequestDTO {

    // ID del usuario o el NIF, se usará para obtener el Usuario
    @NotNull(message = "El NIF de usuario es obligatorio.")
    private Long userId;

    // ID del Evento
    @NotNull(message = "El ID de evento es obligatorio.")
    private Long eventoId;

    @Min(value = 1, message = "La cantidad mínima de entradas es 1.")
    private int cantidadEntradas;

    @NotNull(message = "El método de pago es obligatorio.")
    private CanalPago canalPago;

    @AssertTrue(message = "Los consentimientos obligatorios deben ser aceptados")
    private Boolean consentimientosObligatorios;

    private Boolean consentimientoNewsletter;

    // Solo se usará en el endpoint de finalizar, como token de confirmación simulado.
    private String tokenPagoExterno;
}