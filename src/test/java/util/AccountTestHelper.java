package util;

import org.account.model.Account;
import org.account.model.dto.AccountDTO;
import org.account.model.dto.TransferDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

public class AccountTestHelper {

    public static List<AccountDTO> createAccountDTOs(int n) {
        List<AccountDTO> dtos = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            dtos.add(createAccountDTO(i));
        }

        return dtos;
    }

    public static List<Account> createAccounts(int n) {
        List<Account> accounts = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            accounts.add(createAccount(i));
        }

        return accounts;
    }

    public static AccountDTO createAccountDTO() {
        return createAccountDTO(1);
    }

    public static AccountDTO createAccountDTO(int id) {
        AccountDTO dto = new AccountDTO();
        dto.setId(id);
        dto.setAccountHolderName(String.format("Account holder %s", id));
        dto.setCurrency("GBP");
        dto.setBalance(BigDecimal.valueOf(id * 100 + 0.5));
        return dto;
    }

    public static Account createAccount() {
        return createAccount(1);
    }

    public static Account createAccount(int id) {
        Account account = new Account();
        account.setId(id);
        account.setAccountHolderName(String.format("Account holder %s", id));
        account.setCurrency(Currency.getInstance("GBP"));
        account.setBalance(BigDecimal.valueOf(id * 100 + 0.5));
        account.setCreatedDate(new Date());
        return account;
    }

    public static TransferDTO createTransferDTO() {
        TransferDTO dto = new TransferDTO();
        dto.setSourceAccountId(1);
        dto.setDestinationAccountId(2);
        dto.setCurrency("GBP");
        dto.setTransferAmount(BigDecimal.valueOf(100));
        return dto;
    }
}
