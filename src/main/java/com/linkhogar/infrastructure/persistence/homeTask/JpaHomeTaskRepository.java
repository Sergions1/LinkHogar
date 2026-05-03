package com.linkhogar.infrastructure.persistence.homeTask;

import com.linkhogar.domain.homeTasks.HomeTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaHomeTaskRepository extends JpaRepository<HomeTask, UUID> {
    List<HomeTask> findByHomeId(UUID homeId);
}
