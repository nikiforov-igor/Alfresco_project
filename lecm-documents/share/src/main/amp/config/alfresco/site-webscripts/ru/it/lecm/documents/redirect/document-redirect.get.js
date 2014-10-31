<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

(function() {
	var redirectUrl,
		nodeRef,
		noRedirect,
		result,
		redirectInfo,
		requestContext,
		response,
		redirectContext;

	//если текущий пользователь является админом, то редирект не делаем
	if (user.isAdmin) {
		return;
	}
	nodeRef = args['nodeRef'];
	noRedirect = 'true' == '' + args['noRedirect'];
	//если передан параметр noRedirect=true, то редирект не делаем
	if (noRedirect) {
		return;
	}
	//обращаемся в вебскрипт из repo и получаем информацию о редиректе
	result = remote.connect("alfresco").get('/lecm/documents/redirect/info?nodeRef=' + nodeRef);
	if (result.status != 200) {
		AlfrescoUtil.error(result.status, 'Could not get redirect details for node ' + nodeRef);
	}
	redirectInfo = eval('(' + result + ')');
	//далее смотрим нужен ли редирект и делаем его по указанному url-у
	if (redirectInfo.redirect) {
		switch (redirectInfo.type) {
			case 'SYSTEM_DOCUMENT':
				redirectContext = '/document-system';
				break;
			case 'DOCUMENT_ATTACHMENT':
				redirectContext = '/document-attachment';
				break;
			case 'BASE_DOCUMENT':
				redirectContext = '/document';
				break;
		}
		requestContext = context.getRequestContext();
		if (requestContext.isPassiveMode()) {
			response = requestContext.getResponse().getResponse();
		} else {
			response = requestContext.getResponse();
		}
		redirectUrl = url.servletContext + redirectContext + '?nodeRef=' + redirectInfo.nodeRef;
		response.sendRedirect(redirectUrl);
	}
})();
