package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Transaction;

public interface TransactionService {

    void executeTransaction(Transaction transaction) throws InterruptedException;
}
