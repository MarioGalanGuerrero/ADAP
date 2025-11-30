package com.byron.cudeca.service;

import com.byron.cudeca.model.CertificadoDonacion;
import com.byron.cudeca.model.Donacion;
import com.byron.cudeca.model.Pedido;
import com.byron.cudeca.model.Usuario;
import com.byron.cudeca.repository.CertificadoDonacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificadoDonacionService {

    private final CertificadoDonacionRepository certificadoDonacionRepository;

    @Autowired
    public CertificadoDonacionService(CertificadoDonacionRepository certificadoRepository) {
        this.certificadoDonacionRepository = certificadoRepository;
    }

    // ----------------------------------------------------------------------
    // GENERACIÓN DE CERTIFICADOS
    // ----------------------------------------------------------------------

    /**
     * Genera un certificado recibiendo TODOS los datos obligatorios como parámetros sueltos.
     */
    @Transactional
    public CertificadoDonacion generarCertificado(Usuario usuario,
                                                  Double importe,
                                                  Pedido pedido,
                                                  Donacion donacion,
                                                  // Parámetros fiscales individuales:
                                                  String nombreCompletoFiscal,
                                                  String nifFiscal,
                                                  String direccion,
                                                  String codigoPostal,
                                                  String poblacion,
                                                  String provincia,
                                                  String pais) {

        CertificadoDonacion cert = new CertificadoDonacion();

        // 1. Datos Automáticos
        cert.setFechaEmision(LocalDateTime.now());
        cert.setImporteCertificado(importe);
        cert.setPedido(pedido);
        cert.setDonacion(donacion);

        // 2. Datos Personales (Prioridad: Parámetro -> Usuario)
        if (nifFiscal != null && !nifFiscal.isBlank()) {
            cert.setNif(nifFiscal);
        } else {
            cert.setNif(String.valueOf(usuario.getNif()));
        }

        if (nombreCompletoFiscal != null && !nombreCompletoFiscal.isBlank()) {
            cert.setNombreCompleto(nombreCompletoFiscal);
        } else {
            cert.setNombreCompleto(usuario.getNombre() + " " + usuario.getApellidos());
        }

        // 3. Dirección Fiscal (Obligatoria por tu entidad)
        // Aquí confiamos en que los parámetros vienen rellenos desde el Controller/PedidoService
        cert.setDireccion(direccion);
        cert.setCodigoPostal(codigoPostal);
        cert.setPoblacion(poblacion);
        cert.setProvincia(provincia);
        cert.setPais(pais);

        return certificadoDonacionRepository.save(cert);
    }

    /**
     * Genera un certificado para un Pedido (entradas, productos solidarios).
     * Se llama automáticamente al finalizar un pedido si el usuario marcó "Consentimiento".
     */
    @Transactional
    public CertificadoDonacion generarCertificadoParaPedido(Pedido pedido) {
        // Validación: Solo generamos certificado si el pedido está PAGADO
        if (!"PAGADO".equals(pedido.getEstadoPago().name())) {
            throw new IllegalStateException("No se puede generar certificado de un pedido no pagado.");
        }

        return certificadoDonacionRepository.findByPedido(pedido)
                .orElseGet(() -> {
                    CertificadoDonacion cert = new CertificadoDonacion();
                    cert.setPedido(pedido);
                    cert.setImporteCertificado(pedido.getImporteTotal());
                    return certificadoDonacionRepository.save(cert);
                });
    }

    // ----------------------------------------------------------------------
    // CONSULTAS Y REPORTES
    // ----------------------------------------------------------------------

    /**
     * Obtiene todos los certificados de un usuario para su área privada ("Mis Certificados").
     */
    public List<CertificadoDonacion> obtenerCertificadosDeUsuario(Usuario usuario) {
        return certificadoDonacionRepository.findByUsuario(usuario);
    }

    /**
     * Obtiene un certificado por ID.
     */
    public CertificadoDonacion obtenerPorId(Long id) {
        return certificadoDonacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Certificado no encontrado"));
    }

    /**
     * Obtiene todos los certificados de un año fiscal concreto.
     * Vital para generar el MODELO 182 de Hacienda.
     */
    public List<CertificadoDonacion> obtenerReporteFiscal(int anio) {
        LocalDate inicio = LocalDate.of(anio, 1, 1);
        LocalDate fin = LocalDate.of(anio, 12, 31);
        return certificadoDonacionRepository.findByFechaEmisionBetween(inicio, fin);
    }
}