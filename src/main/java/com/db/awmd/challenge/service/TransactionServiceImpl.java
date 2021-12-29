package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountsRepository accountsRepository;
    private final NotificationService notificationService;


    @Override
    @SneakyThrows
    public void executeTransaction(Transaction transaction) {
        accountsRepository.transfer(transaction);
        notificationService.notifyAboutTransfer(accountsRepository.getAccount(transaction.getAccountIdFrom()),
                String.format("Funds were sent in the amount of %s $", transaction.getTransfer()));
        notificationService.notifyAboutTransfer(accountsRepository.getAccount(transaction.getAccountIdTo()),
                String.format("Receipt to the account %s $", transaction.getTransfer()));

    }
}
