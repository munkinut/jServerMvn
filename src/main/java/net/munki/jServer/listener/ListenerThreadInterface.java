package net.munki.jServer.listener;

/*
 * Created on 23-May-2003
 *
 */

// import java.io.PrintStream;

/**
 * @author Warren Milburn
 */
public interface ListenerThreadInterface {
    void run();

    void kill();
}