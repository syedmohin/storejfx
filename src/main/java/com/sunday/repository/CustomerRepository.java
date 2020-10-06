package com.sunday.repository;

import com.sunday.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    Optional<Customer> findByCustomerId(String cust);

    void deleteByCustomerId(String customerId);

    boolean existsByCustomerId(String customerId);

    Optional<Customer> findTopByOrderByIdDesc();
}
