package com.byron.cudeca.service;

import com.byron.cudeca.model.Administrador;
import com.byron.cudeca.repository.AdministradorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Necesitas Spring Security
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder; // Para encriptar contraseñas

    @Autowired
    public AdministradorService(AdministradorRepository administradorRepository,
                                PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo administrador en el sistema.
     * Encripta la contraseña y valida duplicados.
     */
    @Transactional
    public Administrador registrarAdministrador(Administrador admin) {
        // 1. Validaciones de Integridad
        if (administradorRepository.existsById(admin.getNif())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese NIF.");
        }
        if (administradorRepository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        if (administradorRepository.existsByIdAdministrador(admin.getId())) {
            throw new IllegalArgumentException("El ID de Administrador ya está en uso.");
        }

        // 2. Encriptar la contraseña (VITAL)
        String passEncriptada = passwordEncoder.encode(admin.getContrasena());
        admin.setContrasena(passEncriptada);

        // 3. Guardar
        // Al ser herencia JOINED, Spring guarda en 'usuarios' y en 'administradores' automáticamente.
        return administradorRepository.save(admin);
    }

    /**
     * Busca un administrador por su NIF (Clave primaria).
     */
    public Administrador buscarPorNif(String nif) {
        // Nota: Si tu NIF en Model es String, cambia el parámetro a String aquí.
        return administradorRepository.findById(nif)
                .orElseThrow(() -> new EntityNotFoundException("Administrador no encontrado con NIF: " + nif));
    }

    /**
     * Busca un administrador por su Email.
     * Usado internamente para el Login.
     */
    public Optional<Administrador> buscarPorEmail(String email) {
        return administradorRepository.findByEmail(email);
    }

    /**
     * Lista todos los administradores del sistema.
     */
    public List<Administrador> listarTodos() {
        return administradorRepository.findAll();
    }

    /**
     * Actualizar datos de un administrador.
     * OJO: No permitimos cambiar la contraseña aquí (se suele hacer en otro método aparte).
     */
    @Transactional
    public Administrador actualizarPerfil(String nif, Administrador datosNuevos) {
        Administrador adminActual = buscarPorNif(nif);

        // Actualizamos datos básicos (Heredados de Usuario)
        adminActual.setNombre(datosNuevos.getNombre());
        adminActual.setApellidos(datosNuevos.getApellidos());
        adminActual.setNumeroTelefono(datosNuevos.getNumeroTelefono());

        // Actualizamos datos específicos de Admin
        // Ojo: validamos que el nuevo ID Admin no esté pillado por otro
        if (!adminActual.getId().equals(datosNuevos.getId()) &&
                administradorRepository.existsByIdAdministrador(datosNuevos.getId())) {
            throw new IllegalArgumentException("El nuevo ID de Administrador ya existe.");
        }
        adminActual.setId(datosNuevos.getId());

        return administradorRepository.save(adminActual);
    }

    /**
     * Eliminar un administrador.
     */
    @Transactional
    public void eliminarAdministrador(String nif) {
        if (!administradorRepository.existsById(nif)) {
            throw new EntityNotFoundException("No se puede eliminar, el administrador no existe.");
        }
        administradorRepository.deleteById(nif);
    }
}