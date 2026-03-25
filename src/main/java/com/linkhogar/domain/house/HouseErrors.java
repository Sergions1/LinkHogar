package com.linkhogar.domain.house;

import com.linkhogar.domain.common.result.Error;

import java.util.UUID;

public class HouseErrors {
    private HouseErrors(){}

    public static com.linkhogar.domain.common.result.Error NotFound(UUID houseId) {
        return com.linkhogar.domain.common.result.Error.notFound(
                "House.NotFound",
                "La vivienda con el Id = '" + houseId + "' no fue encontrado"
        );
    }

}
