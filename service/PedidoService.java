package com.byron.cudeca.service;

import com.byron.cudeca.model.*;
import com.byron.cudeca.repository.*;
import jdk.jfr.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EventoRepository eventoRepository;
    private final EntradaRepository entradaRepository;
    private final CertificadoDonacionService certificadoService;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, EventoRepository eventoRepository, EntradaRepository entradaRepository, CertificadoDonacionService certificadoDonacionService){
        this.pedidoRepository = pedidoRepository;
        this.eventoRepository = eventoRepository;
        this.entradaRepository = entradaRepository;
        this.certificadoService = certificadoDonacionService;
    }

    @Transactional
    public Pedido finalizarPedido(Usuario usuario,
                                  Long eventoId,
                                  int cantidadEntradas,
                                  CanalPago canalPago,
                                  Boolean consentimiento,
                                  // Añadimos los campos fiscales aquí también:
                                  String direccion,
                                  String codigoPostal,
                                  String poblacion,
                                  String provincia,
                                  String pais,
                                  String nifFiscal) { // Opcional, por si quiere certificado a nombre de otro

        // 1. Obtener y verificar evento
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado."));

        if (evento.getStock() < cantidadEntradas) { // Asegúrate de tener el campo 'stock' en Evento
            throw new RuntimeException("Stock insuficiente.");
        }

        // 2. Descontar stock
        evento.setStock(evento.getStock() - cantidadEntradas);
        eventoRepository.save(evento);

        // 3. Crear Pedido
        Pedido nuevoPedido = new Pedido();
        // Nota: Deberías calcular el importe real (cantidad * precio) si el evento tiene precio
        nuevoPedido.setImporteTotal(0.0);
        nuevoPedido.setFecha(LocalDate.now());
        nuevoPedido.setHora(LocalTime.now());
        nuevoPedido.setUsuario(usuario);
        nuevoPedido.setEstadoPago(EstadoPago.PAGADO);
        nuevoPedido.setCanalPago(canalPago);
        nuevoPedido.setConsentimiento(consentimiento);

        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

        // 4. Generar entradas
        List<Entrada> entradasGeneradas = new ArrayList<>();
        for (int i = 0; i < cantidadEntradas; i++) {
            Entrada entrada = new Entrada();
            // Generamos QR único (String)
            entrada.setQr(UUID.randomUUID().toString());
            entrada.setUsada(false);
            entrada.setEvento(evento);
            entrada.setPedido(pedidoGuardado);
            // Necesitamos nombres para la entrada, usamos el del usuario por defecto
            entrada.setNombrePersona(usuario.getNombre());
            entrada.setTipoEntrada("General"); // O pásalo por parámetro

            entradasGeneradas.add(entrada);
        }
        entradaRepository.saveAll(entradasGeneradas);
        pedidoGuardado.setEntradas(entradasGeneradas);

        // 5. GENERAR CERTIFICADO (Si dio consentimiento)
        if (Boolean.TRUE.equals(consentimiento)) {
            // Validación básica para no guardar datos nulos en la tabla Certificado
            if (direccion == null || codigoPostal == null || poblacion == null) {
                throw new IllegalArgumentException("Faltan datos fiscales obligatorios para el certificado.");
            }

            // REVISAR ESTO
            certificadoService.generarCertificado(
                    usuario,
                    pedidoGuardado.getImporteTotal(),
                    pedidoGuardado,
                    null, // Donación es null
                    null, // Nombre fiscal (usa el del usuario)
                    nifFiscal, // NIF fiscal (puede ser null y usará el del usuario)
                    direccion,
                    codigoPostal,
                    poblacion,
                    provincia,
                    pais
            );
        }

        return pedidoGuardado;
    }
}
