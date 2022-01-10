package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TransactionRepositoryInMemory implements TransactionRepository{

    private final AccountsRepository accountsRepository;
    private final BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<>();

    @Override
    @SneakyThrows
    public void transfer(Transaction transaction) {
        synchronized (transactions) {
            transactions.put(transaction);
            Transaction transfer = transactions.poll(2, TimeUnit.SECONDS);
            accountsRepository.decreaseBalance(transfer.getAccountIdFrom(), transfer.getTransfer());
            accountsRepository.increaseBalance(transfer.getAccountIdTo(), transfer.getTransfer());

            // Only for test
            Thread.sleep(1000);
        }
    }
}
