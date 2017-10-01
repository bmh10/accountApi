package system;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.account.model.dto.AccountDTO;
import org.account.model.dto.TransferDTO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.account.AppServer;
import util.AccountTestHelper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

public class AccountApiSystemTest {

    private static final int TEST_PORT = 2230;
    private static final String BASE_URI = "http://localhost:" + TEST_PORT + "/api/v1/account";
    private AppServer server;
    private ObjectMapper mapper;

    @Before
    public void before() throws Exception {
        mapper = new ObjectMapper();
        server = new AppServer(TEST_PORT);
        server.runTestServer();
    }

    @After
    public void after() throws Exception {
        server.stopTestServer();
    }

    @Test
    public void accountCreationWorkflowTest() throws Exception {
        // Create account
        AccountDTO dto = AccountTestHelper.createAccountDTO();

        Response response = create(dto);
        assertResponseOk(response);

        AccountDTO createdAccount = response.readEntity(AccountDTO.class);

        // Check account was created
        response = read(createdAccount.getId());
        assertResponseOk(response);
        Assert.assertEquals(createdAccount, response.readEntity(AccountDTO.class));
    }

    @Test
    public void transferMoneyWorkflowTest() throws Exception {
        // Create 2 accounts
        List<AccountDTO> dtos = AccountTestHelper.createAccountDTOs(2);
        for (AccountDTO dto : dtos) {
            create(dto);
        }

        // Check accounts were created
        Response response = readAll();
        assertResponseOk(response);

        List<AccountDTO> allDtos = response.readEntity(new GenericType<List<AccountDTO>>() {});
        for (int i = 0; i < 2; i++) {
            dtos.get(i).setCreatedDate(allDtos.get(i).getCreatedDate());
            Assert.assertEquals(dtos.get(i), allDtos.get(i));
        }

        // Transfer money from one account to other
        BigDecimal transferAmount = BigDecimal.valueOf(50);
        BigDecimal account1InitialBalance = allDtos.get(0).getBalance();
        BigDecimal account2InitialBalance = allDtos.get(1).getBalance();
        BigDecimal account1ExpectedFinalBalance = account1InitialBalance.subtract(transferAmount);
        BigDecimal account2ExpectedFinalBalance = account2InitialBalance.add(transferAmount);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setSourceAccountId(1);
        transferDTO.setDestinationAccountId(2);
        transferDTO.setCurrency("GBP");
        transferDTO.setTransferAmount(transferAmount);
        response = transfer(transferDTO);
        assertResponseOk(response);

        // Check accounts have updated balances
        response = read(1);
        assertResponseOk(response);
        Assert.assertEquals(account1ExpectedFinalBalance, response.readEntity(AccountDTO.class).getBalance());

        response = read(2);
        assertResponseOk(response);
        Assert.assertEquals(account2ExpectedFinalBalance, response.readEntity(AccountDTO.class).getBalance());
    }

    private Response create(AccountDTO accountDTO) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(BASE_URI);
        String json = mapper.writeValueAsString(accountDTO);
        return resource.request().accept(MediaType.APPLICATION_JSON).post(Entity.json(json));
    }

    private Response read(int id) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(BASE_URI + "/" + id);
        return resource.request().accept(MediaType.APPLICATION_JSON).get();
    }

    private Response readAll() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(BASE_URI);
        return resource.request().accept(MediaType.APPLICATION_JSON).get();
    }

    private Response transfer(TransferDTO transferDTO) throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget resource = client.target(BASE_URI);
        String json = mapper.writeValueAsString(transferDTO);
        return resource.request().accept(MediaType.APPLICATION_JSON).put(Entity.json(json));
    }

    private void assertResponseOk(Response response) {
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    private void assertNotFound(Response response) {
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

}
