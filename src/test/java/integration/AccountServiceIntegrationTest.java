package integration;

import org.account.dao.AccountDAO;
import org.account.dao.AccountDAOImpl;
import org.account.exception.*;
import org.account.model.dto.AccountDTO;
import org.account.model.dto.TransferDTO;
import org.account.service.AccountService;
import org.account.service.AccountServiceImpl;
import org.account.transformer.AccountTransformer;
import org.account.transformer.AccountTransformerImpl;
import org.account.validator.AccountValidatorImpl;
import org.account.validator.TransferValidatorImpl;
import org.account.validator.Validator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.AccountTestHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountServiceIntegrationTest {

    private AccountService service;

    @Before
    public void before() {
        Validator<AccountDTO> accountValidator = new AccountValidatorImpl();
        Validator<TransferDTO> transferValidator = new TransferValidatorImpl();
        AccountTransformer transformer = new AccountTransformerImpl();
        AccountDAO dao = new AccountDAOImpl();
        service = new AccountServiceImpl(accountValidator, transferValidator, transformer, dao);
    }

    @Test
    public void getAllAccounts_whenNoAccounts_returnsNothing() {
        Assert.assertTrue(service.getAllAccounts().isEmpty());
    }

    @Test
    public void getAllAccounts_whenMultipleAccounts_returnsAll() {
        insertAccounts(10);
        Assert.assertEquals(10, service.getAllAccounts().size());
    }

    @Test(expected = AccountNotFoundException.class)
    public void getAccount_whenNotFound_throwsAccountNotFoundException() throws AccountNotFoundException {
        service.getAccount(1);
    }

    @Test
    public void getAccount_whenFound_returnsAccount() throws AccountNotFoundException {
        List<AccountDTO> insertedDTOs = insertAccounts(1);
        AccountDTO accountDto = service.getAccount(1);
        Assert.assertEquals(insertedDTOs.get(0), accountDto);
    }

    @Test(expected = RequiredParameterException.class)
    public void createAccount_withMissingAccountHolderName_throwsRequiredParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setAccountHolderName(null);
        service.createAccount(dto);
    }

    @Test(expected = RequiredParameterException.class)
    public void createAccount_withMissingCurrency_throwsRequiredParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setCurrency(null);
        service.createAccount(dto);
    }

    @Test(expected = InvalidParameterException.class)
    public void createAccount_withInvalidCurrency_throwsInvalidParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setCurrency("GBPXYZ");
        service.createAccount(dto);
    }

    @Test(expected = RequiredParameterException.class)
    public void createAccount_withMissingBalance_throwsRequiredParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setBalance(null);
        service.createAccount(dto);
    }

    @Test(expected = InvalidParameterException.class)
    public void createAccount_withNegativeBalance_throwsInvalidParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setBalance(BigDecimal.valueOf(-1));
        service.createAccount(dto);
    }

    @Test
    public void createAccount_withValidFields_returnsCreatedAccount() throws InvalidParameterException, RequiredParameterException {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        AccountDTO createdAccountDto = service.createAccount(dto);

        dto.setId(createdAccountDto.getId());
        dto.setCreatedDate(createdAccountDto.getCreatedDate());
        Assert.assertEquals(dto, createdAccountDto);
    }

    @Test(expected = RequiredParameterException.class)
    public void transferMoney_whenMissingTransferAmount_throwsRequiredParameterException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setTransferAmount(null);
        service.transferMoney(transferDTO);
    }

    @Test(expected = RequiredParameterException.class)
    public void transferMoney_whenMissingCurrency_throwsRequiredParameterException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setCurrency(null);
        service.transferMoney(transferDTO);
    }

    @Test(expected = InvalidParameterException.class)
    public void transferMoney_whenNegativeTransferAmount_throwsInvalidParameterException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setTransferAmount(BigDecimal.valueOf(-1));
        service.transferMoney(transferDTO);
    }

    @Test(expected = InvalidParameterException.class)
    public void transferMoney_whenInvalidCurrencyCode_throwsInvalidParameterException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setCurrency("GBPXYZ");
        service.transferMoney(transferDTO);
    }

    @Test(expected = InvalidParameterException.class)
    public void transferMoney_whenSourceAndDestinationAccountsIdsSame_throwsInvalidParameterException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setDestinationAccountId(transferDTO.getSourceAccountId());
        service.transferMoney(transferDTO);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferMoney_whenSourceAccountNotFound_throwsAccountNotFoundException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        service.transferMoney(transferDTO);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferMoney_whenDestinationAccountNotFound_throwsAccountNotFoundException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO();
        service.createAccount(srcAccountDTO);
        service.transferMoney(transferDTO);
    }

    @Test(expected = CurrencyConversionException.class)
    public void transferMoney_whenSourceAccountCurrencyNotSameAsTransferCurrency_throwsCurrencyConversionException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO(1);
        srcAccountDTO.setCurrency("USD");
        AccountDTO dstAccountDTO = AccountTestHelper.createAccountDTO(2);
        service.createAccount(srcAccountDTO);
        service.createAccount(dstAccountDTO);
        service.transferMoney(transferDTO);
    }

    @Test(expected = CurrencyConversionException.class)
    public void transferMoney_whenDestinationAccountCurrencyNotSameAsTransferCurrency_throwsCurrencyConversionException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO(1);
        AccountDTO dstAccountDTO = AccountTestHelper.createAccountDTO(2);
        dstAccountDTO.setCurrency("USD");
        service.createAccount(srcAccountDTO);
        service.createAccount(dstAccountDTO);
        service.transferMoney(transferDTO);
    }

    @Test(expected = InsufficientFundsException.class)
    public void transferMoney_whenSourceAccountHasInsufficientFundsForTransfer_throwsInsufficientFundsException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO(1);
        srcAccountDTO.setBalance(BigDecimal.ZERO);
        AccountDTO dstAccountDTO = AccountTestHelper.createAccountDTO(2);
        service.createAccount(srcAccountDTO);
        service.createAccount(dstAccountDTO);
        service.transferMoney(transferDTO);
    }

    @Test
    public void transferMoney_whenSuccessful_accountBalancedUpdatedCorrectly() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO(1);
        AccountDTO dstAccountDTO = AccountTestHelper.createAccountDTO(2);
        BigDecimal srcAccountExpectedFinalBalance = srcAccountDTO.getBalance().subtract(transferDTO.getTransferAmount());
        BigDecimal dstAccountExpectedFinalBalance = dstAccountDTO.getBalance().add(transferDTO.getTransferAmount());
        service.createAccount(srcAccountDTO);
        service.createAccount(dstAccountDTO);
        service.transferMoney(transferDTO);

        Assert.assertEquals(srcAccountExpectedFinalBalance, service.getAccount(1).getBalance());
        Assert.assertEquals(dstAccountExpectedFinalBalance, service.getAccount(2).getBalance());
    }

    private List<AccountDTO> insertAccounts(int n) {
        List<AccountDTO> createdAccounts = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            AccountDTO dto = AccountTestHelper.createAccountDTO(i);
            try {
                createdAccounts.add(service.createAccount(dto));
            }
            catch (Exception e) {
                Assert.fail();
            }
        }

        return createdAccounts;
    }
}
