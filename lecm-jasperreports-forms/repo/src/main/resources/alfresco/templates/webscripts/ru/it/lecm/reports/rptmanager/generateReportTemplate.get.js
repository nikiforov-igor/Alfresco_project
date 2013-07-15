var newTemplate = rptmanager.generateReportTemplate(args["reportRef"]);
model.success = newTemplate != null;
if (newTemplate) {
    model.templateRef = newTemplate.getNodeRef().toString();
}
