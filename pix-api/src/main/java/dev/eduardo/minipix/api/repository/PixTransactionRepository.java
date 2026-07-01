package dev.eduardo.minipix.api.repository;

import dev.eduardo.minipix.api.entity.PixTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PixTransactionRepository extends JpaRepository<PixTransactionEntity, String> {
}
