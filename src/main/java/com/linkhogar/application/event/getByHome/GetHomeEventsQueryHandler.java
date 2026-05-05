package com.linkhogar.application.event.getByHome;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.event.HomeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetHomeEventsQueryHandler {

    private final HomeEventRepository homeEventRepository;

    @Transactional(readOnly = true)
    public Result<List<HomeEventResponse>> handle(GetHomeEventsQuery query) {

        List<HomeEventResponse> events = homeEventRepository.findByHomeIdOrderByStartDateAsc(query.homeId())
                .stream()
                .map(event -> new HomeEventResponse(
                        event.getId(),
                        event.getCreatorId(),
                        event.getCreatorName(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.isAllDay(),
                        event.getReminderMinutesBefore()
                ))
                .collect(Collectors.toList());

        return Result.success(events);
    }
}