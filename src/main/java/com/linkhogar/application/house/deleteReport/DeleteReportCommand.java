package com.linkhogar.application.house.deleteReport;

import java.util.UUID;

public record DeleteReportCommand (UUID reportId,
                                   boolean archiveHouse) // true = eliminar anuncio y notificar, false = ignorar denuncia
                                   {}
