package net.munki.jServer.service;

/*
 * ScriptService.java
 *
 * Created on 18 May 2020, 21:57
 */

import net.munki.jServer.exception.jServerException;
import net.munki.jServer.property.PropertyManager;
import net.munki.jServer.script.ScriptHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class ScriptService {

    private Logger log;
    private final ScriptHandler sh;

    private final List<ServiceListenerInterface> serviceListeners;

    private void initLogging() {
        log = Logger.getLogger(this.getClass().getName());
    }

    /** Creates new ScriptHandler */
    public ScriptService() {
        initLogging();
        log.info("ScriptService() called");
        PropertyManager pm = PropertyManager.getInstance();
        String scriptPath = pm.getScriptsLocation();
        log.info("scriptPath = " + scriptPath);
        sh = new ScriptHandler();
        serviceListeners = new ArrayList<>();
    }

    public void serve(InputStream i, OutputStream o) {
        log.info(getServiceName() + " running ...");

        InputStreamReader isr = new InputStreamReader(i);
        OutputStreamWriter osw = new OutputStreamWriter(o);
        PrintWriter pw = new PrintWriter(new BufferedWriter(osw));
        BufferedReader inbound = new BufferedReader(isr);

        String commandLine = "";
        String cmd = "";
        ArrayList<String> paramList = new ArrayList<>();

        log.info("Sending greeting.");
        pw.println("Connected to " + this.getServiceName());
        pw.flush();

        try {
            while ((commandLine = inbound.readLine()) != null) {
                if (commandLine.equals("EXIT")) break;
                StringTokenizer st = new StringTokenizer(commandLine);
                paramList = new ArrayList<>();
                while (st.hasMoreTokens()) {
                    paramList.add(st.nextToken());
                }
                if (!paramList.isEmpty()) {
                    cmd = paramList.remove(0);
                }

                log.info("Read line. Command was " + commandLine);
                String[] params = new String[paramList.size()];
                params = paramList.toArray(params);

                try {
                    sh.handleScript(cmd, "Could be anything!!", i, o, params);
                }
                catch (jServerException e) {
                    pw.println("No such command.");
                    pw.flush();
                    log.warning("No such command: " + cmd);
                }
            }
        }
        catch (IOException e) {
            log.warning("Could not read line from client: " + e.getMessage());
        }

        pw.println("Disconnecting from " + this.getServiceName() + " ...");
        pw.flush();

        try {
            inbound.close();
            pw.close();
            osw.close();
            isr.close();
            log.info(getServiceName() + " finished ...");
        } catch (IOException e) {
            log.warning("Could not gracefully close the connection: " + e.getMessage());
        }

    }


    public String getServiceName() {
        return this.getClass().getName();
    }

    public void addServiceListener(ServiceListenerInterface sli) {
        serviceListeners.add(sli);
    }

    public void setOutput(PrintStream ps) {
    }

}
