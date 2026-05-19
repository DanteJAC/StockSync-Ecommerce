package com.stockSync.backend.stock.service;

import com.stockSync.backend.user.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseService {

    /**
     * Retorna el ID del entorno (Tenant).
     * Si el usuario es ADMIN, retorna su propio ID.
     * Si es LOCAL o BODEGA, retorna el ID de su jefe/dueño.
     */
    protected Long getTenantId() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getOwner() != null ? currentUser.getOwner().getId() : currentUser.getId();
    }

    /**
     * Retorna la entidad User que representa al dueño del entorno.
     * Se usa EXCLUSIVAMENTE para asignar el propietario al crear nuevos registros (setUser).
     */
    protected User getTenantUser() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getOwner() != null ? currentUser.getOwner() : currentUser;
    }
}