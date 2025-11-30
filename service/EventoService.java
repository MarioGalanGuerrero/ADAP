package com.byron.cudeca.service;

import com.byron.cudeca.model.*;
import com.byron.cudeca.repository.EventoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {
    private final EventoRepository eventoRepository;
    private final Clock clock;

    @Autowired
    public EventoService(EventoRepository eventoRepository) {
        this(eventoRepository, Clock.systemDefaultZone());
    }

    public EventoService(EventoRepository eventoRepository, Clock clock) {
        this.eventoRepository = eventoRepository;
        this.clock = clock;
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
        return eventoRepository.findByFechaAfterOrderByFechaAsc(LocalDate.now(clock));
    }

    /**
     * Busca eventos por nombre.
     * Útil para la barra de búsqueda del frontend.
     */
    public List<Evento> buscarEventos(String palabraClave) {
        if (palabraClave == null || palabraClave.isBlank()) {
            return obtenerEventosFuturos(); // Si no escriben nada, devolvemos la lista normal
        }
        LocalDate hoy = LocalDate.now(clock);
        return eventoRepository.findByNombreContainingIgnoreCase(palabraClave).stream()
                .filter(evento -> evento.getFecha() == null || !evento.getFecha().toLocalDate().isBefore(hoy))
                .sorted(Comparator.comparing(Evento::getFecha, Comparator.nullsLast(LocalDateTime::compareTo)))
                .collect(Collectors.toList());
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
     * Obtiene eventos aplicando filtros opcionales de palabra clave y ordenación.
     * Este método se usa para la home pública, por lo que siempre descarta eventos pasados.
     */
    public List<Evento> obtenerEventosConFiltros(String palabraClave, String sortBy, String sortDir) {
        LocalDate hoy = LocalDate.now(clock);
        List<Evento> eventos = (palabraClave == null || palabraClave.isBlank())
                ? eventoRepository.findByFechaAfterOrderByFechaAsc(hoy)
                : eventoRepository.findByNombreContainingIgnoreCase(palabraClave).stream()
                    .filter(evento -> evento.getFecha() == null || !evento.getFecha().toLocalDate().isBefore(hoy))
                    .collect(Collectors.toList());

        Comparator<Evento> comparator = construirComparador(sortBy, sortDir);
        return eventos.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Guarda un nuevo evento en la base de datos.
     */
    @Transactional
    public Evento crearEvento(Evento evento) {
        validarEvento(evento);
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

    @Transactional
    public void borrarEvento(Long id) {
        eliminarEvento(id);
    }

    private void validarEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser nulo.");
        }
        if (evento.getNombre() == null || evento.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del evento es obligatorio.");
        }
        if (evento.getTipoEvento() == null || evento.getTipoEvento().isBlank()) {
            throw new IllegalArgumentException("El tipo del evento es obligatorio.");
        }
        if (evento.getFecha() == null) {
            throw new IllegalArgumentException("La fecha del evento es obligatoria.");
        }
        if (evento.getFecha().toLocalDate().isBefore(LocalDate.now(clock))) {
            throw new IllegalArgumentException("La fecha del evento no puede estar en el pasado.");
        }
        if (evento.getUbicacion() == null || evento.getUbicacion().isBlank()) {
            throw new IllegalArgumentException("La ubicación del evento es obligatoria.");
        }
        if (evento.getStock() == null || evento.getStock() < 0) {
            throw new IllegalArgumentException("El stock debe ser un número igual o mayor a cero.");
        }
        if (evento.getAdministrador() == null) {
            throw new IllegalArgumentException("El administrador del evento es obligatorio.");
        }
    }

    private Comparator<Evento> construirComparador(String sortBy, String sortDir) {
        String criterio = (sortBy == null || sortBy.isBlank()) ? "fecha" : sortBy.toLowerCase();
        Comparator<Evento> comparator;

        switch (criterio) {
            case "nombre":
                comparator = Comparator.comparing(Evento::getNombre, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "tipoevento":
                comparator = Comparator.comparing(Evento::getTipoEvento, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "ubicacion":
                comparator = Comparator.comparing(Evento::getUbicacion, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "stock":
                comparator = Comparator.comparing(Evento::getStock, Comparator.nullsLast(Integer::compareTo));
                break;
            default:
                comparator = Comparator.comparing(Evento::getFecha, Comparator.nullsLast(LocalDateTime::compareTo));
                break;
        }

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        return comparator.thenComparing(Evento::getId, Comparator.nullsLast(Long::compareTo));
    }
}
