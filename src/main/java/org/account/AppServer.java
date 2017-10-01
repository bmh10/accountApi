package org.account;

import org.account.model.dto.TransferDTO;
import org.account.validator.TransferValidatorImpl;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.account.api.AccountApi;
import org.account.api.AccountApiImpl;
import org.account.dao.AccountDAO;
import org.account.dao.AccountDAOImpl;
import org.account.model.dto.AccountDTO;
import org.account.service.AccountService;
import org.account.service.AccountServiceImpl;
import org.account.transformer.AccountTransformer;
import org.account.transformer.AccountTransformerImpl;
import org.account.validator.AccountValidatorImpl;
import org.account.validator.Validator;

public class AppServer {

    private static final String API_PACKAGE = "org.account.api";
    private Server server;

    public AppServer(int port) {
        configureServer(port);
    }

    public void runServer() throws Exception {
        try {
            server.start();
            server.join();
        }
        finally {
            server.destroy();
        }
    }

    public void runTestServer() throws Exception {
        server.start();
    }

    public void stopTestServer() throws Exception {
        server.stop();
    }

    private void configureServer(int port) {
        AccountApi accountApi = createApi();
        ServletHolder servlet = configureServlet(accountApi);
        server = configureServer(servlet, port);
    }

    private AccountApi createApi() {
        Validator<AccountDTO> accountValidator = new AccountValidatorImpl();
        Validator<TransferDTO> transferValidator = new TransferValidatorImpl();
        AccountTransformer transformer = new AccountTransformerImpl();
        AccountDAO dao = new AccountDAOImpl();
        AccountService service = new AccountServiceImpl(accountValidator, transferValidator, transformer, dao);
        return new AccountApiImpl(service);
    }

    private ServletHolder configureServlet(AccountApi accountApi) {
        ResourceConfig config = new ResourceConfig();
        config.packages(API_PACKAGE);
        config.register(accountApi);
       return new ServletHolder(new ServletContainer(config));
    }

    private org.eclipse.jetty.server.Server configureServer(ServletHolder servlet, int port) {
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(port);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");
        return server;
    }
}
