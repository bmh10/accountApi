package unit;

import org.account.model.Account;
import org.account.model.dto.AccountDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.account.transformer.AccountTransformer;
import org.account.transformer.AccountTransformerImpl;
import util.AccountTestHelper;

import java.util.Currency;

public class AccountTransformerTest {

    private AccountTransformer accountTransformer;

    @Before
    public void before() {
        accountTransformer = new AccountTransformerImpl();
    }

    @Test
    public void fromDAO_checkConversion() {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        Account account = accountTransformer.fromDTO(dto);

        Assert.assertEquals((int)dto.getId(), account.getId());
        Assert.assertEquals(dto.getAccountHolderName(), account.getAccountHolderName());
        Assert.assertEquals(Currency.getInstance(dto.getCurrency()), account.getCurrency());
        Assert.assertEquals(dto.getBalance(), account.getBalance());
    }

    @Test
    public void toDAO_checkConversion() {
        Account account = AccountTestHelper.createAccount();
        AccountDTO dto = accountTransformer.toDTO(account);

        Assert.assertEquals(account.getId(), (int)dto.getId());
        Assert.assertEquals(account.getAccountHolderName(), dto.getAccountHolderName());
        Assert.assertEquals(account.getCurrency(), Currency.getInstance(dto.getCurrency()));
        Assert.assertEquals(account.getBalance(), dto.getBalance());
    }

    @Test
    public void convertThenConvertBack() {
        AccountDTO dto = AccountTestHelper.createAccountDTO();
        Account account = accountTransformer.fromDTO(dto);
        AccountDTO dto2 = accountTransformer.toDTO(account);
        Assert.assertEquals(dto, dto2);
    }
}
