package com.linkhogar.application.house.getByOwnerId;

import com.linkhogar.application.house.getByCity.HouseCardResponse;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseErrors;
import com.linkhogar.domain.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetByOwnerIdQueryHandler {
    private final HouseRepository houseRepository;

    public Result<Page<HouseCardResponse>> handle(GetByOwnerIdQuery query) {
      try{
            Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), Sort.by(Sort.Direction.DESC, "creationDate"));

            Page<House> housesPage = houseRepository.findByOwnerId(query.getOwnerId(), pageable);

            if (housesPage.isEmpty()) {
                return Result.success(Page.empty(pageable));
            }

            List<HouseCardResponse> houseCards = housesPage.getContent().stream()
                    .map(HouseCardResponse::toHouseCardResponse)
                    .toList();

            Page<HouseCardResponse> resultPage = new PageImpl<>(
                    houseCards,
                    pageable,
                    housesPage.getTotalElements()
            );

            return Result.success(resultPage);
        }catch (Exception e){
          return Result.failure(HouseErrors.NotFound());
        }
    }
}
