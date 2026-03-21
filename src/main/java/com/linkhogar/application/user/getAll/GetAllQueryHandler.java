package com.linkhogar.application.user.getAll;

import com.linkhogar.application.user.getById.UserResponse;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllQueryHandler {
    private final UserRepository userRepository;

    public Page<UserResponse> handle(GetAllQuery query) {
        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by("registerDate").descending()
        );

        return userRepository.findAllFiltered(
                query.search(),
                query.role(),
                query.enabled(),
                pageable
        ).map(UserResponse::mapToResponse);
    }
}