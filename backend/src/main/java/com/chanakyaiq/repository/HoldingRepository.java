package com.chanakyaiq.repository;

import com.chanakyaiq.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByUserId(String userId);
    Optional<Holding> findByUserIdAndSymbol(String userId, String symbol);
}
