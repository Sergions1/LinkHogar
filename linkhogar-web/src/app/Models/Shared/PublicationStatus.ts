enum PublicationStatus {
  DRAFT,             // El usuario lo está editando, nadie lo ve.
  PENDING_REVIEW,    // El admin lo está revisando.
  PUBLISHED,         // Es visible en el buscador.
  PAUSED,            // El usuario lo ha ocultado temporalmente.
  EXPIRED,           // Pasó el tiempo máximo, hay que renovar.
  ARCHIVED           // Se eliminó lógicamente (papelera).
}
