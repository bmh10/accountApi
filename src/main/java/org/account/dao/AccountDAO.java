package org.account.dao;

import org.account.exception.AccountNotFoundException;
import org.account.exception.InsufficientFundsException;
import org.account.model.Account;

import java.math.BigDecimal;
import java.util.Collection;

public interface AccountDAO {

    Collection<Account> getAllAccounts();

    Account getAccount(int id) throws AccountNotFoundException;

    Account createAccount(Account account);

    boolean accountWithdraw(int id, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException;

    boolean accountDeposit(int id, BigDecimal amount) throws AccountNotFoundException;
}
