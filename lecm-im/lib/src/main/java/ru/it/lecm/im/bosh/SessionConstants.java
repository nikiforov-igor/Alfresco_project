package ru.it.lecm.im.bosh;

public class SessionConstants {

    // Content-type header
    public static final String DEFAULT_CONTENT = "text/xml; charset=utf-8";
    // Maximum inactivity period
    public static final int MAX_INACTIVITY = 60;
    // Maximum number of simultaneous requests allowed
    public static final int MAX_REQUESTS = 5;
    // Maximum time to wait for XMPP server replies
    public static final int MAX_WAIT = 300;
    // Shortest polling period
    public static final int MIN_POLLING = 2;
    // Default XMPP port to connect
    public static final int DEFAULT_XMPPPORT = 5222;
    // Sleep time
	static final int READ_TIMEOUT = 1000;
    // Socket timeout
	static final int SOCKET_TIMEOUT = 6000;
    // Session starting
	protected static final String SESS_START = "starting";
    // Session active
	protected static final String SESS_ACTIVE = "active";
    // Session terminate
	protected static final String SESS_TERM = "term";
}
