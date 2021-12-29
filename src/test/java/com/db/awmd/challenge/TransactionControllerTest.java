package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @MockBean
  private NotificationService notificationService;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void successTransaction() throws Exception {
    Account accountFrom = new Account("1", BigDecimal.valueOf(100));
    Account accountTo = new Account("2", BigDecimal.valueOf(0));
    accountsService.createAccount(accountFrom);
    accountsService.createAccount(accountTo);

    this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountIdFrom\":\"1\",\"accountIdTo\":\"2\",\"transfer\":100}")).andExpect(status().isOk());

    assertThat(accountFrom.getBalance()).isEqualByComparingTo("0");
    assertThat(accountTo.getBalance()).isEqualByComparingTo("100");
  }

  @Test
  public void errorTransaction() throws Exception {
    Account accountFrom = new Account("1", BigDecimal.valueOf(0));
    Account accountTo = new Account("2", BigDecimal.valueOf(0));
    accountsService.createAccount(accountFrom);
    accountsService.createAccount(accountTo);


    assertThatThrownBy(()->    this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountIdFrom\":\"1\",\"accountIdTo\":\"2\",\"transfer\":100}")).andExpect(status().isOk()));
  }
}
