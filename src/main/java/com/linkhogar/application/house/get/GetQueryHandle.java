package com.linkhogar.application.house.get;

import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetQueryHandle {
    private final HouseRepository houseRepository;

    public Page<HouseResponse> handle(Pageable pageable){
        Page<House> housePage = houseRepository.getAll(pageable);

        return housePage.map(HouseResponse::mapToResponse);
    }
}
