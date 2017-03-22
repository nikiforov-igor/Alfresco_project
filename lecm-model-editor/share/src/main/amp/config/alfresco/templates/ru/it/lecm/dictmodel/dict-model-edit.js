<import resource="classpath:/alfresco/templates/ru/it/lecm/model-editor/isAdmin.js">

(function() {
	// Need to know what type of node this is - document or folder
	var nodeRef = url.args.nodeRef,
		nodeType = 'document',
		fileName = '',
		metadata,
		connector = remote.connect('alfresco'),
		result = connector.get('/slingshot/edit-metadata/node/' + nodeRef.replace(':/', ''));

	if (200 == result.status) {
		metadata = eval('(' + result + ')');
		nodeType = metadata.node.isContainer ? 'folder' : 'document';
		fileName = metadata.node.fileName;
	}
	context.page.properties['nodeRef'] = nodeRef;
	context.page.properties['nodeType'] = nodeType;
	context.page.properties['fileName'] = fileName;
})();
