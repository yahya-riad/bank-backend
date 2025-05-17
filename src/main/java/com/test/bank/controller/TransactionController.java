package com.test.bank.controller;


import com.test.bank.model.ReconciledTransactionDto;
import com.test.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
class TransactionController {

    private final TransactionService transactionService;


    @GetMapping("/chain/{primaryId}")
    public List<ReconciledTransactionDto> getChain(@PathVariable String primaryId) {
        return transactionService.reconcileTransactionEvent(primaryId);
    }
}