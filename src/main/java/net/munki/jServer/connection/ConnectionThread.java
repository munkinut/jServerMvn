package net.munki.jServer.connection;

/*
 * Connection.java
 *
 * Created on 21 May 2003, 11:23
 */

import net.munki.jServer.service.ScriptService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

// import java.io.PrintStream;


public class ConnectionThread extends Thread implements ConnectionThreadInterface {

    // TODO Consider replacing synchronizing with a more up to date method
    private final ScriptService service;
    private final Socket client;
    private AtomicBoolean running = new AtomicBoolean(false);
    private Logger logger;

    public ConnectionThread(Socket client, ScriptService service) {
        this.service = service;
        this.client = client;
        initLogging();
    }

    private void initLogging() {
        logger = Logger.getLogger(this.getClass().getName());
    }

    public void run() {
        setRunning(true);
        InputStream i = null;
        OutputStream o = null;
        if ((client != null) && (!client.isClosed())) {
            String cli = client.getInetAddress().getHostAddress();
            logger.info("Client " + cli + " connected ...");
            try {
                i = client.getInputStream();
                o = client.getOutputStream();
                synchronized (service) {
                    service.serve(i, o);
                }
                i.close();
                o.close();
                logger.info("Client " + cli + " disconnected ...");

            } catch (IOException ioe) {
                logger.warning(ioe.toString());
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                logger.warning(e.getMessage());
            }
        }
        setRunning(false);
        synchronized (this) {
            this.notifyAll();
        }
    }

    private void setRunning(boolean run) {
            running.set(run);
    }

    public synchronized void kill() {
        String serviceName = "";
        if (service != null) serviceName = service.getServiceName();
        logger.info("Kill requested for " + serviceName + " connection ...");
        setRunning(false);
        // interrupt();
    }

}
