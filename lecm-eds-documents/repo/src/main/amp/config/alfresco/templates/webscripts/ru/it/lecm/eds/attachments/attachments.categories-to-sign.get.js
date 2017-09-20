var documentNodeRef = args['documentNodeRef'];
var document = null;

if (documentNodeRef) {
    document = search.findNode(documentNodeRef);
}
var docTypeDictionary = null;
var categoriesOfAttachmentsToSign;
if (document) {
    var typeAssocName = "";
    if (document.typeShort == "lecm-contract:document"){
        typeAssocName ="lecm-contract:typeContract-assoc";
    } else {
        typeAssocName = "lecm-eds-document:document-type-assoc";
    }
    var typeAssoc =  document.assocs[typeAssocName];
    if (typeAssoc && typeAssoc.length > 0) {
        docTypeDictionary = typeAssoc[0];
    }
}
if (docTypeDictionary) {
    categoriesOfAttachmentsToSign = docTypeDictionary.properties["lecm-doc-dic-dt:categories-of-attachments-to-sign"];
}
var allCategories = documentAttachments.getCategories(documentNodeRef);
var categoriesToSign = [];
if (categoriesOfAttachmentsToSign) {
    categoriesToSign = categoriesOfAttachmentsToSign.split(";");
}

var categories = [];
for (var i = 0; i < allCategories.length; i++) {
    for (var j = 0; j < categoriesToSign.length; j++) {
        //проверка категории подписываемых вложений
        if (allCategories[i].name == categoriesToSign[j]) {
            categories.push(allCategories[i]);
            break;
        }
    }
}

if (!categories.length) {
    categories = allCategories;
}

var items = [];
if (categories) {
    for (var i = 0; i < categories.length; i++) {
        var attachments = documentAttachments.getAttachmentsByCategory(categories[i]);
        var showAttachments = [];
        for (var j = 0; j < attachments.length; j++) {
            showAttachments.push(attachments[j]);
        }

        if (showAttachments && showAttachments.length) {
            items.push({
                category: {
                    node: categories[i],
                    isReadOnly: documentAttachments.isReadonlyCategory(categories[i].nodeRef)
                },
                attachments: showAttachments
            });
        }
    }
}

model.items = items;
model.isMlSupported = lecmMessages.isMlSupported();