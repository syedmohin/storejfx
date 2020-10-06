package com.sunday.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Stock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String vehicleNo;
    @Column(unique = true)
    private String stockId;
    private int weight;
    private int rate;
    private int totalAmount;
    private boolean complete;
    private int Balance;
    private LocalDate date;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "stockId")
    private List<StockModifiedAmount> stockModifiedAmount = new ArrayList<>();

    @PrePersist
    private void setDateInDatabase() {
        this.date = LocalDate.now();
    }

}