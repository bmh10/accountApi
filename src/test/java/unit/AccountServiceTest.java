package unit;

import org.account.exception.*;
import org.account.model.dto.AccountDTO;
import org.account.model.dto.TransferDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.account.dao.AccountDAO;
import org.account.model.Account;
import org.account.service.AccountService;
import org.account.service.AccountServiceImpl;
import org.account.transformer.AccountTransformerImpl;
import org.account.validator.Validator;
import util.AccountTestHelper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Mock
    private Validator<AccountDTO> mockAccountValidator;
    @Mock
    private Validator<TransferDTO> mockTransferValidator;
    @Mock
    private AccountTransformerImpl AccountTransformer;
    @Mock
    private AccountDAO accountDAO;

    private AccountService accountService;

    @Before
    public void before() {
        accountService = new AccountServiceImpl(mockAccountValidator, mockTransferValidator, AccountTransformer, accountDAO);
    }

    @Test
    public void getAllAccounts_whenAccountsExist_returnsAccountDTOs() {
        List<Account> accounts = AccountTestHelper.createAccounts(5);
        List<AccountDTO> accountDtos = AccountTestHelper.createAccountDTOs(5);
        when(accountDAO.getAllAccounts()).thenReturn(accounts);
        for (int i = 0; i < accounts.size(); i++) {
            when(AccountTransformer.toDTO(accounts.get(i))).thenReturn(accountDtos.get(i));
        }

        Collection<AccountDTO> returnedDtos = accountService.getAllAccounts();

        for (AccountDTO returnedDto : returnedDtos) {
            Assert.assertTrue(accountDtos.contains(returnedDto));
        }
        verify(accountDAO).getAllAccounts();
        verify(AccountTransformer, times(accounts.size())).toDTO(any(Account.class));
    }

    @Test(expected = AccountNotFoundException.class)
    public void getAccount_whenAccountNotFound_throwsAccountNotFoundException() throws AccountNotFoundException {
        when(accountDAO.getAccount(1)).thenThrow(new AccountNotFoundException("Account not found"));
        accountService.getAccount(1);
    }

    @Test
    public void getAccount_whenAccountExists_returnsAccountDTO() throws AccountNotFoundException {
        Account account = AccountTestHelper.createAccount();
        AccountDTO accountDto = AccountTestHelper.createAccountDTO();
        when(accountDAO.getAccount(1)).thenReturn(account);
        when(AccountTransformer.toDTO(account)).thenReturn(accountDto);

        AccountDTO returnedDto = accountService.getAccount(1);

        Assert.assertTrue(accountDto.equals(returnedDto));

        verify(accountDAO).getAccount(1);
        verify(AccountTransformer).toDTO(account);
    }

    @Test(expected = InvalidParameterException.class)
    public void createAccount_whenInvalidParameter_throwsInvalidParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO accountDto = AccountTestHelper.createAccountDTO();
        doThrow(new InvalidParameterException("Invalid parameter")).when(mockAccountValidator).validate(accountDto);
        accountService.createAccount(accountDto);
        verify(mockAccountValidator).validate(accountDto);
    }

    @Test(expected = RequiredParameterException.class)
    public void createAccount_whenRequiredParameterNotPresent_throwsRequiredParameterException() throws InvalidParameterException, RequiredParameterException {
        AccountDTO accountDto = AccountTestHelper.createAccountDTO();
        doThrow(new RequiredParameterException("Required parameter")).when(mockAccountValidator).validate(accountDto);
        accountService.createAccount(accountDto);
        verify(mockAccountValidator).validate(accountDto);
    }

    @Test
    public void createAccount_whenSuccessful_createdDateIsSet() throws InvalidParameterException, RequiredParameterException {
        AccountDTO accountDto = AccountTestHelper.createAccountDTO();
        Account account = AccountTestHelper.createAccount();
        when(AccountTransformer.fromDTO(accountDto)).thenReturn(account);
        when(accountDAO.createAccount(account)).thenReturn(account);
        when(AccountTransformer.toDTO(account)).thenCallRealMethod();

        AccountDTO createdAccountDto = accountService.createAccount(accountDto);

        Assert.assertTrue(createdAccountDto.getCreatedDate() != null);
        accountDto.setCreatedDate(createdAccountDto.getCreatedDate());
        Assert.assertEquals(accountDto, createdAccountDto);

        verify(mockAccountValidator).validate(accountDto);
        verify(AccountTransformer).fromDTO(accountDto);
        verify(accountDAO).createAccount(account);
        verify(AccountTransformer).toDTO(account);
    }

    @Test(expected = InvalidParameterException.class)
    public void transferMoney_whenInvalidParameter_throwsInvalidParameterException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        doThrow(new InvalidParameterException("Invalid parameter")).when(mockTransferValidator).validate(transferDTO);
        accountService.transferMoney(transferDTO);
    }

    @Test(expected = RequiredParameterException.class)
    public void transferMoney_whenRequiredParameterExceptionMissing_throwsRequiredParameterException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        doThrow(new RequiredParameterException("Invalid parameter")).when(mockTransferValidator).validate(transferDTO);
        accountService.transferMoney(transferDTO);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferMoney_whenSourceAccountNotFound_throwsAccountNotFoundException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        when(accountDAO.getAccount(transferDTO.getSourceAccountId())).thenThrow(new AccountNotFoundException("Account not found"));
        accountService.transferMoney(transferDTO);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferMoney_whenDestinationAccountNotFound_throwsAccountNotFoundException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        when(accountDAO.getAccount(transferDTO.getDestinationAccountId())).thenThrow(new AccountNotFoundException("Account not found"));
        accountService.transferMoney(transferDTO);
    }

    @Test(expected = CurrencyConversionException.class)
    public void transferMoney_whenSourceAccountCurrencyNotSameAsTransferCurrency_throwsCurrencyConversionException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        Account srcAccount = AccountTestHelper.createAccount(1);
        srcAccount.setCurrency(Currency.getInstance("USD"));
        Account dstAccount = AccountTestHelper.createAccount(2);
        when(accountDAO.getAccount(transferDTO.getSourceAccountId())).thenReturn(srcAccount);
        when(accountDAO.getAccount(transferDTO.getDestinationAccountId())).thenReturn(dstAccount);
        accountService.transferMoney(transferDTO);
    }

    @Test(expected = CurrencyConversionException.class)
    public void transferMoney_whenDestinationAccountCurrencyNotSameAsTransferCurrency_throwsCurrencyConversionException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        Account srcAccount = AccountTestHelper.createAccount(1);
        Account dstAccount = AccountTestHelper.createAccount(2);
        dstAccount.setCurrency(Currency.getInstance("USD"));
        when(accountDAO.getAccount(transferDTO.getSourceAccountId())).thenReturn(srcAccount);
        when(accountDAO.getAccount(transferDTO.getDestinationAccountId())).thenReturn(dstAccount);
        accountService.transferMoney(transferDTO);
    }

    @Test(expected = InsufficientFundsException.class)
    public void transferMoney_whenSourceAccountHasInsufficientFundsForTransfer_throwsInsufficientFundsException() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        Account srcAccount = AccountTestHelper.createAccount(1);
        srcAccount.setBalance(BigDecimal.ZERO);
        Account dstAccount = AccountTestHelper.createAccount(2);
        when(accountDAO.getAccount(transferDTO.getSourceAccountId())).thenReturn(srcAccount);
        when(accountDAO.getAccount(transferDTO.getDestinationAccountId())).thenReturn(dstAccount);
        accountService.transferMoney(transferDTO);
    }

    @Test
    public void transferMoney_whenSuccessful_accountBalancedUpdatedCorrectly() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        Account srcAccount = AccountTestHelper.createAccount(1);
        Account dstAccount = AccountTestHelper.createAccount(2);
        when(accountDAO.getAccount(transferDTO.getSourceAccountId())).thenReturn(srcAccount);
        when(accountDAO.getAccount(transferDTO.getDestinationAccountId())).thenReturn(dstAccount);
        accountService.transferMoney(transferDTO);

        verify(accountDAO).accountWithdraw(transferDTO.getSourceAccountId(), transferDTO.getTransferAmount());
        verify(accountDAO).accountDeposit(transferDTO.getDestinationAccountId(), transferDTO.getTransferAmount());
    }

    @Test
    public void transferMoney_whenWithdrawFails_rollsBackDeposit() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        Account srcAccount = AccountTestHelper.createAccount(1);
        Account dstAccount = AccountTestHelper.createAccount(2);
        when(accountDAO.getAccount(transferDTO.getSourceAccountId())).thenReturn(srcAccount);
        when(accountDAO.getAccount(transferDTO.getDestinationAccountId())).thenReturn(dstAccount);

        when(accountDAO.accountWithdraw(srcAccount.getId(), transferDTO.getTransferAmount())).thenReturn(false);
        when(accountDAO.accountDeposit(dstAccount.getId(), transferDTO.getTransferAmount())).thenReturn(true);

        accountService.transferMoney(transferDTO);

        verify(accountDAO).accountWithdraw(transferDTO.getSourceAccountId(), transferDTO.getTransferAmount());
        verify(accountDAO).accountDeposit(transferDTO.getDestinationAccountId(), transferDTO.getTransferAmount());

        // Rollback call
        verify(accountDAO).accountWithdraw(transferDTO.getDestinationAccountId(), transferDTO.getTransferAmount());
    }

    @Test
    public void transferMoney_whenDepositThrowsException_rollsBackWithdraw() throws InvalidParameterException, AccountNotFoundException, RequiredParameterException, CurrencyConversionException, InsufficientFundsException {
        TransferDTO transferDTO = AccountTestHelper.createTransferDTO();
        Account srcAccount = AccountTestHelper.createAccount(1);
        Account dstAccount = AccountTestHelper.createAccount(2);
        when(accountDAO.getAccount(transferDTO.getSourceAccountId())).thenReturn(srcAccount);
        when(accountDAO.getAccount(transferDTO.getDestinationAccountId())).thenReturn(dstAccount);

        when(accountDAO.accountWithdraw(srcAccount.getId(), transferDTO.getTransferAmount())).thenReturn(true);
        when(accountDAO.accountDeposit(dstAccount.getId(), transferDTO.getTransferAmount())).thenThrow(new RuntimeException("Database connection lost"));

        accountService.transferMoney(transferDTO);

        verify(accountDAO).accountWithdraw(transferDTO.getSourceAccountId(), transferDTO.getTransferAmount());
        verify(accountDAO).accountDeposit(transferDTO.getDestinationAccountId(), transferDTO.getTransferAmount());

        // Rollback call
        verify(accountDAO).accountDeposit(transferDTO.getSourceAccountId(), transferDTO.getTransferAmount());
    }
}