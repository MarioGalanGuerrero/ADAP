package com.byron.cudeca.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "administrador")
@Data
@EqualsAndHashCode(callSuper = true)
public class Administrador extends Usuario {

    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String contrasena;

    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL)
    private List<Evento> eventosCreados;

    public List<Evento> getEventosCreados() {
        return eventosCreados;
    }

    public void setEventosCreados(List<Evento> eventosCreados) {
        this.eventosCreados = eventosCreados;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
