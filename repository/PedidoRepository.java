package com.byron.cudeca.repository;

import com.byron.cudeca.model.EstadoPago;
import com.byron.cudeca.model.Pedido;
import com.byron.cudeca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // -------------------------------------------------------------------------
    // MÉTODOS PERSONALIZADOS ÚTILES
    // -------------------------------------------------------------------------

    /**
     * 1. HISTORIAL DE PEDIDOS DEL USUARIO
     * Fundamental para la sección "Mis Pedidos" de tu web.
     * Busca por el objeto Usuario y los ordena para que salgan los nuevos primero.
     */
    List<Pedido> findByUsuarioOrderByFechaDescHoraDesc(Usuario usuario);

    /**
     * 2. BUSCAR POR ESTADO
     * Útil para el Admin: "Dame todos los pedidos que estén PENDIENTES o REEMBOLSO".
     */
    List<Pedido> findByEstadoPago(EstadoPago estado);

    /**
     * 3. BUSCAR POR USUARIO Y ESTADO
     * Ej: Para ver si un usuario tiene pagos pendientes.
     */
    List<Pedido> findByUsuarioAndEstadoPago(Usuario usuario, EstadoPago estado);
}
