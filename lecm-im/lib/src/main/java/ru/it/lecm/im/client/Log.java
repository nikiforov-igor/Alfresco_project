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


        if(!window.iJabLoggerEnabled){
            window.imLog = function (){

            };

            return;
        }

        if (window.Alfresco && window.Alfresco.logger) {
            window.imLog = function (message) {
                Alfresco.logger.trace("IMLog: " + message);
            };
        } else {

            if (!window.console) {
                window.console = {
                    log: function () {
                    }
                };
            }

            window.imLog = function (message) {
                console.log("IMLog: " + message);
            };
        }

    }-*/;

    public static void log(String message)
    {
        myLog(message);
    }

    public static native void myLog(String message) /*-{
        window.imLog(message);
    }-*/;
}
