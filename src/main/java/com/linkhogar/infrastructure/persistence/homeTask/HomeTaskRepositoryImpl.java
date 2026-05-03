package com.linkhogar.infrastructure.persistence.homeTask;

import com.linkhogar.domain.homeTasks.HomeTask;
import com.linkhogar.domain.homeTasks.HomeTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HomeTaskRepositoryImpl implements HomeTaskRepository {
    private final JpaHomeTaskRepository jpaHomeTaskRepository;

    @Override
    public HomeTask save(HomeTask task) {
        return jpaHomeTaskRepository.save(task);
    }

    @Override
    public Optional<HomeTask> findById(UUID id) {
        return jpaHomeTaskRepository.findById(id);
    }

    @Override
    public List<HomeTask> findByHomeId(UUID homeId) {
        return jpaHomeTaskRepository.findByHomeId(homeId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaHomeTaskRepository.deleteById(id);
    }
}
