package com.test.bank.service;

import com.test.bank.model.ReconciledTransactionDto;
import java.util.List;

public interface TransactionService {

    List<ReconciledTransactionDto> reconcileTransactionEvent(String startingPrimaryId);
}
