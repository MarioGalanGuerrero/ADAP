package com.byron.cudeca.repository;

import com.byron.cudeca.model.Entrada;
import com.byron.cudeca.model.Evento;
import com.byron.cudeca.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    // -------------------------------------------------------------------------
    // VALIDACIÓN DE ACCESO (SCANNER QR)
    // -------------------------------------------------------------------------

    /**
     * Buscar una entrada por su código QR único.
     * Este es el método que usarás cuando el voluntario escanee el código en la puerta.
     * Devuelve Optional porque puede que el QR sea falso o no exista.
     */
    Optional<Entrada> findByQrCodigo(String qrCodigo);

    // -------------------------------------------------------------------------
    // LISTADOS Y GESTIÓN
    // -------------------------------------------------------------------------

    /**
     * Listar todas las entradas de un Pedido concreto.
     * Útil si el usuario quiere ver "qué compré en este pedido".
     */
    List<Entrada> findByPedido(Pedido pedido);

    /**
     * Listar todas las entradas vendidas para un Evento.
     * Útil para que el Administrador vea la lista de asistentes ("Guest List").
     */
    List<Entrada> findByEvento(Evento evento);

    // -------------------------------------------------------------------------
    // ESTADÍSTICAS (Dashboards)
    // -------------------------------------------------------------------------

    /**
     * Cuenta cuántas entradas se han vendido para un evento.
     * Más eficiente que traerse la lista entera si solo quieres saber el número.
     */
    long countByEvento(Evento evento);

    /**
     * Cuenta cuántas personas han entrado ya al evento (validadas = true).
     * Útil para saber el aforo en tiempo real.
     */
    long countByEventoAndUsadaTrue(Evento evento);
}