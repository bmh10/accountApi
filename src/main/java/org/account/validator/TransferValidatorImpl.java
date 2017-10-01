package org.account.validator;

import org.account.exception.InvalidParameterException;
import org.account.exception.RequiredParameterException;
import org.account.model.dto.TransferDTO;

public class TransferValidatorImpl extends AbstractValidator<TransferDTO> {

    private static final String SRC_DST_SAME_ACCOUNT_ERR = "Source and destination accounts must not have same ID";

    public void validate(TransferDTO transferDTO) throws RequiredParameterException, InvalidParameterException {
        assertRequiredParam("transferAmount", transferDTO.getTransferAmount());
        assertRequiredParam("currency", transferDTO.getCurrency());
        assertValidCurrency(transferDTO.getCurrency());
        assertPositive(transferDTO.getTransferAmount());

        if (transferDTO.getSourceAccountId() == transferDTO.getDestinationAccountId()) {
            throw new InvalidParameterException(SRC_DST_SAME_ACCOUNT_ERR);
        }
    }
}
