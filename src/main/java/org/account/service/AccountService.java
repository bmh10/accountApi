package org.account.service;

import org.account.exception.*;
import org.account.model.dto.AccountDTO;
import org.account.model.dto.TransferDTO;

import java.util.Collection;

public interface AccountService {

    Collection<AccountDTO> getAllAccounts();

    AccountDTO getAccount(int id) throws AccountNotFoundException;

    AccountDTO createAccount(AccountDTO accountDTO)
            throws RequiredParameterException, InvalidParameterException;

    void transferMoney(TransferDTO transferDTO)
            throws AccountNotFoundException, InsufficientFundsException,
            InvalidParameterException, RequiredParameterException, CurrencyConversionException;
}
