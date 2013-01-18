package ru.it.lecm.im.client;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 18.12.12
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class Log {

    static {
        ensureLogPresense();
    }

    private static native void ensureLogPresense() /*-{
        if (!window.console)
        {
            window.console = {
                 log : function(){}
            };
        }

        if (!window.Alfresco){
            window.Alfresco = {};
            if (!window.Alfresco.logger){
                window.Alfresco.logger = {
                    debug : function(){}
                }
            }
        }
    }-*/;

    public static void log(String message)
    {
        logConsole(message);
        alfrescoLog(message);
    }


    public static native void logConsole(String message) /*-{
        console.log( "IMLog: " + message );

    }-*/;

    public static native void alfrescoLog(String message) /*-{
        Alfresco.logger.debug("IMLog: "+ message);
    }-*/;
}
