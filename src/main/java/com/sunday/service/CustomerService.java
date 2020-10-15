package com.sunday.service;

import com.sunday.model.Customer;
import com.sunday.model.CustomerModifiedAmount;
import com.sunday.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public List<Customer> getAllData() {
        var itre = customerRepository.findAll();
        List<Customer> list = new ArrayList<>();
        itre.forEach(list::add);
        return list;
    }

    @Transactional
    public List<String> getAllCustomerNames() {
        return getAllData()
                .stream()
                .map(Customer::getCustomerName)
                .distinct()
                .collect(toList());
    }

    @Transactional
    public Customer insert(Customer customer, CustomerModifiedAmount cma) {
        if (customer.getBalance() == 0)
            customer.setComplete(true);
        customer.setReturnedCrate(0);
        customer.setCustomerId(getLastCustomerId());
        customer.getCustomerModifiedAmount().add(cma);
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer findByCustomerId(String cusId) throws Exception {
        var customer = customerRepository.findByCustomerId(cusId);
        return customer.orElseThrow(() -> new Exception("No record found!!!"));
    }

    @Transactional
    public String getLastCustomerId() {
        var customerId = customerRepository.findTopByOrderByIdDesc();
        if (customerId.isPresent()) {
            Customer customer = customerId.get();
            var id = Integer.parseInt(customer.getCustomerId().substring(4));
            return "CUST" + (id + 1);
        } else {
            return "CUST1";
        }
    }

    @Transactional
    public void updateCustomerName(String custId, String newValue) {
        var c = customerRepository.findByCustomerId(custId);
        c.ifPresent(s -> {
            s.setCustomerName(newValue);
            customerRepository.save(s);
        });
    }

    @Transactional
    public Customer updateCrate(String custId, int newValue) {
        var c = customerRepository.findByCustomerId(custId);
        if (c.isPresent()) {
            var s = c.get();
            s.setCrate(newValue);
            s.setReturnedCrate(newValue - s.getReturnedCrate());
            return customerRepository.save(s);
        }
        return null;
    }

    @Transactional
    public Customer updateWeight(String custId, int newValue) {
        var c = customerRepository.findByCustomerId(custId);
        Customer customer = null;
        if (c.isPresent()) {
            var s = c.get();
            s.setWeight(newValue);
            s.setTotalAmount(s.getWeight() * s.getRate());
            var paid = s.getCustomerModifiedAmount()
                    .stream()
                    .mapToInt(CustomerModifiedAmount::getPaidAmount)
                    .sum();
            s.setBalance(s.getTotalAmount() - paid);
            customer = customerRepository.save(s);
        }
        return customer;
    }

    @Transactional
    public Customer updateReturnCrate(String custId, int newValue) {
        var c = customerRepository.findByCustomerId(custId);
        if (c.isPresent()) {
            var s = c.get();
            s.setReturnedCrate(s.getReturnedCrate() + newValue);
            return customerRepository.save(s);
        }
        return null;
    }

    @Transactional
    public Customer updateRate(String custId, int newValue) {
        var c = customerRepository.findByCustomerId(custId);
        Customer customer = null;
        if (c.isPresent()) {
            var s = c.get();
            s.setRate(newValue);
            var paid = s.getCustomerModifiedAmount()
                    .stream()
                    .mapToInt(CustomerModifiedAmount::getPaidAmount)
                    .sum();
            s.setTotalAmount(s.getWeight() * s.getRate());
            s.setBalance(s.getTotalAmount() - paid);
            if (s.getBalance() == 0)
                s.setComplete(true);
            customer = customerRepository.save(s);
        }
        return customer;
    }

    @Transactional
    public Customer updateBalance(String custId, int newValue) {
        var c = customerRepository.findByCustomerId(custId);
        if (c.isPresent()) {
            var s = c.get();
            var paid = s.getCustomerModifiedAmount()
                    .stream()
                    .mapToInt(CustomerModifiedAmount::getPaidAmount)
                    .sum();
            var cma = new CustomerModifiedAmount();
            cma.setPaidAmount(newValue);
            cma.setModifiedDate(LocalDate.now());
            s.getCustomerModifiedAmount().add(cma);
            s.setBalance(s.getTotalAmount() - (paid + newValue));
            if (s.getBalance() < 0) {
                s.setComplete(true);
            }
            return customerRepository.save(s);
        }
        return null;
    }

    @Transactional
    public boolean deleteRecord(String customerId) {
        if (customerRepository.existsByCustomerId(customerId)) {
            customerRepository.deleteByCustomerId(customerId);
            return true;
        } else
            return false;
    }
}
