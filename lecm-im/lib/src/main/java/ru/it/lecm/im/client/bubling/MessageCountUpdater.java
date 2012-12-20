package ru.it.lecm.im.client.bubling;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 18.12.12
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */
public class MessageCountUpdater {
    public static native void Update(int count)/*-{
        $wnd.updateMessagesCount(count);
         }-*/;

}
