//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.otus.chat.server;

public interface AuthenticatedProvider {
    void initialize();

    boolean authenticate(ClientHandler var1, String var2, String var3);

    boolean registration(ClientHandler var1, String var2, String var3, String var4);
}
