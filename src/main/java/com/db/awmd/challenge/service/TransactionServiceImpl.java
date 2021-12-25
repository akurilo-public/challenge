package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountsRepository accountsRepository;
    private final NotificationService notificationService;

    @Override
    public void executeTransaction(Transaction transaction) {
        synchronized (this) {
            isEnoughFunds(transaction.getAccountIdFrom(), transaction.getTransfer());
            decreaseBalance(transaction.getAccountIdFrom(), transaction.getTransfer());
            increaseBalance(transaction.getAccountIdTo(), transaction.getTransfer());
        }

        notificationService.notifyAboutTransfer(accountsRepository.getAccount(transaction.getAccountIdFrom()),
                String.format("Funds were sent in the amount of %s $", transaction.getTransfer()));
        notificationService.notifyAboutTransfer(accountsRepository.getAccount(transaction.getAccountIdTo()),
                String.format("Receipt to the account %s $", transaction.getTransfer()));

    }

    private void increaseBalance(String accountId, BigDecimal balance) {
        accountsRepository.getAccount(accountId).increaseBalance(balance);
    }

    private void decreaseBalance(String accountId, BigDecimal balance) {
        accountsRepository.getAccount(accountId).decreaseBalance(balance);
    }

    private void isEnoughFunds(String accountId, BigDecimal balance) {
        accountsRepository.getAccount(accountId).isEnoughFunds(balance);
    }
}
