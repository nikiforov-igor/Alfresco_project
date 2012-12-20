package ru.it.lecm.im.client;

import com.google.gwt.core.client.EntryPoint;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 07.12.12
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class LecmIM implements EntryPoint {

    private iJab ijab;

    public void onModuleLoad() {
        Log.consoleLog("Share module EntryPoint ");
        ijab = new iJab();
        ijab.onModuleLoad();

    }
}
