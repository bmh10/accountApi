package unit;

import org.account.model.dto.AccountDTO;
import org.junit.Before;
import org.junit.Test;
import org.account.exception.InvalidParameterException;
import org.account.exception.RequiredParameterException;
import org.account.validator.AccountValidatorImpl;
import org.account.validator.Validator;
import util.AccountTestHelper;

import java.math.BigDecimal;

public class AccountValidatorTest {

    private Validator<AccountDTO> accountValidator;

    @Before
    public void before() {
        accountValidator = new AccountValidatorImpl();
    }

    @Test(expected = RequiredParameterException.class)
    public void validate_noAccountHolderName_throwsRequiredParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO accountDTO = AccountTestHelper.createAccountDTO();
        accountDTO.setAccountHolderName(null);
        accountValidator.validate(accountDTO);
    }

    @Test(expected = RequiredParameterException.class)
    public void validate_noCurrency_throwsRequiredParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO accountDTO = AccountTestHelper.createAccountDTO();
        accountDTO.setCurrency(null);
        accountValidator.validate(accountDTO);
    }

    @Test(expected = RequiredParameterException.class)
    public void validate_noBalance_throwsRequiredParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO accountDTO = AccountTestHelper.createAccountDTO();
        accountDTO.setBalance(null);
        accountValidator.validate(accountDTO);
    }

    @Test(expected = InvalidParameterException.class)
    public void validate_invalidCurrencyCode_throwsInvalidParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO accountDTO = AccountTestHelper.createAccountDTO();
        accountDTO.setCurrency("GBPXYZ");
        accountValidator.validate(accountDTO);
    }

    @Test(expected = InvalidParameterException.class)
    public void validate_negativeBalance_throwsInvalidParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO accountDTO = AccountTestHelper.createAccountDTO();
        accountDTO.setBalance(BigDecimal.valueOf(-1));
        accountValidator.validate(accountDTO);
    }

}
