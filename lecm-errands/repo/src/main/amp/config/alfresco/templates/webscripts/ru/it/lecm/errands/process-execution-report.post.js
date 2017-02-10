<import resource="classpath:/alfresco/module/errands-repo/workflow/scripts/errands-execute-errand-script.js">

var document = search.findNode(args['nodeRef']);
var requestContent = eval("(" + requestbody.getContent() + ")");
var isExecute = false;
var closeChild = false;
var connectedDocuments = [];
var attachments = [];
var reportText = "";
if (requestContent) {
    if (requestContent.hasOwnProperty("prop_lecm-errands-ts_execution-report-is-execute")) {
        isExecute = requestContent["prop_lecm-errands-ts_execution-report-is-execute"] == "true";
    }
    if (requestContent.hasOwnProperty("prop_lecm-errands-ts_execution-close-child")) {
        closeChild = requestContent["prop_lecm-errands-ts_execution-close-child"] == "true";
    }
    if (requestContent.hasOwnProperty("prop_lecm-errands-ts_execution-report-text")) {
        reportText = requestContent["prop_lecm-errands-ts_execution-report-text"];
    }
    if (requestContent.hasOwnProperty("assoc_lecm-errands-ts_execution-report-connected-document-assoc")) {
        connectedDocuments = requestContent["assoc_lecm-errands-ts_execution-report-connected-document-assoc"].split(",").map(function (ref) {
            return search.findNode(ref);
        });
    }
    if (requestContent.hasOwnProperty("assoc_lecm-errands-ts_execution-report-attachment-assoc")) {
        attachments = requestContent["assoc_lecm-errands-ts_execution-report-attachment-assoc"].split(",").map(function (ref) {
            return search.findNode(ref);
        });
    }
    if (document) {
        if (document.properties["lecm-errands:execution-report-status"] != "PROJECT") {
            document.properties["lecm-errands:execution-report-create-date"] = new Date();
        }
        ExecuteErrandScript.fillExecutionReport(document, attachments, connectedDocuments, reportText, closeChild);
        if (!isExecute) {
            document.properties["lecm-errands:execution-report-status"] = "PROJECT";
            document.properties["lecm-errands:project-report-text"] = reportText;
            document.properties["lecm-errands:project-report-attachment"] = attachments.map(function (a) {
                return a.nodeRef.toString();
            }).join();
            document.properties["lecm-errands:project-report-connections"] = connectedDocuments.map(function (a) {
                return a.nodeRef.toString();
            }).join();
        } else {
            ExecuteErrandScript.executeErrand(document, closeChild);
            document.properties["lecm-errands:project-report-text"] = null;
            document.properties["lecm-errands:project-report-attachment"] = null;
            document.properties["lecm-errands:project-report-connections"] = null;
        }
        document.save();
        model.success = true;
    }
}
