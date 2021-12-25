package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotEmpty;

@Slf4j
@Data
public class Account {

    @NotNull
    @NotEmpty
    private final String accountId;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal balance;

    public Account(String accountId) {
        this.accountId = accountId;
        this.balance = BigDecimal.ZERO;
    }

    @JsonCreator
    public Account(@JsonProperty("accountId") String accountId,
                   @JsonProperty("balance") BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public void increaseBalance(BigDecimal balance) {
        this.balance = this.balance.add(balance);
    }

    public void decreaseBalance(BigDecimal balance) {
        this.balance = this.balance.subtract(balance);
    }

    public void isEnoughFunds(BigDecimal transfer) {
        if (this.balance.compareTo(transfer) == -1) {
            log.error("Insufficient funds to transfer (accountId={}, balance={}, transfer={})", this.balance, transfer);
            throw new RuntimeException(String.format("Insufficient funds in the account id=s%", this.accountId));
        }
    }
}
