package com.linkhogar.infrastructure.rest.house;

import com.linkhogar.application.house.Image.addImage.AddHouseImagesCommand;
import com.linkhogar.application.house.Image.addImage.AddHouseImagesCommandHandler;
import com.linkhogar.application.house.create.CreateHouseCommand;
import com.linkhogar.application.house.create.CreateHouseCommandHandler;
import com.linkhogar.application.house.get.GetQuery;
import com.linkhogar.application.house.get.GetQueryHandle;
import com.linkhogar.application.house.get.HouseResponse;
import com.linkhogar.application.house.getByCity.GetByCityQuery;
import com.linkhogar.application.house.getByCity.GetByCityQueryHandler;
import com.linkhogar.application.house.getByCity.HouseCardResponse;
import com.linkhogar.application.house.getById.GetByIdQuery;
import com.linkhogar.application.house.getById.GetByIdQueryHandler;
import com.linkhogar.infrastructure.externalServices.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    private final CloudinaryService cloudinaryService;


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
            summary = "Creación de una casa",
            description = "Recupera la información pública de un usuario dado su UUID. No devuelve la contraseña."
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
            @RequestParam("files") List<MultipartFile> files) {

        try {
            AddHouseImagesCommand command = new AddHouseImagesCommand(houseId, files);
            addHouseImagesCommandHandler.handle(command);

            return ResponseEntity.ok().body("Imagenes correctamente subidas");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error subiendo las imágenes: " + e.getMessage());
        }
    }

}
