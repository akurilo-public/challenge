package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import com.db.awmd.challenge.repository.TransactionRepositoryInMemory;
import com.db.awmd.challenge.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionRepositoryInMemoryTest {

    @Autowired
    private TransactionRepositoryInMemory transactionRepository;

    @Autowired
    private AccountsRepositoryInMemory accountsRepository;

    @MockBean
    private NotificationService notificationService;

    @Test
    public void executeWithoutRaceCondition() throws Exception {
        Account account = new Account("1");
        account.setBalance(new BigDecimal(10));
        this.accountsRepository.createAccount(account);

        Account account2 = new Account("2");
        account2.setBalance(new BigDecimal(0));
        this.accountsRepository.createAccount(account2);

        Transaction transaction1 = Transaction.builder()
                .accountIdFrom("1")
                .accountIdTo("2")
                .transfer(BigDecimal.TEN)
                .build();

        Transaction transaction2 = Transaction.builder()
                .accountIdFrom("2")
                .accountIdTo("1")
                .transfer(BigDecimal.TEN)
                .build();

        Thread thread1 = new Thread() {
            public void run() {
                transactionRepository.transfer(transaction1);
            }
        };

        Thread thread2 = new Thread() {
            public void run() {
                transactionRepository.transfer(transaction2);
            }
        };

        thread1.start();
        thread2.start();
        Thread.sleep(5000);

        assertThat(accountsRepository.getAccount("1").getBalance().doubleValue()).isEqualTo(10);
        assertThat(accountsRepository.getAccount("2").getBalance().doubleValue()).isEqualTo(0);
    }
}


