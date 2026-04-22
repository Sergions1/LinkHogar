package com.linkhogar.infrastructure.rest.house;

import com.linkhogar.application.house.Image.addImage.AddHouseImagesCommand;
import com.linkhogar.application.house.Image.addImage.AddHouseImagesCommandHandler;
import com.linkhogar.application.house.Image.deleteImage.DeleteHouseImageCommand;
import com.linkhogar.application.house.Image.deleteImage.DeleteHouseImageCommandHandler;
import com.linkhogar.application.house.SetHouseStatus.SetHouseStatusCommand;
import com.linkhogar.application.house.SetHouseStatus.SetHouseStatusCommandHandler;
import com.linkhogar.application.house.create.CreateHouseCommand;
import com.linkhogar.application.house.create.CreateHouseCommandHandler;
import com.linkhogar.application.house.createReport.CreateReportCommand;
import com.linkhogar.application.house.createReport.CreateReportCommandHandler;
import com.linkhogar.application.house.createReport.ReportHouseRequest;
import com.linkhogar.application.house.delete.DeleteHouseCommand;
import com.linkhogar.application.house.delete.DeleteHouseCommandHandler;
import com.linkhogar.application.house.deleteReport.DeleteReportCommand;
import com.linkhogar.application.house.deleteReport.DeleteReportCommandHandler;
import com.linkhogar.application.house.get.GetQuery;
import com.linkhogar.application.house.get.GetQueryHandle;
import com.linkhogar.application.house.get.HouseResponse;
import com.linkhogar.application.house.getAllReports.GetAllReportsQuery;
import com.linkhogar.application.house.getAllReports.GetAllReportsQueryHandler;
import com.linkhogar.application.house.getByCity.GetByCityQuery;
import com.linkhogar.application.house.getByCity.GetByCityQueryHandler;
import com.linkhogar.application.house.getByCity.HouseCardResponse;
import com.linkhogar.application.house.getById.GetByIdQuery;
import com.linkhogar.application.house.getById.GetByIdQueryHandler;
import com.linkhogar.application.house.getByOwnerId.GetByOwnerIdQuery;
import com.linkhogar.application.house.getByOwnerId.GetByOwnerIdQueryHandler;
import com.linkhogar.application.house.update.UpdateCommand;
import com.linkhogar.application.house.update.UpdateCommandHandler;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.HouseReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@RestController
@RequestMapping("/houses") // (Spring) Define la ruta base.
@RequiredArgsConstructor //(Lombok) Genera el constructor para los campos 'final'. Imprescindible para inyectar dependencias limpiamente.
@Tag(name = "House")
public class HouseController {
    private final CreateHouseCommandHandler createHouseCommandHandler;
    private final GetByCityQueryHandler getByCityQueryHandler;
    private final GetQueryHandle getQueryHandle;
    private final GetByIdQueryHandler getByIdQueryHandler;
    private final AddHouseImagesCommandHandler addHouseImagesCommandHandler;
    private final SetHouseStatusCommandHandler setHouseStatusCommandHandler;
    private final DeleteHouseCommandHandler deleteHouseCommandHandler;
    private final GetByOwnerIdQueryHandler getHousesByOwnerQueryHandler;
    private final UpdateCommandHandler updateCommandHandler;
    private final DeleteHouseImageCommandHandler deleteHouseImageCommandHandler;
    private final CreateReportCommandHandler createReportCommandHandler;
    private final GetAllReportsQueryHandler getAllReportsQueryHandler;
    private final DeleteReportCommandHandler deleteReportCommandHandler;

