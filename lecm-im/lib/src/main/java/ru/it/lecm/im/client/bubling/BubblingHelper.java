package ru.it.lecm.im.client.bubling;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 12.12.12
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */
public class BubblingHelper {



    public final static native JavaScriptObject getSubscriber()
        /*-{
            return $wnd.iBubblingSubscriber;
        }-*/;

    public static void Subscribe(IToggleWindow callback )
    {
        SubscribeNative( getSubscriber(), callback );
    }

    // https://developers.google.com/web-toolkit/doc/latest/DevGuideCodingBasicsJSNI?hl=ru
    // Coding Basics - JavaScript Native Interface (JSNI)

    private static native void SubscribeNative(JavaScriptObject subscriber,  IToggleWindow callback )
        /*-{
            console.log("SubscribeNative ");
            ToggleWindow = function(result)
            {
                callback.@ru.it.lecm.im.client.bubling.IToggleWindow::ToggleWindow(Z)(result);
            };
            subscriber(ToggleWindow);
        }-*/;

}
