package com.bankapp.controller;

import com.bankapp.exception.AccountNotFoundException;
import com.bankapp.exception.InsufficientFundsException;
import com.bankapp.model.Account;
import com.bankapp.model.AccountType;
import com.bankapp.model.Transaction;
import com.bankapp.model.TransactionType;
import com.bankapp.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void getAllAccounts_shouldReturn200() throws Exception {
        Account acc = new Account("ACC-001", "Alice", new BigDecimal("100.00"), AccountType.CHECKING);
        when(accountService.getAllAccounts()).thenReturn(List.of(acc));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerName").value("Alice"));
    }

    @Test
    void createAccount_shouldReturn201() throws Exception {
        Account acc = new Account("ACC-001", "Alice", new BigDecimal("100.00"), AccountType.CHECKING);
        when(accountService.createAccount(any())).thenReturn(acc);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ownerName\":\"Alice\",\"accountType\":\"CHECKING\",\"initialDeposit\":100.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerName").value("Alice"));
    }

    @Test
    void createAccount_withMissingName_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountType\":\"CHECKING\",\"initialDeposit\":100.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deposit_shouldReturn200() throws Exception {
        Account acc = new Account("ACC-001", "Alice", new BigDecimal("300.00"), AccountType.CHECKING);
        when(accountService.deposit(eq(1L), any())).thenReturn(acc);

        mockMvc.perform(post("/api/accounts/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":200.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(300.00));
    }

    @Test
    void withdraw_insufficientFunds_shouldReturn400() throws Exception {
        when(accountService.withdraw(eq(1L), any()))
                .thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(post("/api/accounts/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":9999.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transfer_shouldReturn204() throws Exception {
        mockMvc.perform(post("/api/accounts/1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetAccountNumber\":\"ACC-002\",\"amount\":50.00}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTransactions_notFound_shouldReturn404() throws Exception {
        when(accountService.getTransactions(99L))
                .thenThrow(new AccountNotFoundException("Account not found with id: 99"));

        mockMvc.perform(get("/api/accounts/99/transactions"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTransactions_shouldReturnList() throws Exception {
        Account acc = new Account("ACC-001", "Alice", new BigDecimal("100.00"), AccountType.CHECKING);
        Transaction tx = new Transaction(acc, TransactionType.DEPOSIT, new BigDecimal("100.00"), new BigDecimal("100.00"), "Deposit");
        when(accountService.getTransactions(1L)).thenReturn(List.of(tx));

        mockMvc.perform(get("/api/accounts/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"));
    }
}
