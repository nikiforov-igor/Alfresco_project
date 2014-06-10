var newReportNode = rptmanager.buildReportAndSave(args["reportCode"], args["destFolderRef"], args);
model.success = newReportNode != null;
if (newReportNode) {
    model.reportRef = newReportNode.getNodeRef().toString();
    model.reportName = newReportNode.properties["cm:name"];
}
