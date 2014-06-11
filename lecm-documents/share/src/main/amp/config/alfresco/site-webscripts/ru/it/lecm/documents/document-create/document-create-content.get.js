<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
	var widget = {
		name : "LogicECM.module.Documents.Create",
		options : {
			documentType : AlfrescoUtil.param("documentType", "")
		}
	};

	model.widgets = [widget];
}

main();