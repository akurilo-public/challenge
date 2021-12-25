package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class Transaction {

    @NotNull
    private final String accountIdFrom;

    @NotNull
    private final String accountIdTo;

    @NotNull
    private BigDecimal transfer;

    @JsonCreator
    public Transaction(@JsonProperty("accountIdFrom") String accountIdFrom,
                       @JsonProperty("accountIdTo") String accountIdTo,
                       @JsonProperty("transfer") BigDecimal transfer) {
        this.accountIdFrom = accountIdFrom;
        this.accountIdTo = accountIdTo;
        this.transfer = transfer;
    }
}

