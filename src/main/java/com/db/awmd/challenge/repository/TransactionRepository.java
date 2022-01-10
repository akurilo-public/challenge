package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Transaction;

public interface TransactionRepository {

     void transfer(Transaction transaction);

}
