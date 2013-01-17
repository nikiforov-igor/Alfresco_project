package ru.it.lecm.im.client.listeners;

public interface MessageReceiveListener
{
    void onMessageReceive(final String jid, final String message);
}
