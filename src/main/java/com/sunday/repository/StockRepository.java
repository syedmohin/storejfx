package com.sunday.repository;

import com.sunday.model.Stock;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StockRepository extends CrudRepository<Stock, Integer> {

    Optional<Stock> findByStockId(String stockId);

    Optional<Stock> findTopByOrderByIdDesc();

    boolean existsByStockId(String stockId);

    void deleteByStockId(String stockId);
}
