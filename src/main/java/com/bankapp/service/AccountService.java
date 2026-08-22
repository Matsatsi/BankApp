package com.bankapp.service;

import com.bankapp.dto.AmountRequest;
import com.bankapp.dto.CreateAccountRequest;
import com.bankapp.dto.TransferRequest;
import com.bankapp.exception.AccountNotFoundException;
import com.bankapp.exception.InsufficientFundsException;
import com.bankapp.model.Account;
import com.bankapp.model.Transaction;
import com.bankapp.model.TransactionType;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(CreateAccountRequest request) {
        String accountNumber = generateAccountNumber();
        Account account = new Account(accountNumber, request.getOwnerName(),
                request.getInitialDeposit(), request.getAccountType());
        account = accountRepository.save(account);
        if (request.getInitialDeposit().compareTo(BigDecimal.ZERO) > 0) {
            Transaction tx = new Transaction(account, TransactionType.DEPOSIT,
                    request.getInitialDeposit(), account.getBalance(), "Initial deposit");
            transactionRepository.save(tx);
        }
        return account;
    }

    @Transactional(readOnly = true)
    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account deposit(Long id, AmountRequest request) {
        Account account = getAccount(id);
        account.setBalance(account.getBalance().add(request.getAmount()));
        account = accountRepository.save(account);
        transactionRepository.save(new Transaction(account, TransactionType.DEPOSIT,
                request.getAmount(), account.getBalance(),
                request.getDescription() != null ? request.getDescription() : "Deposit"));
        return account;
    }

    public Account withdraw(Long id, AmountRequest request) {
        Account account = getAccount(id);
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Available: " + account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        account = accountRepository.save(account);
        transactionRepository.save(new Transaction(account, TransactionType.WITHDRAWAL,
                request.getAmount(), account.getBalance(),
                request.getDescription() != null ? request.getDescription() : "Withdrawal"));
        return account;
    }

    public void transfer(Long fromId, TransferRequest request) {
        Account from = getAccount(fromId);
        Account to = accountRepository.findByAccountNumber(request.getTargetAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Target account not found: " + request.getTargetAccountNumber()));
        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Available: " + from.getBalance());
        }
        String desc = request.getDescription() != null ? request.getDescription() : "Transfer";
        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);
        transactionRepository.save(new Transaction(from, TransactionType.TRANSFER,
                request.getAmount(), from.getBalance(), desc + " to " + to.getAccountNumber()));
        transactionRepository.save(new Transaction(to, TransactionType.TRANSFER,
                request.getAmount(), to.getBalance(), desc + " from " + from.getAccountNumber()));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(Long accountId) {
        getAccount(accountId); // validate account exists
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "ACC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
