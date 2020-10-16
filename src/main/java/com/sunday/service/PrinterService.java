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
        return """
                <html lang="en">
                  <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">\s
                    <style>
                       *{
                         font-size: 15px;
                         margin: 0;
                         padding: 0;
                       }
                    </style>
                  </head>
                  <body>
                      <p style="font-size:20px;text-align: center;">AFC</p>
                      <p style="text-align: center;">sattarbagh,hyd</p>
                      <p style="font-size:12px;">9059085684,9912047816</p>
                      <table>
                        <tr>
                          <td style="font-size:12px;">Customer Name</td>
                          <td style="font-size:10px;">%s</td>
                        </tr>
                        <tr>
                          <td style="font-size:12px;">Trans ID</td>
                          <td style="font-size:12px;">%s</td>
                        </tr>
                        <tr>
                          <td style="font-size:12px;">Date</td>
                          <td style="font-size:10px;">%s</td>
                        </tr>
                        <tr>
                          <td style="font-size:12px;">Weigth</td>
                          <td style="font-size:12px;">%d</td>
                        </tr>
                        <tr>
                          <td style="font-size:12px;">Crate</td>
                          <td style="font-size:12px;">%d</td>
                        </tr>
                        <tr>
                          <td style="font-size:12px;">Pending Crate</td>
                          <td style="font-size:12px;">%d</td>
                        </tr>
                        <tr>
                          <td style="font-size:12px;">Total Amount</td>
                          <td style="font-size:12px;">%d</td>
                        </tr>
                        <tr>
                          <td style="font-size:12px;">Paid</td>
                          <td style="font-size:12px;">%d</td>
                        </tr>
                        <tr>
                          <td style="font-size:12px;">Balance</td>
                          <td style="font-size:12px;">%d</td>
                        </tr>
                      </table>
                  </body>
                </html>
                """.formatted(c.getCustomerName(), c.getCustomerId(), c.getDate(), c.getWeight(), c.getCrate(), (c.getCrate() - c.getReturnedCrate()), c.getTotalAmount(), paid, c.getBalance());
    }

    public String printStock(Stock s) {

        var sma = s.getStockModifiedAmount();
        var paid = sma.stream()
                .mapToInt(StockModifiedAmount::getPaidAmount)
                .sum();
        StringBuilder data = new StringBuilder("""
                <html lang="en">
                   <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                              <style>
                                       * {
                                         font-size: 15px;
                                         margin: 0;
                                         padding: 0;
                                       }
                                       .table {
                                         border-collapse: collapse;
                                         text-align: center;
                                       }
                                 
                                       .td {
                                         border: 1px solid black;
                                       }
                                     </style>
                                   </head>
                                   <body>
                                     <div>
                                       <p style="font-size: 20px; text-align: center">AFC</p>
                                       <p style="text-align: center">sattarbagh,hyd</p>
                                     </div>
                                     <div>
                                       <table>
                                         <tr>
                                           <td style="font-size: 12px">Vehicle No</td>
                                           <td style="font-size: 15px">%s</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 15px">Weight</td>
                                           <td style="font-size: 15px">%d</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 15px">Rate</td>
                                           <td style="font-size: 15px">%d</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 15px">Total Amount</td>
                                           <td style="font-size: 15px">%d</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 15px">Paid Amount</td>
                                           <td style="font-size: 15px">%d</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 15px">Date</td>
                                           <td style="font-size: 15px">%s</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 15px">Balance</td>
                                           <td style="font-size: 15px">%d</td>
                                         </tr>
                                       </table>
                                     </div>
                                     <div>
                                       <table class="td table">
                                         <tr>
                                           <th class="td">Paid Amount</th>
                                           <th class="td">on Date</th>
                                         </tr>
                """.formatted(s.getVehicleNo(), s.getWeight(), s.getRate(), s.getTotalAmount(), paid, s.getDate().toString(), s.getBalance()));
        for (StockModifiedAmount s1 : sma) {
            data.append("""
                            <tr>
                              <td class="td">%d</td>
                              <td class="td">%s</td>
                            </tr>
                    """.formatted(s1.getPaidAmount(), s1.getModifieddate()));
        }
        data.append("""
                </table>
                    </div>
                  </body>
                </html>
                """);
        return data.toString();
    }
}
