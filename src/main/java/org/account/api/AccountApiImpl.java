package org.account.api;

import org.account.exception.*;
import org.account.model.dto.AccountDTO;
import org.account.model.dto.TransferDTO;
import org.account.service.AccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("api/v1/account")
public class AccountApiImpl extends AbstractApi implements AccountApi {

    private AccountService accountService;

    public AccountApiImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Gets all accounts
     * @return Response object containing collection of all accounts
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts() {
        Collection<AccountDTO> accounts = accountService.getAllAccounts();
        return ok(accounts);
    }

    /**
     * Gets account with specified ID
     * @param id The account ID
     * @return Response object containing the account
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") int id) {
        try {
            AccountDTO account = accountService.getAccount(id);
            return ok(account);
        }
        catch (AccountNotFoundException e) {
            return notFound(e.getMessage());
        }
    }

    /**
     * Creates account from provided account DTO
     * @param accountDTO The account DTO
     * @return Response object containing the created account
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountDTO accountDTO) {
        try {
            AccountDTO account = accountService.createAccount(accountDTO);
            return ok(account);
        }
        catch (RequiredParameterException | InvalidParameterException e) {
            return badRequest(e.getMessage());
        }
    }

    /**
     * Transfers money from one account to another
     * @param transferDTO The transfer DTO
     * @return Response object
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoney(TransferDTO transferDTO) {
        try {
            accountService.transferMoney(transferDTO);
            return ok();
        } catch (RequiredParameterException | InvalidParameterException | AccountNotFoundException e) {
            return badRequest(e.getMessage());
        } catch (CurrencyConversionException | InsufficientFundsException e) {
            return notAcceptable(e.getMessage());
        }
    }
}
