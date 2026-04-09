package com.project.hotel.dto;

/**
 * Estados posibles de una habitación en el calendario de reservas.
 * Cada estado lleva una etiqueta legible y un código de color CSS
 * listo para ser mapeado a un componente de frontend tipo agenda/calendario.
 *
 * <ul>
 *   <li>{@link #DISPONIBLE} – La habitación está libre para reservar (verde).</li>
 *   <li>{@link #OCUPADA} – Existe una reserva activa o cumplida que cubre ese día (rojo).</li>
 *   <li>{@link #MANTENIMIENTO} – La habitación está en mantenimiento (amarillo).</li>
 *   <li>{@link #FUERA_DE_SERVICIO} – La habitación está inactiva / fuera de servicio (gris).</li>
 * </ul>
 */
public enum EstadoCalendario {

    /** Habitación libre en ese día. Color sugerido: verde. */
    DISPONIBLE("Disponible", "#28a745"),

    /** Habitación con reserva activa o cumplida ese día. Color sugerido: rojo. */
    OCUPADA("Ocupada", "#dc3545"),

    /** Habitación en mantenimiento. Color sugerido: amarillo. */
    MANTENIMIENTO("En mantenimiento", "#ffc107"),

    /** Habitación desactivada o fuera de servicio. Color sugerido: gris. */
    FUERA_DE_SERVICIO("Fuera de servicio", "#6c757d");

    private final String etiqueta;
    private final String codigoColor;

    EstadoCalendario(String etiqueta, String codigoColor) {
        this.etiqueta = etiqueta;
        this.codigoColor = codigoColor;
    }

    /** Etiqueta legible para mostrar en UI. */
    public String getEtiqueta() {
        return etiqueta;
    }

    /** Código de color hexadecimal CSS sugerido para este estado. */
    public String getCodigoColor() {
        return codigoColor;
    }
}
