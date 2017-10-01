package org.account.transformer;

import org.account.model.Account;
import org.account.model.dto.AccountDTO;

public interface AccountTransformer {

    Account fromDTO(AccountDTO accountDTO);
    AccountDTO toDTO(Account account);
}
