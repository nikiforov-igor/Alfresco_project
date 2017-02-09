<import resource="classpath:/alfresco/module/errands-repo/workflow/scripts/errands-execute-errand-script.js">

var document = search.findNode(args['nodeRef']);
var requestContent = eval("(" + requestbody.getContent() + ")");
var isExecute = false;
var closeChild = false;
var connectedDocuments = [];
var attachments = [];

if (requestContent) {

    if (requestContent.hasOwnProperty("prop_lecm-errands_execution-report-is-execute")) {
        isExecute = requestContent["prop_lecm-errands_execution-report-is-execute"];
    }
    if (requestContent.hasOwnProperty("prop_lecm-errands_execution-report-close-child")) {
        closeChild = requestContent["prop_lecm-errands_execution-report-close-child"];
    }
    if (requestContent.hasOwnProperty("assoc_lecm-errands_execution-connected-document-assoc")) {
        connectedDocuments = requestContent["assoc_lecm-errands_execution-connected-document-assoc"]
    }
    if (requestContent.hasOwnProperty("assoc_lecm-errands_execution-report-attachment-assoc")) {
        attachments = requestContent["assoc_lecm-errands_execution-report-attachment-assoc"];
    }
    if (document) {
        ExecuteErrandScript.fillExecutionReport(document, attachments, connectedDocuments, reportText, closeChild);
        if (!isExecute) {
            document.properties["lecm-errands:execution-report-status"] = "PROJECT";
        } else {
            ExecuteErrandScript.executeErrand(document, closeChild);
        }
        document.save();
        model.isExecute = isExecute;
    }
}
