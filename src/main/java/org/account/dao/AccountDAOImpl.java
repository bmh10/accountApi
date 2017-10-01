package org.account.dao;

import org.account.exception.AccountNotFoundException;
import org.account.exception.InsufficientFundsException;
import org.account.model.Account;
import org.account.model.dto.TransferDTO;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountDAOImpl implements AccountDAO {

    private static final String ACCOUNT_NOT_FOUND_ERR = "Account with ID %s not found";
    private static final String INSUFFICIENT_FUNDS_ERR = "Account with ID %s has insufficient funds to perform withdrawal";
    private final AtomicInteger atomicId = new AtomicInteger(0);
    private final Map<Integer, Account> accountMap = new HashMap<Integer, Account>();

    public Collection<Account> getAllAccounts() {
        return accountMap.values();
    }

    public Account getAccount(int id) throws AccountNotFoundException {
        checkAccountExists(id);
        return accountMap.get(id);
    }

    public Account createAccount(Account account) {
        account.setId(atomicId.incrementAndGet());
        accountMap.put(account.getId(), account);
        return account;
    }

    public boolean accountWithdraw(int id, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException {
        checkAccountExists(id);
        Account account = accountMap.get(id);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(String.format(INSUFFICIENT_FUNDS_ERR, id));
        }

        account.setBalance(account.getBalance().subtract(amount));
        return true;
    }

    public boolean accountDeposit(int id, BigDecimal amount) throws AccountNotFoundException {
        checkAccountExists(id);
        Account account = accountMap.get(id);
        account.setBalance(account.getBalance().add(amount));
        return true;
    }

    private void checkAccountExists(int id) throws AccountNotFoundException {
        if (!accountMap.containsKey(id)) {
            throw new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND_ERR, id));
        }
    }
}
