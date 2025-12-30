package com.vbs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

     int id;          // sender user id
     String username; // receiver username
     double amount;   // transfer amount

}
