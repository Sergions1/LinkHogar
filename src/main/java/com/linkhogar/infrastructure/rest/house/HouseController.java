package com.linkhogar.infrastructure.rest.house;

import com.linkhogar.application.house.create.CreateHouseCommand;
import com.linkhogar.application.house.create.CreateHouseCommandHandler;
import com.linkhogar.application.house.get.GetQuery;
import com.linkhogar.application.house.get.GetQueryHandle;
import com.linkhogar.application.house.get.HouseResponse;
import com.linkhogar.application.house.getByCity.GetByCityQuery;
import com.linkhogar.application.house.getByCity.GetByCityQueryHandler;
import com.linkhogar.domain.house.House;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import static com.linkhogar.domain.common.result.ErrorType.*;

@RestController
@RequestMapping("/houses") // (Spring) Define la ruta base.
@RequiredArgsConstructor //(Lombok) Genera el constructor para los campos 'final'. Imprescindible para inyectar dependencias limpiamente.
@Tag(name = "House")
public class HouseController {
    private final CreateHouseCommandHandler createHouseCommandHandler;
    private final GetByCityQueryHandler getByCityQueryHandler;
    private final GetQueryHandle getQueryHandle;

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

        createHouseCommandHandler.handle(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Obtención de casas por ciudad",
            description = "Devuelve un listado de tipo HouseCardResponse"
    )
    @GetMapping("/{city}")
    public ResponseEntity<?> getByCity(@PathVariable String city){
        GetByCityQuery query = new GetByCityQuery(city);

        var result = getByCityQueryHandler.handle(query);

        if (result != null){
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.noContent().build();
    }

}
