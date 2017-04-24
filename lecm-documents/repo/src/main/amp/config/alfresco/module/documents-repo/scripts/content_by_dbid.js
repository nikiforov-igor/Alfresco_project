/*параметры*/
var errorFolderName = "Сканирование/Ошибки сканирования";
/*выполняем*/
var name = document.properties["cm:name"];
var attempts = 0;
var attached = false;

var dotIndex = name.lastIndexOf(".");
var fName;
var extension;
if (dotIndex != -1) {
    fName = name.substring(0, dotIndex);
    extension = name.substring(dotIndex);
} else {
    fName = name;
    extension = "";
}
var newName = name;

while (!attached && (10 > attempts++)) {
    try {
        logger.log("Attachment moving started. Attempt:" + attempts);
        /*Получаем ID*/
        var dbid = name.split(".")[0].split("_")[0];
        var id = parseInt(dbid, 10);
        if (id > 0) {
            var node = base.getNode(id);
            if (node) {
                /*нашли ноду по ID*/
                if (node.isSubType('lecm-document:base')) {
                    var categoryName;
                    var settingsDictionary = dictionary.getDictionaryByName("Настройки типов документов");
                    var currentDocSettings;
                    if (settingsDictionary) {
                        var settings = dictionary.getChildren(settingsDictionary.nodeRef);
                        currentDocSettings = settings.filter(function (item) {
                            return node.typeShort == item.properties["lecm-doc-dic-type:document-type"];
                        });
                    }
                    if (currentDocSettings && currentDocSettings.length) {
                        categoryName = currentDocSettings[0].properties["lecm-doc-dic-type:attachment-category"];
                    } else {
                        var categories = documentAttachments.getCategoriesForType(node.typeShort);
                        if (categories && categories.length) {
                            categoryName = categories[0];
                        }
                    }
                    if (categoryName) {
                        /*получаем категорию "categoryName"*/
                        var category = documentAttachments.getCategoryByName(categoryName, node);

                        var i = 0;
                        while (category.childByNamePath(newName) != null) {
                            i++;
                            newName = fName + "(" + i + ")" + extension;
                        }
                        if (i > 0) {
                            document.properties["cm:name"] = newName;
                            document.save();
                        }
                        /*перемешаем вложение в категорию "Подлинник"*/
                        documentAttachments.addAttachment(document, category);

                        /*увеличиваем счетчик отсканированных документов*/
                        var saCount = node.properties["lecm-document-aspects:scanned-attachments-count"];
                        if (saCount) {
                            saCount++;
                        } else {
                            saCount = 1;
                        }
                        node.properties["lecm-document-aspects:scanned-attachments-count"] = saCount;
                        node.save();
                        attached = true;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
    } catch(e){
        attached = false;
        if (e.javaException) {
            logger.log(e.javaException.getMessage());
            e.javaException.printStackTrace();
        }

        if (e.rhinoException) {
            logger.log(e.rhinoException.getMessage());
            logger.log(e.rhinoException.details());
        }

    } finally {
        logger.log("Attachment moving finished. Attached: " + attached);
    }
}

if (!attached) {
    /*Обрабатываем ошибку*/
    logger.log("Failed to attach file");

    newName = errorFolderName + "/" + name;
    i = 0;
    while (companyhome.childByNamePath(newName) != null) {
        i++;
        newName = errorFolderName + "/" + fName + "(" + i + ")" + extension;
    }
    if (i > 0) {
        document.properties["cm:name"] = fName + "(" + i + ")" + extension;
        document.save();
    }
    var props = [];
    props["cm:isIndexed"] = false;
    document.addAspect("cm:indexControl", props);

    logger.log("FolderName = " + errorFolderName);
    base.moveNode(document, errorFolderName);
}