package org.account.validator;

import org.account.exception.InvalidParameterException;
import org.account.exception.RequiredParameterException;
import org.account.model.dto.AccountDTO;

public class AccountValidatorImpl extends AbstractValidator<AccountDTO> {

    public void validate(AccountDTO accountDTO) throws RequiredParameterException, InvalidParameterException {
        assertRequiredParam("accountHolderName", accountDTO.getAccountHolderName());
        assertRequiredParam("balance", accountDTO.getBalance());
        assertRequiredParam("currency", accountDTO.getCurrency());
        assertValidCurrency(accountDTO.getCurrency());
        assertPositive(accountDTO.getBalance());
    }
}
