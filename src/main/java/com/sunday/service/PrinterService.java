package com.sunday.service;

import com.sunday.model.Customer;
import com.sunday.model.CustomerModifiedAmount;
import com.sunday.model.Stock;
import com.sunday.model.StockModifiedAmount;
import org.springframework.stereotype.Service;

@Service
public class PrinterService {

    public String printCustomer(Customer c) {
        var paid = c.getCustomerModifiedAmount()
                .stream()
                .mapToInt(CustomerModifiedAmount::getPaidAmount)
                .sum();
        var cma = c.getCustomerModifiedAmount();
        var data = """
                    <html lang="en">
                      <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            *{
                                
                           }
                          .table {
                            border-collapse: collapse;
                            width: 100px;
                          }
                    
                          .td {
                            border: 1px solid black;
                            height: 50px;
                          }
                        </style>
                      </head>
                      <body>
                        <hr />
                        <div>
                          <h2 style="text-align: center;">Name</h2>
                        </div>
                        <div>
                          <table>
                            <tr>
                              <td>Customer Name</td>
                              <td>%s</td>
                              <td>Trans ID</td>
                              <td>%s</td>
                            </tr>
                            <tr>
                              <td>Weigth</td>
                              <td>%d</td>
                              <td>Rate</td>
                              <td>%d</td>
                            </tr>
                            <tr>
                              <td>Crate</td>
                              <td>%d</td>
                              <td>Pending Crate</td>
                              <td>%d</td>
                            </tr>
                            <tr>
                              <td>Total Amount</td>
                              <td>%d</td>
                              <td>Paid</td>
                              <td>%d</td>
                            </tr>
                            <tr>
                              <td></td>
                              <td></td>
                              <td>Balance</td>
                              <td>%d</td>
                            </tr>
                          </table>
                        </div>
                        <hr>
                        <div>
                          <table class="table td">
                          <tr>
                        <th class="td">Paid Amount</th>
                        <th class="td">On Date</th>
                    </tr>
                """.formatted(c.getCustomerName(), c.getCustomerId(), c.getWeight(), c.getRate(), c.getCrate(), (c.getCrate() - c.getReturnedCrate()), c.getTotalAmount(), paid, c.getBalance());
        for (CustomerModifiedAmount cm : cma) {
            data += """
                    <tr>
                        <td class="td">%d</td>
                        <td class="td">%s</td>
                    </tr>
                    """.formatted(cm.getPaidAmount(), cm.getModifiedDate().toString());
        }
        data += """
                </table>
                    </div>
                    <hr>
                    <div style="text-align: center;">Thank you</div>
                    <hr>
                  </body>
                </html>
                """;
        return data;
    }

    public String printStock(Stock s) {
        var sma = s.getStockModifiedAmount();
        var paid = sma.stream()
                .mapToInt(StockModifiedAmount::getPaidAmount)
                .sum();
        String data = """
                <html lang="en">
                  <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <style>
                      .table {
                      width: 100%;
                        text-align: center;
                        border-collapse: collapse;
                      }
                      .td {
                        border: 1px solid black;
                        heigth: 50%;
                      }
                    </style>
                  </head>
                  <body>
                    <div>
                      <h2 style="text-align: center">Name</h2>
                    </div>
                    <div>
                      <table>
                        <tr>
                          <td>Vehicle No</td>
                          <td>%s</td>
                        </tr>
                        <tr>
                          <td>Weight</td>
                          <td>%d</td>
                          <td>Rate</td>
                          <td>%d</td>
                        </tr>
                        <tr>
                          <td>Total Amount</td>
                          <td>%d</td>
                          <td>Paid Amount</td>
                          <td>%d</td>
                        </tr>
                        <tr>
                          <td>Date</td>
                          <td>%s</td>
                          <td>Balance</td>
                          <td>%d</td>
                        </tr>
                      </table>
                    </div>
                    <div>
                      <table class="td table">
                      <tr>
                              <th class="td">Paid Amount</th>
                              <th class="td">on Date</th>
                            </tr>
                """.formatted(s.getVehicleNo(), s.getWeight(), s.getRate(), s.getTotalAmount(), paid, s.getDate().toString(), s.getBalance());
        for (StockModifiedAmount s1 : sma) {
            data += """
                            <tr>
                              <td class="td">%d</td>
                              <td class="td">%s</td>
                            </tr>
                    """.formatted(s1.getPaidAmount(), s1.getModifieddate());
        }
        data += """
                </table>
                    </div>
                  </body>
                </html>
                """;
        return data;
    }
}
