package ru.it.lecm.im.client;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 18.12.12
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class Log {
    public static native void consoleLog( String message) /*-{
        console.log( "Log: " + message );
    }-*/;
}
