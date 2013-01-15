package ru.it.lecm.im.client;

public interface MessageReceiveListener
{
    void onMessageReceive(final String jid, final String message);
}
