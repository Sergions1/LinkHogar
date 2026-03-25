package com.linkhogar.application.house.getPendingHouses;

import com.linkhogar.application.house.get.HouseResponse;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPendingHousesQueryHandler {

    private final HouseRepository houseRepository;

    public Page<HouseResponse> handle(GetPendingHousesQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());

        return houseRepository.findByPublicationStatus(PublicationStatus.PENDING_REVIEW, pageable)
              .map(HouseResponse::mapToResponse);
    }
}
