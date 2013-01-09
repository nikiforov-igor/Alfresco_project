package ru.it.lecm.gwt.debug.server;

import ru.it.lecm.im.bosh.PalladiumLogic;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 09.01.13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class PalladiumServlet extends HttpServlet {

    private final PalladiumLogic logic = new PalladiumLogic();;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);    //To change body of overridden methods use File | Settings | File Templates.
        logic.init();
    }

    @Override
    public void destroy() {
        super.destroy();    //To change body of overridden methods use File | Settings | File Templates.
        logic.destroy();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);    //To change body of overridden methods use File | Settings | File Templates.
        logic.doOptions(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logic.doPost(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logic.doGet(request, response);
    }
}
