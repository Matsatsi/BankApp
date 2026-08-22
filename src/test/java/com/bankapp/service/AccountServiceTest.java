package com.bankapp.service;

import com.bankapp.dto.AmountRequest;
import com.bankapp.dto.CreateAccountRequest;
import com.bankapp.dto.TransferRequest;
import com.bankapp.exception.AccountNotFoundException;
import com.bankapp.exception.InsufficientFundsException;
import com.bankapp.model.Account;
import com.bankapp.model.AccountType;
import com.bankapp.model.Transaction;
import com.bankapp.model.TransactionType;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    private Account existingAccount;

    @BeforeEach
    void setUp() {
        existingAccount = new Account("ACC-001", "Alice", new BigDecimal("500.00"), AccountType.CHECKING);
    }

    @Test
    void createAccount_shouldSaveAccountAndTransaction() {
        CreateAccountRequest req = new CreateAccountRequest();
        req.setOwnerName("Bob");
        req.setAccountType(AccountType.SAVINGS);
        req.setInitialDeposit(new BigDecimal("100.00"));

        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Account created = accountService.createAccount(req);

        assertThat(created.getOwnerName()).isEqualTo("Bob");
        assertThat(created.getBalance()).isEqualByComparingTo("100.00");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deposit_shouldIncreaseBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AmountRequest req = new AmountRequest();
        req.setAmount(new BigDecimal("200.00"));

        Account updated = accountService.deposit(1L, req);

        assertThat(updated.getBalance()).isEqualByComparingTo("700.00");
    }

    @Test
    void withdraw_shouldDecreaseBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AmountRequest req = new AmountRequest();
        req.setAmount(new BigDecimal("100.00"));

        Account updated = accountService.withdraw(1L, req);

        assertThat(updated.getBalance()).isEqualByComparingTo("400.00");
    }

    @Test
    void withdraw_shouldThrowWhenInsufficientFunds() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));

        AmountRequest req = new AmountRequest();
        req.setAmount(new BigDecimal("9999.00"));

        assertThatThrownBy(() -> accountService.withdraw(1L, req))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void getAccount_shouldThrowWhenNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount(99L))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void transfer_shouldMoveMoneyBetweenAccounts() {
        Account target = new Account("ACC-002", "Bob", new BigDecimal("0.00"), AccountType.CHECKING);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.findByAccountNumber("ACC-002")).thenReturn(Optional.of(target));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferRequest req = new TransferRequest();
        req.setTargetAccountNumber("ACC-002");
        req.setAmount(new BigDecimal("200.00"));

        accountService.transfer(1L, req);

        assertThat(existingAccount.getBalance()).isEqualByComparingTo("300.00");
        assertThat(target.getBalance()).isEqualByComparingTo("200.00");
    }
}