    @Operation(
            summary = "Obtención de todas las casas",
            description = "Devuelve el listado de tipo HouseResponse."
    )
    @GetMapping
    public ResponseEntity<Page<HouseResponse>> Houses(@ParameterObject @PageableDefault(page = 0, size=10, sort = "creationDate") Pageable pageable){
        GetQuery query = new GetQuery();

        var result = getQueryHandle.handle(pageable);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Creación de una casa"
    )
    @PostMapping
    public ResponseEntity<?> createHouse(@RequestBody CreateHouseCommand request, Authentication authentication){
        String userId = authentication.getName();

        UUID houseId = createHouseCommandHandler.handle(request, userId);

        Map<String, UUID> response = new HashMap<>();
        response.put("id", houseId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtención de casas por ciudad",
            description = "Devuelve un listado de tipo HouseCardResponse"
    )
    @GetMapping("/city/{city}")
    public ResponseEntity<Page<HouseCardResponse>> getByCity(@PathVariable String city,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size){
        GetByCityQuery query = new GetByCityQuery(city, page, size);

        var result = getByCityQueryHandler.handle(query);

        if (result != null){
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtención de casas por Id",
            description = "Devuelve un tipo HouseResponse"
    )
    @GetMapping("/{id}")
    public ResponseEntity<HouseResponse> getById(@PathVariable String id){
        GetByIdQuery query = new GetByIdQuery(id);

        var result = getByIdQueryHandler.handle(query);

        if (result != null){
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{houseId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadHouseImages(
            @PathVariable UUID houseId,
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            AddHouseImagesCommand command = new AddHouseImagesCommand(houseId, files);
            addHouseImagesCommandHandler.handle(command);

            return ResponseEntity.ok().body("Imagenes correctamente subidas");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error subiendo las imágenes: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeHouseStatus(
            @PathVariable UUID id,
            @RequestBody SetHouseStatusCommand request) {

        SetHouseStatusCommand command = new SetHouseStatusCommand(
                id,
                request.status()
        );

        //TODO queda revisar que el usuario dueño tambien pueda realizar el cambio

        try{
            Result<Void> result = setHouseStatusCommandHandler.handle(command);

            return ResponseEntity.ok().build();
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHouse(@PathVariable UUID id, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String user = authentication.getName();

        if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        DeleteHouseCommand command = new DeleteHouseCommand(
                id,
                UUID.fromString(user),
                authentication.getAuthorities()
        );

        Result<Void> result = deleteHouseCommandHandler.handle(command);
        if(result.isSuccess()){
              return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result.getError());
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<HouseCardResponse>> getHousesByOwner(
            @PathVariable UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        GetByOwnerIdQuery query = new GetByOwnerIdQuery(ownerId, page, size);
        Result<Page<HouseCardResponse>> result = getHousesByOwnerQueryHandler.handle(query);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{houseId}")
    public ResponseEntity<?> updateHouse(
            @PathVariable UUID houseId,
            @RequestBody CreateHouseCommand request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extraemos el Principal (asegúrate de que coincida con tu implementación de seguridad)
        String user = authentication.getName();

        UpdateCommand command = new UpdateCommand(
                houseId,
                request,
                UUID.fromString(user),
                authentication.getAuthorities()
        );

        Result<Void> result = updateCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else {
            // Si el error es de permisos, podrías devolver un 403 (FORBIDDEN)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getError());
        }
    }

    @DeleteMapping("/{houseId}/image")
    public ResponseEntity<?> deleteSingleImage(
            @PathVariable UUID houseId,
            @RequestParam("url") String imageUrl,
            Authentication authentication) {

        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String user = authentication.getName();

        DeleteHouseImageCommand command = new DeleteHouseImageCommand(houseId, imageUrl, UUID.fromString(user), authentication.getAuthorities());
        Result<Void> result = deleteHouseImageCommandHandler.handle(command);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getError());
        }
    }

    @PostMapping("/{houseId}/reports")
    public ResponseEntity<?> createHouseReport(
            @PathVariable String houseId,
            @RequestBody ReportHouseRequest request,
            Authentication authentication)
    {
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        UUID userId = UUID.fromString(authentication.getName());

        CreateReportCommand command = new CreateReportCommand(UUID.fromString(houseId), userId, request.reason(),  request.description());

        Result<Void> result = createReportCommandHandler.handle(command);

        if(result.isSuccess()){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getError());
        }

    }

    @GetMapping("/houseReports/getAll")
    public ResponseEntity<Page<HouseReport>> getAllHouseReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication){
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = UUID.fromString(authentication.getName());

        Pageable pageable = PageRequest.of(page, size);

        Result<Page<HouseReport>> result = getAllReportsQueryHandler.handle(new GetAllReportsQuery(userId), pageable);

        if(result.isSuccess()){
            return ResponseEntity.ok(result.getValue());
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/houseReport/delete/{reportId}")
    public ResponseEntity<?> deleteHouseReport(@PathVariable String reportId, Authentication authentication){
        if(authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Result<Void> result = deleteReportCommandHandler.handle(new DeleteReportCommand(UUID.fromString(reportId)));

        if(result.isSuccess()){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
