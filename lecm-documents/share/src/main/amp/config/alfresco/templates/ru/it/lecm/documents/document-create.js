function main() {
	if (page.url.args.documentType == "lecm-events:document") {
		var response;
		var requestContext = context.getRequestContext();
		if (requestContext.isPassiveMode()) {
			response = requestContext.getResponse().getResponse();
		} else {
			response = requestContext.getResponse();
		}
		response.sendRedirect(page.url.servletContext + '/event-create?documentType=' + page.url.args.documentType + "&p1=" + page.url.args.p1 + "&p2=" + page.url.args.p2);
	}
}

main();