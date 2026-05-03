package com.linkhogar.domain.homeTasks;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeTaskRepository {
    HomeTask save(HomeTask task);
    Optional<HomeTask> findById(UUID id);
    List<HomeTask> findByHomeId(UUID homeId);
    void deleteById(UUID id);
}
