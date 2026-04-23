package com.linkhogar.application.house.deleteReport;

import com.linkhogar.application.notifications.createNotification.CreateNotificationCommand;
import com.linkhogar.application.notifications.createNotification.CreateNotificationCommandHandler;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.*;
import com.linkhogar.domain.house.enums.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DeleteReportCommandHandler {
    private final HouseReportRepository houseReportRepository;
    private final HouseRepository houseRepository;
    private final CreateNotificationCommandHandler createNotificationCommandHandler;

    @Transactional
    public Result<Void> handle(DeleteReportCommand command){
        try {
            // 1. Buscamos la denuncia
           HouseReport report = houseReportRepository.findById(command.reportId());
            if (report == null) {
                System.out.println("Denuncia con id " + command.reportId() + " no encontrada");
                return Result.failure(Error.notFound("501", "Denuncia no encontrada"));
            }

            // 2. Si el admin ha decidido que la denuncia es real y hay que eliminar la casa
            if (command.archiveHouse()) {
                House house = houseRepository.getById(report.getHouseId());

                // Ocultamos la casa
                house.setPublicationStatus(PublicationStatus.ARCHIVED);
                houseRepository.save(house);

                // Disparamos la notificación al dueño
                CreateNotificationCommand notifyCommand = new CreateNotificationCommand(
                        house.getOwner().getId(),
                        "Anuncio Retirado por Moderación",
                        "Tu anuncio '" + house.getTitle() + "' ha sido retirado tras una revisión."
                );
                createNotificationCommandHandler.handle(notifyCommand);
            }

            report.setStatus(ReportStatus.REVISADA);
            houseReportRepository.save(report);

            return Result.success(null);

        } catch (Exception e) {
            System.out.println("Error al eliminar denuncia: " + e.getMessage());
            return Result.failure(Error.failure("501", "No se pudo eliminar la denuncia"));
        }
    }
}
