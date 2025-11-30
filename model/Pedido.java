package com.byron.cudeca.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "pedido")
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false)
    private Double importeTotal;

    @Column(nullable = false)
    private Boolean consentimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalPago canalPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioNif", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<Entrada> entradas;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private CertificadoDonacion certificadoDonacion;

    public CertificadoDonacion getCertificadoDonacion() {
        return certificadoDonacion;
    }

    public void setCertificadoDonacion(CertificadoDonacion certificadoDonacion) {
        this.certificadoDonacion = certificadoDonacion;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<Entrada> entradas) {
        this.entradas = entradas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public CanalPago getCanalPago() {
        return canalPago;
    }

    public void setCanalPago(CanalPago canalPago) {
        this.canalPago = canalPago;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Boolean getConsentimiento() {
        return consentimiento;
    }

    public void setConsentimiento(Boolean consentimiento) {
        this.consentimiento = consentimiento;
    }

    public Double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(Double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
