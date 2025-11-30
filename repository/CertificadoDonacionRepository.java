package com.byron.cudeca.repository;

import com.byron.cudeca.model.CertificadoDonacion;
import com.byron.cudeca.model.Donacion;
import com.byron.cudeca.model.Pedido;
import com.byron.cudeca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificadoDonacionRepository extends JpaRepository<CertificadoDonacion, Long> {

    // -------------------------------------------------------------------------
    // BÚSQUEDAS DIRECTAS (Para evitar duplicados)
    // -------------------------------------------------------------------------

    /**
     * Busca si ya existe un certificado para un Pedido concreto.
     * Útil antes de generar uno nuevo para no duplicarlo.
     */
    Optional<CertificadoDonacion> findByPedido(Pedido pedido);

    /**
     * Busca si ya existe un certificado para una Donación concreta.
     */
    Optional<CertificadoDonacion> findByDonacion(Donacion donacion);

    // -------------------------------------------------------------------------
    // BÚSQUEDAS AVANZADAS (Reportes y Usuario)
    // -------------------------------------------------------------------------

    /**
     * Busca todos los certificados de un Usuario.
     * * EXPLICACIÓN DE LA QUERY:
     * Como el certificado no tiene el campo 'usuario' directo, tenemos que hacer un JOIN
     * para mirar si el usuario es el dueño del Pedido O el dueño de la Donación.
     */
    @Query("SELECT c FROM CertificadoDonacion c " +
            "LEFT JOIN c.pedido p " +
            "LEFT JOIN c.donacion d " +
            "WHERE p.usuario = :usuario OR d.usuario = :usuario " +
            "ORDER BY c.fechaEmision DESC")
    List<CertificadoDonacion> findByUsuario(@Param("usuario") Usuario usuario);

    /**
     * Busca certificados emitidos en un rango de fechas.
     * Fundamental para el Modelo 182 (Declaración a Hacienda) que se saca por año fiscal.
     */
    List<CertificadoDonacion> findByFechaEmisionBetween(LocalDate inicio, LocalDate fin);
}