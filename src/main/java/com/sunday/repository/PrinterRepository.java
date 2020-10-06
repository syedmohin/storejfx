package com.sunday.repository;

import com.sunday.model.Printer;
import org.springframework.data.repository.CrudRepository;

public interface PrinterRepository extends CrudRepository<Printer, Integer> {
}
