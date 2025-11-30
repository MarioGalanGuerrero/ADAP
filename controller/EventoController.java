package com.byron.cudeca.controller;

import com.byron.cudeca.model.Evento;
import com.byron.cudeca.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller for handling public event listings, searches, and administrative CRUD operations.
 */

@RestController
@RequestMapping("/api")
public class EventoController {

    private final EventoService eventoService;

    @Autowired
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // -----------------------------------------------------------------
    // PUBLIC ENDPOINTS (Accessed by everyone - GET)
    // -----------------------------------------------------------------

    /**
     * Endpoint: GET /api/eventos
     * Retrieves a filtered and sorted list of future events for the main page.
     * @param keyword Optional search keyword (uses EventoRepository.findByNombreContainingIgnoreCase)
     * @param sortBy Optional field to sort by (e.g., "fecha", "nombre", "ubicacion", "stock")
     * @param sortDir Optional sort direction ("asc" or "desc")
     * @return List of Evento entities (or DTOs for production)
     */
    @GetMapping("/eventos")
    public ResponseEntity<List<Evento>> getAllEvents(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String sortDir
    ) {
        // El filtro se hace en service
        List<Evento> eventos = eventoService.obtenerEventosConFiltros(keyword, sortBy, sortDir);
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    /**
     * Endpoint: GET /api/eventos/{id}
     * Retrieves details for a single event.
     * @param id The ID of the event.
     */
    @GetMapping("/eventos/{id}")
    public ResponseEntity<Evento> getEventById(@PathVariable Long id) {
        Evento evento = eventoService.obtenerEventoPorId(id); 
        return new ResponseEntity<>(evento, HttpStatus.OK);
    }
    
    // -----------------------------------------------------------------
    //                         ADMIN ENDPOINTS 
    // -----------------------------------------------------------------
    
    /**
     * Endpoint: POST /api/admin/eventos
     * Creates a new event (Admin only).
     * @param evento The Evento entity (or DTO) to be created.
     */
    @PostMapping("/admin/eventos")
    //Este acceso debe de ser restringido solo para admins (mirar donde se implementa)
    public ResponseEntity<Evento> createNewEvent(@RequestBody Evento evento) {
        Evento nuevoEvento = eventoService.crearEvento(evento);
        return new ResponseEntity<>(nuevoEvento, HttpStatus.CREATED);
    }
    
    /**
     * Endpoint: DELETE /api/admin/eventos/{id}
     * Deletes an existing event (Admin only).
     * @param id The ID of the event to delete.
     */
    @DeleteMapping("/admin/eventos/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventoService.borrarEvento(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
    }
}