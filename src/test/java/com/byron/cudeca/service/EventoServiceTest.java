package com.byron.cudeca.service;

import com.byron.cudeca.model.Administrador;
import com.byron.cudeca.model.Evento;
import com.byron.cudeca.repository.EventoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2024-05-01T10:00:00Z"), ZoneId.of("UTC"));

    @Mock
    private EventoRepository eventoRepository;

    private EventoService eventoService;

    @BeforeEach
    void setUp() {
        eventoService = new EventoService(eventoRepository, FIXED_CLOCK);
    }

    // Verifica que sin palabra clave se recuperan eventos futuros y se ordenan por fecha ascendente
    @Test
    void obtenerEventosConFiltros_sinKeywordOrdenaPorFechaAsc() {
        Evento mayo = crearEvento(1L, "Charla", LocalDateTime.of(2024, 5, 10, 18, 0));
        Evento junio = crearEvento(2L, "Concierto", LocalDateTime.of(2024, 6, 5, 20, 0));
        Evento abril = crearEvento(3L, "Taller", LocalDateTime.of(2024, 4, 30, 9, 0));
        when(eventoRepository.findByFechaAfterOrderByFechaAsc(LocalDate.of(2024, 5, 1)))
                .thenReturn(Arrays.asList(mayo, junio, abril));

        List<Evento> resultado = eventoService.obtenerEventosConFiltros(null, null, null);

        assertThat(resultado).containsExactly(abril, mayo, junio);
        verify(eventoRepository).findByFechaAfterOrderByFechaAsc(LocalDate.of(2024, 5, 1));
        verify(eventoRepository, never()).findByNombreContainingIgnoreCase(any());
    }

    // Verifica que con palabra clave se filtran los eventos pasados y se mantiene el futuro
    @Test
    void obtenerEventosConFiltros_filtraEventosPasadosAlBuscar() {
        Evento pasado = crearEvento(1L, "Noche solidaria", LocalDateTime.of(2024, 4, 1, 19, 0));
        Evento futuro = crearEvento(2L, "Noche solidaria", LocalDateTime.of(2024, 5, 20, 19, 0));
        when(eventoRepository.findByNombreContainingIgnoreCase("Noche"))
                .thenReturn(Arrays.asList(pasado, futuro));

        List<Evento> resultado = eventoService.obtenerEventosConFiltros("Noche", null, null);

        assertThat(resultado).containsExactly(futuro);
        verify(eventoRepository, never()).findByFechaAfterOrderByFechaAsc(any());
    }

    // Verifica que el ordenamiento respeta sortBy y sortDir en modo descendente
    @Test
    void obtenerEventosConFiltros_ordenaPorNombreDesc() {
        Evento eventoA = crearEvento(1L, "Alpha", LocalDateTime.of(2024, 5, 5, 18, 0));
        Evento eventoB = crearEvento(2L, "Beta", LocalDateTime.of(2024, 5, 6, 18, 0));
        when(eventoRepository.findByFechaAfterOrderByFechaAsc(LocalDate.of(2024, 5, 1)))
                .thenReturn(Arrays.asList(eventoA, eventoB));

        List<Evento> resultado = eventoService.obtenerEventosConFiltros(null, "nombre", "desc");

        assertThat(resultado).containsExactly(eventoB, eventoA);
    }

    // Comprueba que sortBy desconocido cae en ordenación por fecha ascendente
    @Test
    void obtenerEventosConFiltros_criterioDesconocidoVuelvePorFecha() {
        Evento eventoA = crearEvento(1L, "Alpha", LocalDateTime.of(2024, 5, 6, 18, 0));
        Evento eventoB = crearEvento(2L, "Beta", LocalDateTime.of(2024, 5, 5, 18, 0));
        when(eventoRepository.findByFechaAfterOrderByFechaAsc(LocalDate.of(2024, 5, 1)))
                .thenReturn(Arrays.asList(eventoA, eventoB));

        List<Evento> resultado = eventoService.obtenerEventosConFiltros(null, "desconocido", null);

        assertThat(resultado).containsExactly(eventoB, eventoA);
    }

    // Valida que la creación de eventos rechaza fechas en el pasado
    @Test
    void crearEvento_conFechaPasadaLanzaExcepcion() {
        Evento evento = crearEvento(null, "Alpha", LocalDateTime.of(2024, 4, 10, 10, 0));

        assertThatThrownBy(() -> eventoService.crearEvento(evento))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pasado");
        verify(eventoRepository, never()).save(any());
    }

    // Valida que la creación de eventos exige datos obligatorios
    @Test
    void crearEvento_sinNombreOLocalizacionLanzaExcepcion() {
        Evento evento = crearEvento(null, "", LocalDateTime.of(2024, 5, 10, 10, 0));
        evento.setUbicacion("   ");

        assertThatThrownBy(() -> eventoService.crearEvento(evento))
                .isInstanceOf(IllegalArgumentException.class);
        verify(eventoRepository, never()).save(any());
    }

    // Confirma que la creación válida guarda el evento
    @Test
    void crearEvento_validoSeGuarda() {
        Evento evento = crearEvento(null, "Alpha", LocalDateTime.of(2024, 5, 10, 10, 0));
        when(eventoRepository.save(evento)).thenReturn(evento);

        Evento guardado = eventoService.crearEvento(evento);

        assertThat(guardado).isSameAs(evento);
        verify(eventoRepository).save(evento);
    }

    // Comprueba que eliminarEvento valida existencia antes de borrar
    @Test
    void eliminarEvento_inexistenteLanzaExcepcion() {
        when(eventoRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> eventoService.eliminarEvento(1L))
                .isInstanceOf(EntityNotFoundException.class);
        verify(eventoRepository, never()).deleteById(any());
    }

    // Comprueba que eliminarEvento delega en el repositorio cuando existe
    @Test
    void eliminarEvento_existenteBorra() {
        when(eventoRepository.existsById(1L)).thenReturn(true);

        eventoService.eliminarEvento(1L);

        verify(eventoRepository).deleteById(1L);
    }

    // Comprueba que obtenerEventoPorId propaga la EntityNotFoundException descriptiva
    @Test
    void obtenerEventoPorId_inexistenteLanzaExcepcion() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventoService.obtenerEventoPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    private Evento crearEvento(Long id, String nombre, LocalDateTime fecha) {
        Evento evento = new Evento();
        evento.setId(id);
        evento.setNombre(nombre);
        evento.setTipoEvento("Formativo");
        evento.setDescripcion("Descripcion");
        evento.setFecha(fecha);
        evento.setUbicacion("Malaga");
        evento.setStock(10);
        evento.setAdministrador(new Administrador());
        return evento;
    }
}
