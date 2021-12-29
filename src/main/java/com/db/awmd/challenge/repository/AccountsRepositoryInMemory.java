package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


@Setter
@Repository
@AllArgsConstructor
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }


    @Override
    @SneakyThrows
    public void transfer(Transaction transaction) {
        synchronized (transactions) {
            transactions.put(transaction);
            Transaction transfer = transactions.poll(2, TimeUnit.SECONDS);
            accounts.computeIfPresent(transaction.getAccountIdFrom(), (key, account) -> account.decreaseBalance(transfer.getTransfer()));
            accounts.computeIfPresent(transaction.getAccountIdTo(), (key, account) -> account.increaseBalance(transfer.getTransfer()));

            // Only for tests
            Thread.sleep(1000);
        }
    }
}
