var workflowId = args["workflowId"];

var definition = workflow.getDefinitionByName("activiti$" + workflowId);

var documents = [];
for each (var instance in definition.getActiveInstances()) {
    for each (var path in instance.getPaths()) {
        for each (var task in path.getTasks()) {
            var docPackage = search.findNode(task.properties["bpm:package"]);
            if (docPackage != null) {
                for each (var document in docPackage.getChildren()) {
                    documents.push(document);
                }
            }
        }
    }
}

jsonDocuments = []

for each (var document in documents) {
    jsonDocuments.push({
        nodeRef: document.nodeRef.toString(),
        name: document.getName(),
        status: document.properties["lecm-workflow:status"]
    });
}

model.documents = jsonDocuments;