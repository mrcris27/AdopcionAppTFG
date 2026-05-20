package com.example.adopciontfg.model;

/**
 * Estado de publicación del animal en la protectora.
 */
public enum AnimalStatus {
    /** Visible para adopción */
    AVAILABLE,
    /** Reservado / en proceso de adopción */
    RESERVED,
    /** Ya adoptado — no se muestra en listados públicos */
    ADOPTED,
    /** Temporalmente no disponible (vet, cuarentena, etc.) */
    UNAVAILABLE
}