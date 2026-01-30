package com.linkhogar.infrastructure.rest.house;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.linkhogar.domain.common.result.ErrorType.*;

@RestController
@RequestMapping("/houses") // (Spring) Define la ruta base.
@RequiredArgsConstructor //(Lombok) Genera el constructor para los campos 'final'. Imprescindible para inyectar dependencias limpiamente.
public class HouseController {
}
