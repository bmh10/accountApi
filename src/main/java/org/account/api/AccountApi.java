package org.account.api;

import org.account.model.dto.AccountDTO;
import org.account.model.dto.TransferDTO;

import javax.ws.rs.core.Response;

public interface AccountApi {

    Response getAccounts();
    Response getAccount(int id);
    Response createAccount(AccountDTO accountDTO);
    Response transferMoney(TransferDTO transferDTO);
}
