package com.byron.cudeca.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "donacion")
@Data
public class Donacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double importe;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalPago canalPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioNif", nullable = false)
    private Usuario usuario;

    @OneToOne(mappedBy = "donacion", cascade = CascadeType.ALL)
    private CertificadoDonacion certificadoDonacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public CanalPago getCanalPago() {
        return canalPago;
    }

    public void setCanalPago(CanalPago canalPago) {
        this.canalPago = canalPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public CertificadoDonacion getCertificadoDonacion() {
        return certificadoDonacion;
    }

    public void setCertificadoDonacion(CertificadoDonacion certificadoDonacion) {
        this.certificadoDonacion = certificadoDonacion;
    }
}
