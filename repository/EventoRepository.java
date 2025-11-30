package com.byron.cudeca.repository;

import com.byron.cudeca.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Evento. 
 * Extiende JpaRepository para obtener los métodos CRUD básicos.
 * Tarea de P2 (Eventos).
 */
@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // ----------------------------------------------------------------------
    // MÉTODOS DE BÚSQUEDA PÚBLICA (Para el listado de la Home)
    // ----------------------------------------------------------------------
    
    /**
     * Obtiene todos los eventos cuya fecha sea posterior a la actual, 
     * ordenados por fecha de inicio ascendente.
     * @param fechaActual La fecha actual para filtrar.
     * @return Lista de eventos futuros ordenados.
     */
    List<Evento> findByFechaAfterOrderByFechaAsc(LocalDate fechaActual);

    /**
     * Búsqueda por palabra clave en el nombre (ignorando mayúsculas/minúsculas).
     * @param nombreFragmento Palabra clave a buscar.
     * @return Lista de eventos que contienen esa palabra clave.
     */
    List<Evento> findByNombreContainingIgnoreCase(String nombreFragmento);
}