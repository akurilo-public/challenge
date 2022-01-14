package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import com.db.awmd.challenge.repository.TransactionRepositoryInMemory;
import com.db.awmd.challenge.service.NotificationService;
import org.junit.After;
import org.junit.Before;
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

    @Before
    public void init() {
        Account account = new Account("1");
        account.setBalance(new BigDecimal(12_000));
        this.accountsRepository.createAccount(account);

        Account account2 = new Account("2");
        account2.setBalance(new BigDecimal(0));
        this.accountsRepository.createAccount(account2);
    }

    @After
    public void cleanUp() {
        accountsRepository.clearAccounts();
    }

    @Test
    public void concurrencyConsistencyTest() throws InterruptedException {
        accountsRepository.increaseBalance("2", new BigDecimal(12_000));

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                balanceUp();
            }
        }, "Thread-1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                balanceDown();
            }
        }, "Thread-2");

        t1.start();
        t2.start();

        // Wait for all threads to complete.
        t1.join();
        t2.join();

        assertThat(accountsRepository.getAccount("1").getBalance().doubleValue()).isEqualTo(12_000);
        assertThat(accountsRepository.getAccount("2").getBalance().doubleValue()).isEqualTo(12_000);
    }

    @Test
    public void concurrencyConsistencyTest2() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                balanceUp();
            }
        }, "Thread-1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                balanceUp();
            }
        }, "Thread-2");

        Thread t3 = new Thread(new Runnable() {
            public void run() {
                balanceUp();
            }
        }, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        // Wait for all threads to complete.
        t1.join();
        t2.join();
        t3.join();

        assertThat(accountsRepository.getAccount("1").getBalance().doubleValue()).isEqualTo(0);
        assertThat(accountsRepository.getAccount("2").getBalance().doubleValue()).isEqualTo(12_000);
    }

    @Test
    public void concurrencyConsistencyWithBalanceParallelAccessTest() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                balanceUp();
            }
        }, "Thread-1");

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                balanceUpWithoutTransaction();
            }
        }, "Thread-2");

        t1.start();
        t2.start();

        // Wait for all threads to complete.
        t1.join();
        t2.join();

        assertThat(accountsRepository.getAccount("2").getBalance().doubleValue()).isEqualTo(8_000);
    }

    /**
     * Transfer balance from the first account to the second account 4000 times.
     */
    private void balanceUp() {
        for (int j = 0; j < 4000; ++j) {
            Transaction transaction = Transaction.builder()
                    .accountIdFrom("1")
                    .accountIdTo("2")
                    .transfer(BigDecimal.ONE)
                    .build();
            transactionRepository.transfer(transaction);
        }
    }

    /**
     * Transfer balance from the second account to the first account 4000 times.
     */
    private void balanceDown() {
        for (int j = 0; j < 4000; ++j) {
            Transaction transaction = Transaction.builder()
                    .accountIdFrom("2")
                    .accountIdTo("1")
                    .transfer(BigDecimal.ONE)
                    .build();
            transactionRepository.transfer(transaction);
        }
    }

    /**
     * Change the balance of the second account 4000 times by the method from the account repository.
     */
    private void balanceUpWithoutTransaction() {
        for (int j = 0; j < 4000; ++j) {
            accountsRepository.increaseBalance("2", BigDecimal.ONE);
        }
    }
}


