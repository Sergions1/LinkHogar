package com.linkhogar.infrastructure.persistence.expense;

import com.linkhogar.domain.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaExpenseRepository extends JpaRepository<Expense, UUID> {
    @Query("SELECT e FROM Expense e WHERE e.homeId = :homeId ORDER BY e.createdAt DESC")
    List<Expense> findByHomeIdOrderByCreatedAtDesc(@Param("homeId") UUID homeId);
}
