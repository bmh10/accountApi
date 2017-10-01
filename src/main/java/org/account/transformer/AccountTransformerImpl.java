package org.account.transformer;

import org.account.model.Account;
import org.account.model.dto.AccountDTO;

import java.util.Currency;

public class AccountTransformerImpl implements AccountTransformer {

    public Account fromDTO(AccountDTO accountDTO) {
        Account o = new Account();
        if (accountDTO.getId() != null) {
            o.setId(accountDTO.getId());
        }
        o.setAccountHolderName(accountDTO.getAccountHolderName());
        o.setCurrency(Currency.getInstance(accountDTO.getCurrency()));
        o.setBalance(accountDTO.getBalance());
        return o;
    }

    public AccountDTO toDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountHolderName(account.getAccountHolderName());
        dto.setCurrency(account.getCurrency().getCurrencyCode());
        dto.setBalance(account.getBalance());
        dto.setCreatedDate(account.getCreatedDate());
        return dto;
    }

}
