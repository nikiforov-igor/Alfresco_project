
function main() {
    var params = {};
    if (json != null && json.has("params")) {
        var pars = json.get("params");

        var nodeRef = pars.get("parent");
        var node = search.findNode(nodeRef);

        var modelName = node.properties["cm:name"].replace("_", ":");

        var categories = documentAttachments.getCategoriesForType(modelName);

        var children = node.childByNamePath("statuses").getChildren();
        var responseCategories = [];
        for each (var status in children) {
            var statusName = status.properties["cm:name"]
            var categoriesFolder = status.childByNamePath("categories");
            if (categoriesFolder == null) {
                categoriesFolder = status.createNode("categories", "cm:folder", "cm:contains");
            }

            for (var i = 0; i < categories.length; i++) {
                var category = categories[i];
                var categoryNode = categoriesFolder.childByNamePath(category);
                if (categoryNode == null) {
                    categoryNode = categoriesFolder.createNode(category, "lecm-stmeditor:documentField", "cm:contains");
                    categoryNode.properties["lecm-stmeditor:editableField"] = false;
                    categoryNode.save();
                }
                if (responseCategories[category] == null) {
                    responseCategories[category] = [];
                }
                responseCategories[category][statusName] = {
                    fieldNodeRef: categoryNode.nodeRef.toString(),
                    editableField: categoryNode.properties["lecm-stmeditor:editableField"]
                };
            }
        }

        var result = [];
        for (var categoryName in responseCategories) {
            var fields =  [];
            fields.push({
                fieldName: "prop_form_field",
                value: categoryName,
                displayValue: categoryName
            })

            for (var categoryStatus in responseCategories[categoryName]) {
                fields.push({
                    fieldName: "prop_" + categoryStatus,
                    displayValue: responseCategories[categoryName][categoryStatus].editableField,
                    value: responseCategories[categoryName][categoryStatus].fieldNodeRef
                })
            }
            result.push(fields);
        }
        model.result = result;
    }
}

main();
