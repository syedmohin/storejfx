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
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String customerName;
    @Column(name = "customerId", unique = true)
    private String customerId;
    private int weight;
    private int rate;
    private int crate;
    private int returnedCrate;
    private int totalAmount;
    private int balance;
    private boolean complete;
    private LocalDate date;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "customerId")
    private List<CustomerModifiedAmount> customerModifiedAmount = new ArrayList<>();

    @PrePersist
    private void setDateAndTime() {
        date = LocalDate.now();
    }
}