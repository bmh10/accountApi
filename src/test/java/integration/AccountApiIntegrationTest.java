package integration;

import org.account.exception.*;
import org.account.model.dto.AccountDTO;
import org.account.model.dto.TransferDTO;
import org.account.validator.TransferValidatorImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.account.api.AccountApi;
import org.account.api.AccountApiImpl;
import org.account.dao.AccountDAO;
import org.account.dao.AccountDAOImpl;
import org.account.service.AccountService;
import org.account.service.AccountServiceImpl;
import org.account.transformer.AccountTransformer;
import org.account.transformer.AccountTransformerImpl;
import org.account.validator.AccountValidatorImpl;
import org.account.validator.Validator;
import util.AccountTestHelper;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountApiIntegrationTest {

    private AccountApi api;

    @Before
    public void before() {
        Validator<AccountDTO> accountValidator = new AccountValidatorImpl();
        Validator<TransferDTO> transferValidator = new TransferValidatorImpl();
        AccountTransformer transformer = new AccountTransformerImpl();
        AccountDAO dao = new AccountDAOImpl();
        AccountService service = new AccountServiceImpl(accountValidator, transferValidator, transformer, dao);
        api = new AccountApiImpl(service);
    }

    @Test
    public void getAccounts_whenNoAccounts_returnsNothing() {
        Response response = api.getAccounts();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertTrue(((List) response.getEntity()).isEmpty());
    }

    @Test
    public void getAccounts_whenMultipleAccounts_returnsAll() {
        insertAccounts(10);
        Response response = api.getAccounts();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(10, ((List) response.getEntity()).size());
    }

    @Test
    public void getAccount_whenNotFound_givesNotFound() {
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), api.getAccount(1).getStatus());
    }

    @Test
    public void getAccount_whenFound_returnsAccount() {
        List<AccountDTO> insertedDTOs = insertAccounts(1);
        Response response = api.getAccount(1);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(insertedDTOs.get(0), response.getEntity());
    }

    @Test
    public void createAccount_withMissingAccountHolderName_givesBadRequest() {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setAccountHolderName(null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.createAccount(dto).getStatus());
    }

    @Test
    public void createAccount_withMissingCurrency_givesBadRequest() {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setCurrency(null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.createAccount(dto).getStatus());
    }

    @Test
    public void createAccount_withInvalidCurrency_givesBadRequest() {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setCurrency("GBPXYZ");
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.createAccount(dto).getStatus());
    }

    @Test
    public void createAccount_withMissingBalance_givesBadRequest() {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setBalance(null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.createAccount(dto).getStatus());
    }

    @Test
    public void createAccount_withNegativeBalance_givesBadRequest() {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        dto.setBalance(BigDecimal.valueOf(-1));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.createAccount(dto).getStatus());
    }

    @Test
    public void createAccount_withValidFields_returnsCreatedAccount() {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        Response response = api.createAccount(dto);
        AccountDTO responseDto = (AccountDTO)response.getEntity();
        dto.setId(responseDto.getId());
        dto.setCreatedDate(responseDto.getCreatedDate());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(dto, response.getEntity());
    }

    @Test
    public void transferMoney_whenMissingTransferAmount_givesBadRequest() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setTransferAmount(null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),  api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenMissingCurrency_givesBadRequest() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setCurrency(null);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenNegativeTransferAmount_givesBadRequest() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setTransferAmount(BigDecimal.valueOf(-1));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenInvalidCurrencyCode_givesBadRequest() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setCurrency("GBPXYZ");
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenSourceAndDestinationAccountsIdsSame_givesBadRequest() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        transferDTO.setDestinationAccountId(transferDTO.getSourceAccountId());
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenSourceAccountNotFound_givesBadRequest() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenDestinationAccountNotFound_givesBadRequest() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.createAccount(srcAccountDTO).getStatus());
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenSourceAccountCurrencyNotSameAsTransferCurrency_givesNotAcceptable() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO(1);
        srcAccountDTO.setCurrency("USD");
        AccountDTO dstAccountDTO = AccountTestHelper.createAccountDTO(2);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.createAccount(srcAccountDTO).getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.createAccount(dstAccountDTO).getStatus());
        Assert.assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenDestinationAccountCurrencyNotSameAsTransferCurrency_givesNotAcceptable() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO(1);
        AccountDTO dstAccountDTO = AccountTestHelper.createAccountDTO(2);
        dstAccountDTO.setCurrency("USD");
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.createAccount(srcAccountDTO).getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.createAccount(dstAccountDTO).getStatus());
        Assert.assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenSourceAccountHasInsufficientFundsForTransfer_givesNotAcceptable() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO(1);
        srcAccountDTO.setBalance(BigDecimal.ZERO);
        AccountDTO dstAccountDTO = AccountTestHelper.createAccountDTO(2);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.createAccount(srcAccountDTO).getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.createAccount(dstAccountDTO).getStatus());
        Assert.assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), api.transferMoney(transferDTO).getStatus());
    }

    @Test
    public void transferMoney_whenSuccessful_accountBalancedUpdatedCorrectly() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        AccountDTO srcAccountDTO = AccountTestHelper.createAccountDTO(1);
        AccountDTO dstAccountDTO = AccountTestHelper.createAccountDTO(2);
        BigDecimal srcAccountExpectedFinalBalance = srcAccountDTO.getBalance().subtract(transferDTO.getTransferAmount());
        BigDecimal dstAccountExpectedFinalBalance = dstAccountDTO.getBalance().add(transferDTO.getTransferAmount());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.createAccount(srcAccountDTO).getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.createAccount(dstAccountDTO).getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(), api.transferMoney(transferDTO).getStatus());

        Assert.assertEquals(srcAccountExpectedFinalBalance, ((AccountDTO)api.getAccount(1).getEntity()).getBalance());
        Assert.assertEquals(dstAccountExpectedFinalBalance, ((AccountDTO)api.getAccount(2).getEntity()).getBalance());
    }

    private List<AccountDTO> insertAccounts(int n) {
        List<AccountDTO> createdAccounts = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            AccountDTO dto = AccountTestHelper.createAccountDTO(i);
            Response response = api.createAccount(dto);
            createdAccounts.add((AccountDTO)response.getEntity());
        }

        return createdAccounts;
    }
}
