package com.byron.cudeca.service;

import com.byron.cudeca.model.*;
import com.byron.cudeca.repository.EventoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {
    private final EventoRepository eventoRepository;

    @Autowired
    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    // ----------------------------------------------------------------------
    // MÉTODOS DE LECTURA (PÚBLICOS)
    // ----------------------------------------------------------------------

    /**
     * Obtiene la cartelera de eventos próximos.
     * Usa el método personalizado del repositorio para filtrar pasados.
     */
    public List<Evento> obtenerEventosFuturos() {
        // Pasamos la fecha de "hoy" para que busque a partir de ahora
        return eventoRepository.findByFechaAfterOrderByFechaAsc(LocalDate.now());
    }

    /**
     * Busca eventos por nombre.
     * Útil para la barra de búsqueda del frontend.
     */
    public List<Evento> buscarEventos(String palabraClave) {
        if (palabraClave == null || palabraClave.isBlank()) {
            return obtenerEventosFuturos(); // Si no escriben nada, devolvemos la lista normal
        }
        return eventoRepository.findByNombreContainingIgnoreCase(palabraClave);
    }

    /**
     * Obtiene TODOS los eventos (incluidos los pasados).
     * Útil para el panel de administración (histórico).
     */
    public List<Evento> obtenerTodosLosEventos() {
        return eventoRepository.findAll();
    }

    /**
     * Obtiene un evento por su ID.
     * Necesario para la página de "Detalles del Evento".
     */
    public Evento obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con ID: " + id));
    }

    // ----------------------------------------------------------------------
    // MÉTODOS DE ESCRITURA (ADMINISTRACIÓN)
    // ----------------------------------------------------------------------

    /**
     * Guarda un nuevo evento en la base de datos.
     */
    @Transactional
    public Evento crearEvento(Evento evento) {
        // Aquí podrías añadir validaciones extra antes de guardar
       return eventoRepository.save(evento);
    }

    /**
     * Elimina un evento por ID.
     */
    @Transactional
    public void eliminarEvento(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new EntityNotFoundException("Evento no encontrado.");
        }
        eventoRepository.deleteById(id);
    }
}
