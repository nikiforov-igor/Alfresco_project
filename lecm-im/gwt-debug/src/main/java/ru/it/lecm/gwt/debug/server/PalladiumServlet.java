package ru.it.lecm.gwt.debug.server;

import ru.it.lecm.im.bosh.PalladiumLogic;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Akatamanov
 * Date: 09.01.13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class PalladiumServlet extends HttpServlet {

//    private final PalladiumLogic logic = new PalladiumLogic();
    private PalladiumLogic logic;

	private final static Logger logger = LoggerFactory.getLogger (PalladiumServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
		try {
			logger.info ("logic = new PalladiumLogic()");
			logic = new PalladiumLogic();
			logger.info ("logic.init()");
			logic.init();
			logger.info ("PalladiumLogic successfully created and inited");
		} catch (Exception ex) {
			logger.error (ex.getMessage (), ex);
		}
    }

    @Override
    public void destroy() {
        super.destroy();
        logic.destroy();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
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
