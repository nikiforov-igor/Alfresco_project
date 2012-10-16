var json = remote.call("/lecm/workflow/documents?workflowId=contractWorkflow");

if (json.status == 200) {
    obj = eval("(" + json + ")");
    model.documents = obj;
}
