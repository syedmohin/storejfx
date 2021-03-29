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
                <html>
                       <head>
                         <style>
                           body {
                             margin: 0;
                             padding: 0;
                             width: 303px;
                           }
                           table {
                             padding: 5px;
                             width: 100%%;
                           }
                         </style>
                       </head>
                       <body>
                         <p style="font-size: 20px; text-align: center; font-weight: bold">AFC</p>
                         <p style="font-size: 15px; text-align: center; font-weight: bold">
                           sattarbagh,hyd
                         </p>
                         <p style="font-size: 15px; font-weight: bold; text-align: center">
                           9059085684,9912047816
                         </p>
                         <table>
                           <tr>
                             <td style="font-size: 12px">Customer Name</td>
                             <td style="font-size: 15px; font-weight: bold">%s</td>
                           </tr>
                           <tr>
                             <td style="font-size: 12px">Trans ID</td>
                             <td style="font-size: 15px; font-weight: bold">%s</td>
                           </tr>
                           <tr>
                             <td style="font-size: 12px">Date</td>
                             <td style="font-size: 15px; font-weight: bold">%s</td>
                           </tr>
                           <tr>
                             <td style="font-size: 12px">Weigth</td>
                             <td style="font-size: 15px; font-weight: bold">%d</td>
                           </tr>
                           <tr>
                             <td style="font-size: 12px">Crate</td>
                             <td style="font-size: 15px; font-weight: bold">%d</td>
                           </tr>
                           <tr>
                             <td style="font-size: 12px">Pending Crate</td>
                             <td style="font-size: 15px; font-weight: bold">%d</td>
                           </tr>
                           <tr>
                             <td style="font-size: 12px">Total Amount</td>
                             <td style="font-size: 15px; font-weight: bold">%d</td>
                           </tr>
                           <tr>
                             <td style="font-size: 12px">Paid</td>
                             <td style="font-size: 15px; font-weight: bold">%d</td>
                           </tr>
                           <tr>
                             <td style="font-size: 12px">Balance</td>
                             <td style="font-size: 15px; font-weight: bold">%d</td>
                           </tr>
                         </table>
                       </body>
                     </html>
                     """.formatted(c.getCustomerName(), c.getCustomerId(), c.getDate().toString(), c.getWeight(), c.getCrate(), (c.getCrate() - c.getReturnedCrate()), c.getTotalAmount(), paid, c.getBalance());
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
                                 body {
                                         margin: 0;
                                         padding: 0;
                                         width: 303px;
                                       }
                                       table {
                                         padding: 5px;
                                         width: 100%%;
                                       }
                                     </style>
                                   </head>
                                   <body>
                                     <div>
                                       <p style="font-size: 20px; text-align: center; font-weight: bold">AFC</p>
                         <p style="font-size: 15px; text-align: center; font-weight: bold">
                           sattarbagh,hyd
                         </p>
                         <p style="font-size: 15px; font-weight: bold; text-align: center">
                           9059085684,9912047816
                         </p>
                                     </div>
                                     <div>
                                       <table>
                                         <tr>
                                           <td style="font-size: 12px">Vehicle No</td>
                                           <td style="font-size: 15px; font-weight: bold">%s</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 12px">Weight</td>
                                           <td style="font-size: 15px; font-weight: bold">%d</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 12px">Rate</td>
                                           <td style="font-size: 15px; font-weight: bold">%d</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 12px">Total Amount</td>
                                           <td style="font-size: 15px; font-weight: bold">%d</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 12px">Paid Amount</td>
                                           <td style="font-size: 15px; font-weight: bold">%d</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 12px">Date</td>
                                           <td style="font-size: 15px; font-weight: bold">%s</td>
                                         </tr>
                                         <tr>
                                           <td style="font-size: 12px">Balance</td>
                                           <td style="font-size: 15px; font-weight: bold">%d</td>
                                         </tr>
                                       </table>
                                     </div>
                                     <div>
                                       <table>
                                         <tr>
                                           <th style="font-size: 12px">Paid Amount</th>
                                           <th style="font-size: 12px">on Date</th>
                                         </tr>
                """.formatted(s.getVehicleNo(), s.getWeight(), s.getRate(), s.getTotalAmount(), paid, s.getDate().toString(), s.getBalance()));
        for (StockModifiedAmount s1 : sma) {
            data.append("""
                            <tr>
                              <td style="font-size: 15px; font-weight: bold">%d</td>
                              <td style="font-size: 15px; font-weight: bold">%s</td>
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
