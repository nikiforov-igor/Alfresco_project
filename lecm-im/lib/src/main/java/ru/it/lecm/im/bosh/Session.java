/**
 * PALLADIUM v1.4
 * Description: the session tool
 * Authors: Stefan Strigler, Vanaryon
 * License: GNU GPL
 * Last revision: 24/10/10
 */

package ru.it.lecm.im.bosh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Session with HTTP Bind
public class Session {

    private final static Logger logger = LoggerFactory.getLogger(Session.class);

    private static Hashtable sessions = new Hashtable();
	
	private static TransformerFactory tff = TransformerFactory.newInstance();
	
	private static String createSessionID(int len) {
		String charlist = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
		
		Random rand = new Random();
		
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < len; i++)
			str.append(charlist.charAt(rand.nextInt(charlist.length())));

        logger.trace("New session id created: " + str);

		return str.toString();
	}
	
	public static Session getSession(String sid) {
		return (Session) sessions.get(sid);
	}
	
	public static Enumeration getSessions() {
		return sessions.elements();
	}
	
	public static int getNumSessions() {
		return sessions.size();
	}
	
	public static void stopSessions() {
		for (Enumeration e = sessions.elements(); e.hasMoreElements();)
			((Session) e.nextElement()).terminate();
	}
	
	private String authid;
	
	boolean authidSent = false;
	
	boolean streamFeatures = false;
	
	private String content = SessionConstants.DEFAULT_CONTENT;
	
	private DocumentBuilder db;
	
	private int hold = SessionConstants.MAX_REQUESTS - 1;
	
	private String inQueue = "";
	
	private BufferedReader br;
	
	private String key;
	
	private long lastActive;
	
	private long lastPoll = 0;
	
	private OutputStreamWriter osw;
	
	private TreeMap responses;
	
	private String status = SessionConstants.SESS_START;
	
	private String sid;
	
	public Socket sock;
	
	private String to;
	
	private DNSUtil.HostAddress host = null;
	
	private int wait = SessionConstants.MAX_WAIT;
	
	private String xmllang = null;
	
	private boolean reinit = false;
	
	private boolean secure = false;
	
	private boolean pauseForHandshake = false;
	
	private Pattern streamPattern;
	
	private Pattern stream10Test;
	
	private Pattern stream10Pattern;
	
	// Create a new session and connect to the XMPP server
	public Session(String to, String route, String xmllang) throws UnknownHostException, IOException {
		logger.trace("Create a new session and connect to the XMPP server");
        this.to = to;
		this.xmllang = xmllang;
		
		int port = SessionConstants.DEFAULT_XMPPPORT;
		
		this.sock = new Socket();
		this.setLastActive();
		
		try {
            logger.trace("Session: DocumentBuilderFactory.newInstance().newDocumentBuilder()");
            this.db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		
		catch (Exception e) {
            logger.error("Session: DocumentBuilderFactory.newInstance().newDocumentBuilder()", e);
        }
		
		// First, try connecting throught the 'route' attribute.
		if (route != null && !route.equals("")) {
			logger.trace("Trying to use 'route' attribute to open a socket...");
			
			if (route.startsWith("xmpp:"))
				route = route.substring("xmpp:".length());
			
			int i;
			
			// Has 'route' the optional port?
			if ((i = route.lastIndexOf(":")) != -1) {
				try {
					int p = Integer.parseInt(route.substring(i + 1));
					
					if (p >= 0 && p <= 65535) {
						port = p;
                        logger.trace("...route attribute holds a valid port (" + port + ").");
					}
				}
				
				catch (NumberFormatException nfe) { }
				
				route = route.substring(0, i);
			}

            logger.trace("Trying to open a socket to '" + route + "', using port " + port + ".");
			
			try {
				this.sock.connect(new InetSocketAddress(route, port), SessionConstants.SOCKET_TIMEOUT);
			}
			
			catch (Exception e) {
                logger.error("Failed to open a socket using the 'route' attribute", e);
			}
		}
		
		// If no socket has been opened, try connecting trough the 'to' attribute
		if (this.sock == null || !this.sock.isConnected()) {
            logger.trace("no socket has been opened, try connecting trough the 'to' attribute");
			this.sock = new Socket();
            logger.trace("Trying to use 'to' attribute to open a socket...");
			
			host = DNSUtil.resolveXMPPServerDomain(to, SessionConstants.DEFAULT_XMPPPORT);
			
			try {
                logger.trace("Trying to open a socket to '" + host.getHost() + "', using port " + host.getPort() + ".");
				this.sock.connect(new InetSocketAddress(host.getHost(), host.getPort()), SessionConstants.SOCKET_TIMEOUT);
			}
			
			catch (UnknownHostException uhe) {
                logger.error("Failed to open a socket using the 'to' attribute: ",  uhe);
				throw uhe;
			
			}
			
			catch (IOException ioe) {
                logger.error("Failed to open a socket using the 'to' attribute: ", ioe);
				throw ioe;
			}
		}
		
		// At this point, we either have a socket, or an exception has already been thrown
		try {
			if (this.sock.isConnected())
            {
                logger.trace("Succesfully connected to " + to);
            }
			
			this.sock.setSoTimeout(SessionConstants.SOCKET_TIMEOUT);
			
			this.osw = new OutputStreamWriter(this.sock.getOutputStream(), "UTF-8");
			
			this.osw.write("<stream:stream to='" + this.to + "'"
					+ appendXMLLang(this.xmllang)
					+ " xmlns='jabber:client'"
					+ " xmlns:stream='http://etherx.jabber.org/streams'"
					+ " version='1.0'" + ">");
			
			this.osw.flush();
			
			// Create unique session id
			while (sessions.get(this.sid = createSessionID(24)) != null){
                logger.trace("Created invalid session id");
            }

            logger.trace("creating session with id " + this.sid);
			
			// Register session
			sessions.put(this.sid, this);
			
			// Create list of responses
			this.responses = new TreeMap();
			
			this.br = new BufferedReader(new InputStreamReader(this.sock.getInputStream(), "UTF-8"));
			
			this.streamPattern = Pattern.compile(".*<stream:stream[^>]*id=['|\"]([^'|^\"]+)['|\"][^>]*>.*", Pattern.DOTALL);
			
			this.stream10Pattern = Pattern.compile(".*<stream:stream[^>]*id=['|\"]([^'|^\"]+)['|\"][^>]*>.*(<stream.*)$", Pattern.DOTALL);
			
			this.stream10Test = Pattern.compile(".*<stream:stream[^>]*version=['|\"]1.0['|\"][^>]*>.*", Pattern.DOTALL);
			
			this.setStatus(SessionConstants.SESS_ACTIVE);
		}
		
		catch (IOException ioe) {
            logger.error("Error while creating session", ioe);
			throw ioe;
		}
	}

	// Adds new response to list of known responses.
	public synchronized Response addResponse(Response r) {
		while (this.responses.size() > 0 && this.responses.size() >= SessionConstants.MAX_REQUESTS)
			this.responses.remove(this.responses.firstKey());
		
		return (Response) this.responses.put(new Long(r.getRID()), r);
	}

	// Checks InputStream from server for incoming packets blocks until request timeout or packets available
	private int init_retry = 0;

    public NodeList checkInQ(long rid) throws IOException {
        return checkInQ(rid, 0);
    }

	private NodeList checkInQ(long rid, long depth) throws IOException {
		NodeList nl = null;

		logger.trace("checkInQ: readFromSocket");
        inQueue += this.readFromSocket(rid);
        logger.trace("checkInQ: readFromSocket DONE!");

        logger.trace("inQueue: " + inQueue.toString());

        logger.trace("Session id " + this.sid + " inQueue depth:" + depth);

		if (init_retry < 1000 && (this.authid == null || this.isReinit()) && inQueue.length() > 0) {
			init_retry++;
			
			if (stream10Test.matcher(inQueue).matches()) {
				Matcher m = stream10Pattern.matcher(inQueue);
				
				if (m.matches()) {
					this.authid = m.group(1);
					inQueue = m.group(2);
                    logger.trace("inQueue: " + inQueue.toString());
					streamFeatures = inQueue.length() > 0;
				}
				
				else {
                    logger.trace("failed to get stream features");
					
					try {
						Thread.sleep(5);
					}
					
					catch (InterruptedException ie) {
                        logger.error("Session interrpted", ie);
                    }
					
					// Retry
                    logger.trace("inQueue: we need to go deeper!");
					return this.checkInQ(rid, depth + 1);
				}
			}
			
			else {
				// Legacy XMPP stream
				Matcher m = streamPattern.matcher(inQueue);
				
				if (m.matches())
					this.authid = m.group(1);
				
				else {
                    logger.trace("failed to get authid");
					
					try {
						Thread.sleep(5);
					}
					
					catch (InterruptedException ie) { }
					
					// Retry
                    return this.checkInQ(rid, depth + 1);
				}
			}
			
			// Reset
			init_retry = 0;
		}
		
		// Try to parse it
		if (!inQueue.equals("")) {
			try {
				Document doc = null;
				
				if (streamFeatures)
					doc = db.parse(new InputSource(new StringReader("<doc>" + inQueue + "</doc>")));
				
				else
					try {
						doc = db.parse(new InputSource(new StringReader("<doc xmlns='jabber:client'>" + inQueue + "</doc>")));
					}
					
					catch (SAXException sex) {
						try {
							// Stream closed?
							doc = db.parse(new InputSource(new StringReader("<stream:stream>" + inQueue)));
							this.terminate();
						}
						
						catch (SAXException sex2) { }
					}
				
				if (doc != null)
					nl = doc.getFirstChild().getChildNodes();
				
				// Check for StartTLS
				if (streamFeatures) {
					for (int i = 0; i < nl.item(0).getChildNodes().getLength(); i++) {
						if (nl.item(0).getChildNodes().item(i).getNodeName().equals("starttls")) {
							if (!this.isReinit()) {
                                logger.trace("starttls present, trying to use it");
								this.osw.write("<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/>");
								this.osw.flush();
								
								String response = this.readFromSocket(rid);
                                logger.trace(response);
								
								TrustManager[] trustAllCerts = new TrustManager[] {
									new X509TrustManager() {
										public X509Certificate[] getAcceptedIssuers() {
											return null;
										}
										
										public void checkClientTrusted(X509Certificate[] certs, String authType) { }
										
										public void checkServerTrusted(X509Certificate[] certs, String authType) { }
									}
								};
								
								try {
									SSLContext sc = SSLContext.getInstance("TLS");
									sc.init(null, trustAllCerts, null);
									
									SSLSocketFactory sslFact = sc.getSocketFactory();
									
									SSLSocket tls;
									
									tls = (SSLSocket) sslFact.createSocket(this.sock, this.sock.getInetAddress().getHostName(), this.sock.getPort(), false);
									tls.addHandshakeCompletedListener(new HandShakeFinished(this));
									
									this.pauseForHandshake = true;

                                    logger.trace("initiating handshake");
									
									tls.startHandshake();
									
									try {
										while (this.pauseForHandshake) {
                                            logger.trace(".");
											Thread.sleep(5);
										}
									}
									
									catch (InterruptedException ire) { }

                                    logger.trace("TLS Handshake complete");
									
									this.sock = tls;
									this.sock.setSoTimeout(SessionConstants.SOCKET_TIMEOUT);
									
									this.br = new SSLSocketReader((SSLSocket) tls);
									
									this.osw = new OutputStreamWriter(tls.getOutputStream(), "UTF-8");
									
									// Reset
									this.inQueue = "";
									this.setReinit(true);
									this.osw.write("<stream:stream to='" + this.to + "'" + appendXMLLang(this.getXMLLang()) + " xmlns='jabber:client' " + " xmlns:stream='http://etherx.jabber.org/streams'" + " version='1.0'" + ">");
									this.osw.flush();

                                    return this.checkInQ(rid, depth + 1);
								}
								
								catch (Exception ssle) {
                                    logger.error("STARTTLS failed: ", ssle);
									
									this.setReinit(false);
									
									if (this.isSecure()) {
										if (!this.sock.getInetAddress().getHostName().equals("localhost") && !this.getResponse(rid).getReq().getServerName().equals(this.sock.getInetAddress().getHostName())) {
                                            logger.trace("secure connection requested but failed");
											throw new IOException();
										}
										
										else
                                            logger.trace("secure requested and we're local");
									}
									
									else
                                        logger.trace("tls failed but we don't need to be secure");
									
									if (this.sock.isClosed()) {
                                        logger.trace("socket closed");
										
										// Reconnect
										Socket s = new Socket();
										s.connect(this.sock.getRemoteSocketAddress(), SessionConstants.SOCKET_TIMEOUT);
										
										this.sock = s;
										this.sock.setSoTimeout(SessionConstants.SOCKET_TIMEOUT);
										this.br = new BufferedReader(new InputStreamReader(this.sock.getInputStream(), "UTF-8"));
										this.osw = new OutputStreamWriter(this.sock.getOutputStream(), "UTF-8");
										
										// Reset
										this.inQueue = "";
										this.setReinit(true);
										
										this.osw.write("<stream:stream to='" + this.to + "'" + appendXMLLang(this.getXMLLang()) + " xmlns='jabber:client' " + " xmlns:stream='http://etherx.jabber.org/streams'" + " version='1.0'" + ">");
										this.osw.flush();

                                        return this.checkInQ(rid, depth + 1);
									}
								}
							}
							
							else
								nl.item(0).removeChild(nl.item(0).getChildNodes().item(i));
						}
					}
				}
				
				if (doc != null) 
					inQueue = "";
			}
			
			catch (SAXException sex3) {
				this.setReinit(false);
                logger.error("failed to parse inQueue: " + inQueue , sex3);
				
				return null;
			}
		}
		
		this.setReinit(false);
		this.setLastActive();
		return nl;
	}
	
	private class HandShakeFinished implements javax.net.ssl.HandshakeCompletedListener {
		private Session sess;
		
		public HandShakeFinished(Session sess) {
			this.sess = sess;
		}
		
		public void handshakeCompleted(javax.net.ssl.HandshakeCompletedEvent event) {
            logger.trace("startTLS: Handshake is complete");
			
			this.sess.pauseForHandshake = false;
			return;
		}
	}
	
	// Checks whether given request ID is valid within context of this session.
	public synchronized boolean checkValidRID(long rid) {
		try {
			if (rid <= ((Long) this.responses.lastKey()).longValue() + SessionConstants.MAX_REQUESTS && rid >= ((Long) this.responses.firstKey()).longValue())
				return true;
			
			else {
                logger.trace("invalid request id: " + rid + " (last: " + ((Long) this.responses.lastKey()).longValue() + ")");
				
				return false;
			}
		}
		
		catch (NoSuchElementException e) {
			return false;
		}
	}
	
	public String getAuthid() {
		return this.authid;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public int getHold() {
		return this.hold;
	}
	
	// Returns the key.
	public synchronized String getKey() {
		return key;
	}
	
	// Returns the lastActive.
	public synchronized long getLastActive() {
		return lastActive;
	}

	// Returns the lastPoll.
	public synchronized long getLastPoll() {
		return lastPoll;
	}
	
	// Lookup response for given request id
	public synchronized Response getResponse(long rid) {
		return (Response) this.responses.get(new Long(rid));
	}
	
	public String getSID() {
		return this.sid;
	}
	
	public String getTo() {
		return this.to;
	}
	
	public int getWait() {
		return this.wait;
	}
	
	public String getXMLLang() {
		return this.xmllang;
	}
	
	public String appendXMLLang(String locale) {
		if(locale != null)
			return " xml:lang='" + locale + "'";
		
		return "";
	}
	
	public synchronized int numPendingRequests() {
		int num_pending = 0;

        for (Object o : this.responses.values()) {
            Response r = (Response) o;

            if (!r.getStatus().equals(Response.STATUS_DONE))
                num_pending++;
        }
		
		return num_pending;
	}
	
	private long lastDoneRID;
	
	public synchronized long getLastDoneRID() {
		return this.lastDoneRID;
	}
	
	// Reads from socket
	private String readFromSocket(long rid) throws IOException {
		StringBuilder retval = new StringBuilder();
		char buf[] = new char[16];
		int c = 0;
		
		Response r = this.getResponse(rid);
		
		while (!this.sock.isClosed() && !this.isStatus(SessionConstants.SESS_TERM)) {
			this.setLastActive();
			try {
                logger.trace("this.br.ready()");
				if (this.br.ready()) {
                    logger.trace("this.br.ready() == true");
					while (this.br.ready() && (c = this.br.read(buf, 0, buf.length)) >= 0) {
                        retval.append(new String(buf, 0, c));
                        logger.trace("readFromSocket inner While end");
                    }

                    break;
				}
				
				else {
					if ((this.hold == 0 && r != null && System.currentTimeMillis() - r.getCDate() > 200) || (this.hold > 0 && ((r != null && System.currentTimeMillis() - r.getCDate() >= this.getWait() * 1000) || this.numPendingRequests() > this.getHold() || (retval.length() > 0))) || r.isAborted()) {
                        logger.trace("readFromSocket done for " + rid);
						break;
					}
					
                    // Wait for incoming packets
                    logger.trace("Wait for incoming packets");
                    Thread.sleep(SessionConstants.READ_TIMEOUT);

				}
			} catch (IOException e) {
				logger.error("Can't read from socket", e);
				this.terminate();
			} catch (InterruptedException ie) {
                logger.error(ie.getMessage(), ie);
            }
        }
		
		if (this.sock.isClosed()) {
			logger.error("Socket is closed!");
            throw new IOException("Socket is closed!");
		}
		
		return retval.toString();
	}

	// Sends all nodes in list to remote XMPP server make sure that nodes get
	public Session sendNodes(NodeList nl) {
		// Build a string
		StringBuilder out = new StringBuilder();
		StreamResult strResult = new StreamResult();
		
		try {
			Transformer tf = tff.newTransformer();
			tf.setOutputProperty("omit-xml-declaration", "yes");
			
			// Loop list
			for (int i = 0; i < nl.getLength(); i++) {
				strResult.setWriter(new StringWriter());
				tf.transform(new DOMSource(nl.item(i)), strResult);
				String tStr = strResult.getWriter().toString();
				out.append(tStr);
			}
		}
		
		catch (Exception e) {
            logger.error("XML.toString(Document): " ,e);
		}
		
		try {
			if (this.isReinit()) {
                logger.trace("Reinitializing Stream!");
				this.osw.write("<stream:stream to='" + this.to + "'" + appendXMLLang(this.getXMLLang()) + " xmlns='jabber:client' " + " xmlns:stream='http://etherx.jabber.org/streams'" + " version='1.0'" + ">");
			}
			
			this.osw.write(out.toString());
			this.osw.flush();
		}
		
		catch (IOException ioe) {
            logger.error(this.sid + " failed to write to stream", ioe);
		}
		
		return this;
	}
	
	public Session setContent(String content) {
		this.content = content;
		return this;
	}
	
	public Session setHold(int hold) {
		if (hold < SessionConstants.MAX_REQUESTS && hold >= 0)
			this.hold = hold;
		return this;
	}
	
	// The key to set.
	public synchronized void setKey(String key) {
		this.key = key;
	}
	
	// Set lastActive to current timestamp
	public synchronized void setLastActive() {
		this.lastActive = System.currentTimeMillis();
	}
	
	public synchronized void setLastDoneRID(long rid) {
		this.lastDoneRID = rid;
	}
	
	// Set lastPoll to current timestamp
	public synchronized void setLastPoll() {
		this.lastPoll = System.currentTimeMillis();
	}
	
	public int setWait(int wait) {
		if (wait < 0)
			wait = 0;
		if (wait > SessionConstants.MAX_WAIT)
			wait = SessionConstants.MAX_WAIT;
		this.wait = wait;
		return wait;
	}
	
	public Session setXMLLang(String xmllang) {
		this.xmllang = xmllang;
		return this;
	}
	
	// Returns the reinit.
	public synchronized boolean isReinit() {
		return reinit;
	}
	
	// Returns the secure
	public synchronized boolean isSecure() {
		return secure;
	}
	
	// The reinit to set.
	public synchronized void setReinit(boolean reinit) {
		this.reinit = reinit;
	}
	
	public synchronized void setStatus(String status) {
		this.status = status;
	}
	
	public synchronized boolean isStatus(String status) {
		return (this.status == status);
	}
	
	// Kill this session
	public void terminate() {
        logger.trace("terminating session " + this.getSID());
		this.setStatus(SessionConstants.SESS_TERM);
		synchronized (this.sock) {
			if (!this.sock.isClosed()) {
				try {
					this.osw.write("</stream:stream>");
					this.osw.flush();
					this.sock.close();
				}
				
				catch (IOException ie) { }
			}
			
			this.sock.notifyAll();
		}
		
		sessions.remove(this.sid);
	}

	// The secure to set
	public synchronized void setSecure(boolean secure) {
		this.secure = secure;
	}
}
