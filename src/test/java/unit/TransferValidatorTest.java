package unit;

import org.account.exception.InvalidParameterException;
import org.account.exception.RequiredParameterException;
import org.account.model.dto.TransferDTO;
import org.account.validator.TransferValidatorImpl;
import org.account.validator.Validator;
import org.junit.Before;
import org.junit.Test;
import util.AccountTestHelper;

import java.math.BigDecimal;

public class TransferValidatorTest {

    private Validator<TransferDTO> transferValidator;

    @Before
    public void before() {
        transferValidator = new TransferValidatorImpl();
    }

    @Test(expected = RequiredParameterException.class)
    public void validate_noCurrency_throwsRequiredParameterException() throws InvalidParameterException, RequiredParameterException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setCurrency(null);
        transferValidator.validate(transferDTO);
    }

    @Test(expected = RequiredParameterException.class)
    public void validate_noTransferAmount_throwsRequiredParameterException() throws InvalidParameterException, RequiredParameterException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setTransferAmount(null);
        transferValidator.validate(transferDTO);
    }

    @Test(expected = InvalidParameterException.class)
    public void validate_invalidCurrencyCode_throwsInvalidParameterException() throws InvalidParameterException, RequiredParameterException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setCurrency("GBPXYZ");
        transferValidator.validate(transferDTO);
    }

    @Test(expected = InvalidParameterException.class)
    public void validate_negativeTransferAmount_throwsInvalidParameterException() throws InvalidParameterException, RequiredParameterException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setTransferAmount(BigDecimal.valueOf(-1));
        transferValidator.validate(transferDTO);
    }
}
