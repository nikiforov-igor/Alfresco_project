var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var dictionaryService = ctx.getBean("dictionaryService");
var qname = Packages.ru.it.lecm.documents.beans.DocumentService.TYPE_BASE_DOCUMENT;
var types = dictionaryService.getSubTypes(qname, true);
types.remove(qname);

documentTypes = types.toArray();

var models = [];
for each (var type in documentTypes) {
    var typeDef = dictionaryService.getType(type)

	models.push({
        id: type.toPrefixString().replace(":", "_"),
        title: typeDef.getTitle(),
        description: typeDef.getDescription(),
		isActive: true
    });
}

var modelRoot = companyhome.childrenByXPath("app:dictionary/app:models")[0];
var dynamicModels = modelRoot.children;
for (var i = 0 ; i < dynamicModels.length; i++) {
	if (dynamicModels[i].typeShort == "cm:dictionaryModel") {
		var id = dynamicModels[i].name + "NS_" + dynamicModels[i].name;

		var exist = false;
		for (var j = 0; j < models.length; j++) {
			if (id == models[j].id) {
				models[j].nodeRef = "" + dynamicModels[i].nodeRef;
				exist = true;
			}
		}

		if (!exist) {
			var title = dynamicModels[i].properties["cm:modelDescription"];
			if (title == null) {
				title = dynamicModels[i].name;
			}

			models.push({
				nodeRef: "" + dynamicModels[i].nodeRef,
				id: id,
				title: title,
				description: dynamicModels[i].properties["cm:modelDescription"],
				isActive: false
			});
		}
	}
}

model.modelRoot = modelRoot;
model.models = models;