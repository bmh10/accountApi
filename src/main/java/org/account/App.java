package org.account;

public class App {

    private static final int PORT = 2223;

    public static void main(String[] args) throws Exception {
        AppServer server = new AppServer(PORT);
        server.runServer();
    }
}
