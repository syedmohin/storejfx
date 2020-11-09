package com.sunday.service;

import com.sunday.model.Stock;
import com.sunday.model.StockModifiedAmount;
import com.sunday.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public Stock insertData(Stock stock) {
        stock.setStockId(getLastCustomerId());
        if (stock.getBalance() == 0)
            stock.setComplete(true);
        return stockRepository.save(stock);
    }


    @Transactional
    public String getLastCustomerId() {
        var st = stockRepository.findTopByOrderByIdDesc();
        if (st.isPresent()) {
            Stock stock = st.get();
            var id = Integer.parseInt(stock.getStockId().substring(3));
            return "STK" + (id + 1);
        } else {
            return "STK1";
        }
    }

    public Stock findByStockId(String stockId) throws Exception {
        return stockRepository.findByStockId(stockId).orElseThrow(() -> new Exception("Record Not Found"));
    }

    @Transactional
    public List<Stock> getAllData() {
        var it = stockRepository.findAll();
        List<Stock> list = new ArrayList<>();
        it.forEach(list::add);
        return list;
    }

    @Transactional
    public List<String> getAllvehicle() {
        return getAllData()
                .stream()
                .map(Stock::getVehicleNo)
                .collect(Collectors.toList());
    }

    @Transactional
    public Stock updateRate(String stockId, int newValue) {
        var r = stockRepository.findByStockId(stockId);
        Stock newStock = null;
        if (r.isPresent()) {
            var s = r.get();
            s.setRate(newValue);
            s.setTotalAmount(s.getRate() * s.getWeight());
            var paid = s.getStockModifiedAmount()
                    .stream()
                    .mapToInt(StockModifiedAmount::getPaidAmount)
                    .sum();
            s.setBalance(s.getTotalAmount() - paid);
            newStock = stockRepository.save(s);
        }
        return newStock;
    }

    @Transactional
    public Stock updateWeight(String stockId, int newValue) {
        var r = stockRepository.findByStockId(stockId);
        Stock newStock = null;
        if (r.isPresent()) {
            var s = r.get();
            s.setWeight(newValue);
            s.setTotalAmount(s.getRate() * s.getWeight());
            var paid = s.getStockModifiedAmount()
                    .stream()
                    .mapToInt(StockModifiedAmount::getPaidAmount)
                    .sum();
            s.setBalance(s.getTotalAmount() - paid);
            newStock = stockRepository.save(s);
        }
        return newStock;
    }

    @Transactional
    public List<StockModifiedAmount> getPaidRecords(String stockId) {
        var r = stockRepository.findByStockId(stockId);
        return r.orElseThrow().getStockModifiedAmount();
    }

    @Transactional
    public Stock updateBalance(String stockId, int newValue) {
        var r = stockRepository.findByStockId(stockId);
        Stock stock = null;
        if (r.isPresent()) {
            var s = r.get();
            var paid = s.getStockModifiedAmount()
                    .stream()
                    .mapToInt(StockModifiedAmount::getPaidAmount)
                    .sum();
            if (s.getBalance() != 0) {
                var sma = new StockModifiedAmount();
                sma.setPaidAmount(newValue);
                sma.setModifieddate(LocalDate.now());
                s.getStockModifiedAmount().add(sma);
                s.setBalance(s.getTotalAmount() - (paid + newValue));
                stock = stockRepository.save(s);
            }
        }
        return stock;
    }

    @Transactional
    public boolean deleteRecord(String stockId) {
        if (stockRepository.existsByStockId(stockId)) {
            stockRepository.deleteByStockId(stockId);
            return true;
        } else {
            return false;
        }
    }

    public Stock updateVehicleNo(String stockId, String newValue) {
        var st = stockRepository.findByStockId(stockId);
        if (st.isPresent()) {
            var r = st.get();
            r.setVehicleNo(newValue);
            return stockRepository.save(r);
        }
        return null;
    }
}
