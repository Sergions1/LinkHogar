package com.linkhogar.application.house.getByCity;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.infrastructure.externalServices.NominatimService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetByCityQueryHandler {
    private final HouseRepository houseRepository;
    private final NominatimService nominatimService;

    /**
     * @summary Maneja la consulta para obtener inmuebles por ciudad, ordenándolos por proximidad.
     * @param query La consulta que contiene la ciudad y la información de paginación.
     * @return Una página de {@link HouseCardResponse} con los inmuebles encontrados. Si se obtienen las coordenadas de la ciudad, los resultados se ordenan por distancia ascendente.
     */
    public Page<HouseCardResponse> handle(GetByCityQuery query){
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<House> housePage = houseRepository.findByCity(query.city(), pageable);

        double[] coords = nominatimService.getCoordinates(query.city());

        if (coords != null) {
            // Ordenamos por distancia
            List<House> sorted = housePage.getContent().stream()
                    .sorted((a, b) -> {
                        double distA = haversine(coords[0], coords[1],
                                a.getAddress().getLatitude() != null ? a.getAddress().getLatitude() : 0,
                                a.getAddress().getLongitude() != null ? a.getAddress().getLongitude() : 0);
                        double distB = haversine(coords[0], coords[1],
                                b.getAddress().getLatitude() != null ? b.getAddress().getLatitude() : 0,
                                b.getAddress().getLongitude() != null ? b.getAddress().getLongitude() : 0);
                        return Double.compare(distA, distB);
                    })
                    .toList();

            return new org.springframework.data.domain.PageImpl<>(
                    sorted.stream().map(this::toHouseCardResponse).toList(),
                    pageable,
                    housePage.getTotalElements()
            );
        }

        return housePage.map(this::toHouseCardResponse);
    }

    /**
     * @summary Calcula la distancia en kilómetros entre dos puntos geográficos usando la fórmula de Haversine.
     * @param lat1 Latitud del primer punto.
     * @param lon1 Longitud del primer punto.
     * @param lat2 Latitud del segundo punto.
     * @param lon2 Longitud del segundo punto.
     * @return La distancia en kilómetros.
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * @summary Convierte una entidad {@link House} a su DTO de respuesta {@link HouseCardResponse}.
     * @param house La entidad del inmueble a convertir.
     * @return El objeto DTO con los datos para la tarjeta de inmueble.
     */
    private HouseCardResponse toHouseCardResponse(House house) {
        return new HouseCardResponse(
                house.getId().toString(),
                house.getTitle(),
                house.getDescription(),
                house.getPublicationDate(),
                house.getUpdateDate(),
                house.getHouseType(),
                house.getStatus(),
                house.getSize(),
                house.getRooms(),
                house.getBaths(),
                house.getPrice(),
                house.getAddress()
        );
    }
}
