package com.linkhogar.application.Address;

import com.linkhogar.domain.address.Address;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor

public class AddressResponse {

    private UUID id;

    private String street;
    private int number;
    private String floor;
    private String door;
    private String city;
    private String province;
    private String country;

    // --- Geolocalización ---
    // Usamos Double (clase wrapper) para permitir nulos.
    private Double latitude;
    private Double longitude;

    public static AddressResponse mapAddress(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getFloor(),
                address.getDoor(),
                address.getCity(),
                address.getProvince(),
                address.getCountry(),
                address.getLatitude(),
                address.getLongitude()
        );
    }
}

