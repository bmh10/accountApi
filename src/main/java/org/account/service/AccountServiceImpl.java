package org.account.service;

import org.account.dao.AccountDAO;
import org.account.exception.*;
import org.account.model.Account;
import org.account.model.dto.AccountDTO;
import org.account.model.dto.TransferDTO;
import org.account.transformer.AccountTransformer;
import org.account.validator.Validator;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {

    private static final String INSUFFICIENT_FOUNDS_ERR = "Account %s has insufficient funds to perform this transaction";
    private static final String NOT_MATCHING_CURRENCY_ERR = "Transfer currency must be the same as the currency of accounts involved in transaction";
    private static final String MONEY_TRANSFER_ERR = "An error occurred while transferring money between accounts %s and %s. Will attempt to rollback.";
    private static final String ROLLBACK_ERR = "ERROR: Rollback of %s failed. AccountID: %s, Amount: %s";

    private Validator<AccountDTO> accountValidator;
    private Validator<TransferDTO> transferValidator;
    private AccountTransformer accountTransformer;
    private AccountDAO accountDAO;

    public AccountServiceImpl(
            Validator<AccountDTO> accountValidator,
            Validator<TransferDTO> transferValidator,
            AccountTransformer accountTransformer,
            AccountDAO accountDAO) {
        this.accountValidator = accountValidator;
        this.transferValidator = transferValidator;
        this.accountTransformer = accountTransformer;
        this.accountDAO = accountDAO;
    }

    public Collection<AccountDTO> getAllAccounts() {
        return accountDAO.getAllAccounts().stream().map(accountTransformer::toDTO).collect(Collectors.toList());
    }

    public AccountDTO getAccount(int id) throws AccountNotFoundException {
        Account a = accountDAO.getAccount(id);
        return accountTransformer.toDTO(a);
    }

    public AccountDTO createAccount(AccountDTO accountDTO) throws RequiredParameterException, InvalidParameterException {
        accountValidator.validate(accountDTO);
        Account account = accountTransformer.fromDTO(accountDTO);
        account.setCreatedDate(new Date());
        Account createdAccount = accountDAO.createAccount(account);
        return accountTransformer.toDTO(createdAccount);
    }

    public void transferMoney(TransferDTO transferDTO)
            throws AccountNotFoundException, InsufficientFundsException,
                   InvalidParameterException, RequiredParameterException, CurrencyConversionException {

        transferValidator.validate(transferDTO);
        Account srcAccount = accountDAO.getAccount(transferDTO.getSourceAccountId());
        Account dstAccount = accountDAO.getAccount(transferDTO.getDestinationAccountId());
        BigDecimal amount = transferDTO.getTransferAmount();

        if (!srcAccount.getCurrency().getCurrencyCode().equals(transferDTO.getCurrency()) ||
                !dstAccount.getCurrency().getCurrencyCode().equals(transferDTO.getCurrency())) {
            throw new CurrencyConversionException(NOT_MATCHING_CURRENCY_ERR);
        }

        doTransfer(srcAccount, dstAccount, amount);
    }

    private void doTransfer(Account srcAccount, Account dstAccount, BigDecimal amount) throws InsufficientFundsException {
        synchronized (srcAccount) {
            synchronized (dstAccount) {
                if (srcAccount.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException(String.format(INSUFFICIENT_FOUNDS_ERR, srcAccount.getId()));
                }

                boolean withdrawSuccess = false;
                boolean depositSuccess = false;
                try {
                    withdrawSuccess = accountDAO.accountWithdraw(srcAccount.getId(), amount);
                    depositSuccess = accountDAO.accountDeposit(dstAccount.getId(), amount);
                }
                catch (Exception e) {
                    System.out.print(String.format(MONEY_TRANSFER_ERR, srcAccount.getId(), dstAccount.getId()));
                }
                finally {
                    // If one of the actions did not complete, we need to rollback.
                    // Normally we would let the DB handle the transaction rollback,
                    // but since everything is stored in memory we can't do this.
                    // Below code is not ideal as the rollback could also fail...
                    if (!(withdrawSuccess && depositSuccess)) {
                        if (withdrawSuccess) {
                            try {
                                accountDAO.accountDeposit(srcAccount.getId(), amount);
                            }
                            catch (Exception e) {
                                System.out.println(String.format(ROLLBACK_ERR, "withdraw", srcAccount.getId(), amount));
                            }
                        }
                        if (depositSuccess) {
                            try {
                                accountDAO.accountWithdraw(dstAccount.getId(), amount);
                            }
                            catch (Exception e) {
                                System.out.println(String.format(ROLLBACK_ERR, "deposit", srcAccount.getId(), amount));
                            }
                        }
                    }
                }
            }
        }
    }
}
