package com.byron.cudeca.controller;

import com.byron.cudeca.model.Evento;
import com.byron.cudeca.service.EventoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventoControllerTest {

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private EventoController eventoController;

    // Verifica que el endpoint público devuelve la lista y estado 200
    @Test
    void getAllEvents_devuelveOkConDatos() {
        when(eventoService.obtenerEventosConFiltros(null, null, null)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Evento>> response = eventoController.getAllEvents(null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(eventoService).obtenerEventosConFiltros(null, null, null);
    }

    // Verifica que el detalle de evento devuelve 200 y el cuerpo esperado
    @Test
    void getEventById_devuelveEvento() {
        Evento evento = new Evento();
        evento.setId(5L);
        when(eventoService.obtenerEventoPorId(5L)).thenReturn(evento);

        ResponseEntity<Evento> response = eventoController.getEventById(5L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(evento);
        verify(eventoService).obtenerEventoPorId(5L);
    }

    // Comprueba que crear un evento retorna 201 y el evento creado
    @Test
    void createNewEvent_retornaCreated() {
        Evento evento = new Evento();
        when(eventoService.crearEvento(any(Evento.class))).thenReturn(evento);

        ResponseEntity<Evento> response = eventoController.createNewEvent(evento);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(evento);
        verify(eventoService).crearEvento(evento);
    }

    // Comprueba que el borrado devuelve 204 y delega en el servicio
    @Test
    void deleteEvent_retornaNoContent() {
        ResponseEntity<Void> response = eventoController.deleteEvent(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(eventoService).borrarEvento(10L);
    }
}
