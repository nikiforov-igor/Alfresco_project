<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">

function main() {
    var attachments = [];
    var fields;
    var nameSubstituteStrings;

    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");
        var categoryRef = (pars.get("parent").length() > 0)  ? pars.get("parent") : null;

        attachments = documentAttachments.getAttachmentsByCategory(categoryRef);
        fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
        nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
    }

	var result = processResults(attachments, fields, nameSubstituteStrings, 0, attachments.length); // call method from search.lib.js
	if (result != null && result.items != null) {
		for (var i = 0; i < result.items.length; i++) {
			result.items[i].isInnerAttachment = documentAttachments.isInnerAttachment(result.items[i].node);
		}
	}

    model.data = result;
}

main();
