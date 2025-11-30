package com.byron.cudeca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "socio")
@Data
@EqualsAndHashCode(callSuper = true)
public class Socio extends Usuario {

    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private LocalDateTime fechaAlta;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private Double donacionTotal;

    @Column(nullable = false)
    private String contrasena;

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Double getDonacionTotal() {
        return donacionTotal;
    }

    public void setDonacionTotal(Double donacionTotal) {
        this.donacionTotal = donacionTotal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDateTime fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
