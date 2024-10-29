//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.otus.chat.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticatedProvider {
    private Server server;
    private List<User> users;

    public InMemoryAuthenticationProvider(Server server) {
        this.server = server;
        this.users = new ArrayList();
        this.users.add(new User("login1", "password1", "username1", Roles.ADMIN));
        this.users.add(new User("qwe", "qwe", "qwe1", Roles.ADMIN));
        this.users.add(new User("asd", "asd", "asd1", Roles.ADMIN));
        this.users.add(new User("zxc", "zxc", "zxc1", Roles.ADMIN));
    }

    public void initialize() {
        System.out.println("Сервис аутентификации запущен: In memory режим");
    }

    private String getUsernameByLoginAndPassword(String login, String password) {
        Iterator var3 = this.users.iterator();

        User user;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            user = (User) var3.next();
        } while (!user.login.equals(login) || !user.password.equals(password));

        return user.username;
    }

    public synchronized boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String authName = this.getUsernameByLoginAndPassword(login, password);
        if (authName == null) {
            clientHandler.sendMessage("Некорректный логин/пароль");
            return false;
        } else if (this.server.isUsernameBusy(authName)) {
            clientHandler.sendMessage("Учетная запись уже занята");
            return false;
        } else {
            clientHandler.setUsername(authName);
            clientHandler.setRole(Roles.ADMIN);
            this.server.subscribe(clientHandler);
            clientHandler.sendMessage("/authok " + authName);
            return true;
        }
    }

    private boolean isLoginAlreadyExist(String login) {
        Iterator var2 = this.users.iterator();

        User user;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            user = (User) var2.next();
        } while (!user.login.equals(login));

        return true;
    }

    private boolean isUsernameAlreadyExist(String username) {
        Iterator var2 = this.users.iterator();

        User user;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            user = (User) var2.next();
        } while (!user.username.equals(username));

        return true;
    }

    public boolean registration(ClientHandler clientHandler, String login, String password, String username) {
        if (login.trim().length() >= 3 && password.trim().length() >= 6 && username.trim().length() >= 2) {
            if (this.isLoginAlreadyExist(login)) {
                clientHandler.sendMessage("Указанный логин уже занят");
                return false;
            } else if (this.isUsernameAlreadyExist(username)) {
                clientHandler.sendMessage("Указанное имя пользователя уже занято");
                return false;
            } else {
                this.users.add(new User(login, password, username, Roles.USER));
                clientHandler.setUsername(username);
                clientHandler.setRole(Roles.USER);
                this.server.subscribe(clientHandler);
                clientHandler.sendMessage("/regok " + username);
                return true;
            }
        } else {
            clientHandler.sendMessage("Требования логин 3+ символа, пароль 6+ символа,имя пользователя 2+ символа не выполнены");
            return false;
        }
    }

    private class User {
        private String login;
        private String password;
        private String username;
        private Roles role;

        public User(String login, String password, String username, Roles role) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.role = role;
        }
    }
}
