package com.vbs.demo.controller;

import com.vbs.demo.dto.TransactionDto;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.TransactionRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TransactionController {
    @Autowired
    UserRepo userRepo;

    @Autowired
    TransactionRepo transactionRepo;



    @PostMapping("/deposit")
    public String deposite(@RequestBody TransactionDto obj)
    {
        User user = userRepo.findById(obj.getId())
                .orElseThrow(()->new RuntimeException("not found"));

        double newBalance = user.getBalance() + obj.getAmount();
        user.setBalance(newBalance);
        userRepo.save(user);

        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Rs" + obj.getAmount()+ "Deposit Successful");
        t.setUserId(obj.getId());

        transactionRepo.save(t);
        return "Deposite Successful";
    }



    @PostMapping("/withdraw")
    public String withdraw(@RequestBody TransactionDto obj)
    {
        User user = userRepo.findById(obj.getId())
                .orElseThrow(()->new RuntimeException("not found"));

        double newBalance = user.getBalance()  - obj.getAmount();
        if(newBalance<0)
        {
            return "Balance Insufficient";
        }
        user.setBalance(newBalance);
        userRepo.save(user);

        Transaction t = new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Rs" + obj.getAmount()+ "withdraw Successful");
        t.setUserId(obj.getId());

        transactionRepo.save(t);
        return "withdraw Successful";
    }


    @PostMapping("/transfer")
    public String transfer(@RequestBody TransactionDto obj)
    {
       User sender = userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("not found"));
        User rec = userRepo.findByUsername(obj.getUsername());


        if(rec==null) return "Username not found";
       if(sender.getId()== rec.getId()) return "self transaction not allowed";
       if(obj.getAmount()<1) return "invalid amount";
       double sbalance = sender.getBalance() - obj.getAmount();
       double rbalance = rec.getBalance() + obj.getAmount();

       if(sbalance<0)return "Insufficient Balance";

       sender.setBalance(sbalance);
       rec.setBalance(rbalance);
       userRepo.save(sender);
       userRepo.save(rec);


       Transaction t1 = new Transaction();
       Transaction t2 = new Transaction();

       t1.setAmount(obj.getAmount());
       t1.setCurrBalance(sbalance);
       t1.setDescription("Rs" + obj.getAmount()+ "send to user" + rec.getUsername());
       t1.setUserId(sender.getId());


        t2.setAmount(obj.getAmount());
        t2.setCurrBalance(rbalance);
        t2.setDescription("Rs" + obj.getAmount()+ "Recieved from  user" + sender.getUsername());
        t2.setUserId(rec.getId());

       transactionRepo.save(t1);
       transactionRepo.save(t2);
       return "Transfer Done Successfully";



    }
@GetMapping("/passbook/{id}")
    public List<Transaction> getPassbook(@PathVariable int id)
{
    return transactionRepo.findByUserId(id);
}
}
