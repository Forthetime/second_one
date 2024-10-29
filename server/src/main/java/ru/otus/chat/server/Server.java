//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.otus.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private AuthenticatedProvider authenticatedProvider;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList();
        this.authenticatedProvider = new InMemoryAuthenticationProvider(this);
        this.authenticatedProvider.initialize();
    }

    public AuthenticatedProvider getAuthenticatedProvider() {
        return this.authenticatedProvider;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);

            try {
                System.out.println("Сервер запущен на порту: " + this.port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    new ClientHandler(this, socket);
                }
            } catch (Throwable var5) {
                try {
                    serverSocket.close();
                } catch (Throwable var4) {
                    var5.addSuppressed(var4);
                }

                throw var5;
            }
        } catch (IOException var6) {
            IOException e = var6;
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        this.clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
    }

    public synchronized ClientHandler findByUsername(String username) {
        ClientHandler findClient=null;
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                findClient=client;
            }
        }
        return findClient;
    }

    public synchronized boolean isAdmin(ClientHandler clienthandler) {
        if (clienthandler.getRole().equals(Roles.ADMIN)) {
            return true;
        } if (clienthandler.getRole().equals(Roles.USER)) {
            return false;
        }
        else {return false;}
    }


    public synchronized void broadcastMessage(String message) {
        Iterator var2 = this.clients.iterator();

        while (var2.hasNext()) {
            ClientHandler client = (ClientHandler) var2.next();
            client.sendMessage(message);
        }

    }

    public synchronized void personalMessage(String message, String username) {
        ClientHandler chosen;
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                chosen = client;
                chosen.sendMessage(message);
            }
        }
    }

    public boolean isUsernameBusy(String username) {
        Iterator var2 = this.clients.iterator();

        ClientHandler client;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            client = (ClientHandler) var2.next();
        } while (!client.getUsername().equals(username));

        return true;
    }
}




