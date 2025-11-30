package com.byron.cudeca.repository;

import com.byron.cudeca.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, String> {

    // -------------------------------------------------------------------------
    // BÚSQUEDAS ESPECÍFICAS DE ADMINISTRADOR
    // -------------------------------------------------------------------------

    /**
     * Buscar un administrador por su ID interno de gestión (ej: "ADM-001").
     * Útil para validar logins si usan este ID en lugar del email.
     */
    Optional<Administrador> findByIdAdministrador(String idAdministrador);

    /**
     * Verificar si existe un ID de administrador concreto.
     * Útil para validaciones al crear uno nuevo (evitar duplicados).
     */
    boolean existsByIdAdministrador(Long idAdministrador);

    // -------------------------------------------------------------------------
    // BÚSQUEDAS HEREDADAS (Campos que están en Usuario)
    // -------------------------------------------------------------------------

    /**
     * Buscar administrador por Email.
     * Aunque 'email' está en la clase Usuario, Spring sabe hacer el JOIN y buscarlo aquí.
     * Vital para el LOGIN (Spring Security carga el usuario por email).
     */
    Optional<Administrador> findByEmail(String email);

    /**
     * Comprobar si existe un administrador con ese email.
     */
    boolean existsByEmail(String email);
}