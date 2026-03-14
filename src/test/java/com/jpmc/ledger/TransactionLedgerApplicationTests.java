package com.jpmc.ledger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.ledger.dto.CreateAccountRequest;
import com.jpmc.ledger.dto.DepositRequest;
import com.jpmc.ledger.dto.TransferRequest;
import com.jpmc.ledger.entity.Account;
import com.jpmc.ledger.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionLedgerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String accountAId;
    private String accountBId;

    @BeforeEach
    void setup() throws Exception {
        CreateAccountRequest reqA = new CreateAccountRequest();
        reqA.setOwnerName("Alice");
        CreateAccountRequest reqB = new CreateAccountRequest();
        reqB.setOwnerName("Bob");

        Account a = accountRepository.save(toAccount(reqA));
        Account b = accountRepository.save(toAccount(reqB));
        a.setBalance(new BigDecimal("1000.00"));
        accountRepository.save(a);
        accountAId = a.getId();
        accountBId = b.getId();
    }

    private Account toAccount(CreateAccountRequest req) {
        Account acc = new Account();
        acc.setOwnerName(req.getOwnerName());
        return acc;
    }

    @Test
    void createAccount_success() throws Exception {
        CreateAccountRequest req = new CreateAccountRequest();
        req.setOwnerName("Charlie");
        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ownerName").value("Charlie"));
    }

    @Test
    void createAccount_missingName_fails() throws Exception {
        CreateAccountRequest req = new CreateAccountRequest();
        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deposit_success() throws Exception {
        DepositRequest req = new DepositRequest();
        req.setAmount(new BigDecimal("500.00"));
        mockMvc.perform(post("/api/v1/accounts/" + accountBId + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(500.00));
    }

    @Test
    void transfer_success() throws Exception {
        TransferRequest req = new TransferRequest();
        req.setFromAccountId(accountAId);
        req.setToAccountId(accountBId);
        req.setAmount(new BigDecimal("200.00"));
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.amount").value(200.00));
    }

    @Test
    void transfer_insufficientFunds_fails() throws Exception {
        TransferRequest req = new TransferRequest();
        req.setFromAccountId(accountAId);
        req.setToAccountId(accountBId);
        req.setAmount(new BigDecimal("99999.00"));
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    void getBalance_accountNotFound_fails() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/nonexistent-id/balance"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}