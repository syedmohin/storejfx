package com.sunday.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class StockModifiedAmount implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    private LocalDate modifieddate;
    private int paidAmount;
}
