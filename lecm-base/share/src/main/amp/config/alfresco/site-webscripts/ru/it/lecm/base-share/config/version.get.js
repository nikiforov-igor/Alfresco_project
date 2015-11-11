var connector = remote.connect("alfresco");
var json = connector.get("/lecm/version");
if (json.status == 200) {
    var result = jsonUtils.toObject(json.text);
    model.repoVersion = result.repository;
    model.repoBuild = result.repositoryBuild;
}