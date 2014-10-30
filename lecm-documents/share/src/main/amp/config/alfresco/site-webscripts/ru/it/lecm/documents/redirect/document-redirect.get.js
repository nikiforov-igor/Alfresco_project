(function(){
//	logger.log('Getting response...')
	var nodeRef = args['nodeRef'];
	var noRedirect = 'true' == '' + args['noRedirect'];
//	logger.log('nodeRef = ' + nodeRef);
//	logger.log('noRedirect = ' + noRedirect);
	var requestContext = context.getRequestContext();
	var response;
	if (requestContext.isPassiveMode()) {
		response = requestContext.getResponse().getResponse();
	} else {
		response = requestContext.getResponse();
	}
	var queryString = url.queryString;
//	logger.log('Query string = ' + queryString);
	var servletContext = url.servletContext;
//	logger.log('Servlet context = ' + servletContext);
	var redirectUrl = servletContext + '/document?' + queryString;
//	response.sendRedirect(redirectUrl);
})();
