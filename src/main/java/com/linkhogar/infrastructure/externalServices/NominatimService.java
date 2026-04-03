package com.linkhogar.infrastructure.externalServices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NominatimService {

    private final RestTemplate restTemplate;

    public double[] getCoordinates(String city) {
        String url = "https://nominatim.openstreetmap.org/search?q="
                + city + ",España&format=json&limit=1&countrycodes=es";

        List<Map> results = restTemplate.getForObject(url, List.class);

        if (results != null && !results.isEmpty()) {
            double lat = Double.parseDouble(results.get(0).get("lat").toString());
            double lon = Double.parseDouble(results.get(0).get("lon").toString());
            return new double[]{lat, lon};
        }

        return null;
    }
}