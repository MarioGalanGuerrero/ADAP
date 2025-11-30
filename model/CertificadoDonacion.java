package com.byron.cudeca.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificadoDonacion")
@Data
public class CertificadoDonacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private Double importeCertificado;

    @Column(nullable = false, unique = true)
    private String nif;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String codigoPostal;

    @Column(nullable = false)
    private String poblacion;

    @Column(nullable = false)
    private String provincia;

    @Column(nullable = false)
    private String pais;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedidoId", nullable = true)
    private Pedido pedido;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donacionId", nullable = true)
    private Donacion donacion;

    public Donacion getDonacion() {
        return donacion;
    }

    public void setDonacion(Donacion donacion) {
        this.donacion = donacion;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Double getImporteCertificado() {
        return importeCertificado;
    }

    public void setImporteCertificado(Double importeCertificado) {
        this.importeCertificado = importeCertificado;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
}
