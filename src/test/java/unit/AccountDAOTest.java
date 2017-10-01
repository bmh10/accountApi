package unit;

import org.account.dao.AccountDAO;
import org.account.dao.AccountDAOImpl;
import org.account.exception.AccountNotFoundException;
import org.account.exception.InsufficientFundsException;
import org.account.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import util.AccountTestHelper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class AccountDAOTest {

    private AccountDAO accountDAO;

    @Before
    public void before() {
        accountDAO = new AccountDAOImpl();
    }

    @Test
    public void getAllAccounts_whenNoAccounts_returnsEmptyCollection() {
        Assert.assertTrue(accountDAO.getAllAccounts().isEmpty());
    }

    @Test
    public void getAllAccounts_whenAccountsExist_returnsAllAccounts() {
        List<Account> accounts = AccountTestHelper.createAccounts(10);
        accounts.forEach(accountDAO::createAccount);
        Collection<Account> returnedAccounts = accountDAO.getAllAccounts();
        Assert.assertEquals(accounts.size(), returnedAccounts.size());
        Assert.assertTrue(returnedAccounts.containsAll(accounts));
    }

    @Test (expected = AccountNotFoundException.class)
    public void getAccount_whenAccountNotFound_throwsAccountNotFoundException() throws AccountNotFoundException {
        accountDAO.getAccount(1);
    }

    @Test
    public void getAccount_whenAccountFound_returnsAccount() throws AccountNotFoundException {
        Account account = AccountTestHelper.createAccount();
        accountDAO.createAccount(account);
        Account returnedAccount = accountDAO.getAccount(account.getId());
        Assert.assertEquals(account, returnedAccount);
    }

    @Test
    public void createAccount_whenCreateSingleAccount_persistsAccount() throws AccountNotFoundException {
        Account account = AccountTestHelper.createAccount();
        Account createdAccount = accountDAO.createAccount(account);
        Assert.assertEquals(createdAccount, accountDAO.getAccount(account.getId()));
    }

    @Test
    public void createAccount_whenCreateSingleAccount_assignsAccountId() {
        Account account = AccountTestHelper.createAccount();
        account.setId(-1);
        Account createdAccount = accountDAO.createAccount(account);
        Assert.assertEquals(1, createdAccount.getId());
    }

    @Test
    public void createAccount_whenCreateManyAccounts_assignsUniqueAccountIds() {
        List<Account> accounts = AccountTestHelper.createAccounts(10);
        Set<Integer> ids = accounts.stream()
                                 .map(Account -> accountDAO.createAccount(Account).getId())
                                 .collect(Collectors.toSet());

        Assert.assertEquals(accounts.size(), ids.size());
    }

    @Test (expected = AccountNotFoundException.class)
    public void accountWithdraw_whenAccountNotFound_throwsAccountNotFoundException() throws AccountNotFoundException, InsufficientFundsException {
        accountDAO.accountWithdraw(1, BigDecimal.ONE);
    }

    @Test (expected = InsufficientFundsException.class)
    public void accountWithdraw_whenInsufficientFunds_throwsInsufficientFundsException() throws AccountNotFoundException, InsufficientFundsException {
        Account account = AccountTestHelper.createAccount();
        Account createdAccount = accountDAO.createAccount(account);
        accountDAO.accountWithdraw(createdAccount.getId(), createdAccount.getBalance().add(BigDecimal.ONE));
    }

    @Test
    public void accountWithdraw_whenSuccessful_subtractsAmountFromAccountBalance() throws AccountNotFoundException, InsufficientFundsException {
        Account account = AccountTestHelper.createAccount();
        Account createdAccount = accountDAO.createAccount(account);
        BigDecimal withdrawAmount = BigDecimal.TEN;
        BigDecimal initialBalance = createdAccount.getBalance();
        BigDecimal expectedFinalBalance = initialBalance.subtract(withdrawAmount);

        accountDAO.accountWithdraw(createdAccount.getId(), withdrawAmount);
        Assert.assertEquals(expectedFinalBalance, accountDAO.getAccount(createdAccount.getId()).getBalance());
    }

    @Test (expected = AccountNotFoundException.class)
    public void accountDeposit_whenAccountNotFound_throwsAccountNotFoundException() throws AccountNotFoundException, InsufficientFundsException {
        accountDAO.accountDeposit(1, BigDecimal.ONE);
    }

    @Test
    public void accountDeposit_whenSuccessful_addsAmountToAccountBalance() throws AccountNotFoundException, InsufficientFundsException {
        Account account = AccountTestHelper.createAccount();
        Account createdAccount = accountDAO.createAccount(account);
        BigDecimal depositAmount = BigDecimal.TEN;
        BigDecimal initialBalance = createdAccount.getBalance();
        BigDecimal expectedFinalBalance = initialBalance.add(depositAmount);

        accountDAO.accountDeposit(createdAccount.getId(), depositAmount);
        Assert.assertEquals(expectedFinalBalance, accountDAO.getAccount(createdAccount.getId()).getBalance());
    }
}