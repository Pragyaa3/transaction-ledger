package com.jpmc.ledger.service;

import com.jpmc.ledger.dto.CreateAccountRequest;
import com.jpmc.ledger.dto.DepositRequest;
import com.jpmc.ledger.dto.TransferRequest;
import com.jpmc.ledger.entity.Account;
import com.jpmc.ledger.entity.Transaction;
import com.jpmc.ledger.repository.AccountRepository;
import com.jpmc.ledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Account createAccount(CreateAccountRequest request) {
        Account account = new Account();
        account.setOwnerName(request.getOwnerName());
        return accountRepository.save(account);
    }

    public Account getBalance(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    }

    @Transactional
    public Account deposit(String accountId, DepositRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
        account.setBalance(account.getBalance().add(request.getAmount()));
        return accountRepository.save(account);
    }

    public Transaction transfer(TransferRequest request) {
        Account from = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction transaction = new Transaction();
        transaction.setFromAccountId(from.getId());
        transaction.setToAccountId(to.getId());
        transaction.setAmount(request.getAmount());
        transaction.setStatus("SUCCESS");

        return transactionRepository.save(transaction);
    }

    public Page<Transaction> getTransactionHistory(String accountId, Pageable pageable) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId, pageable);
    }
}