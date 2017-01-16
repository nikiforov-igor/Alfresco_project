/*Создаем входящий*/
var newIncoming = documentScript.createDocument("lecm-incoming:document", null, null);
if (newIncoming) {
    /*заполняем свойства*/
    newIncoming.properties["lecm-incoming:is-by-channel"] = true;
    newIncoming.save();

    /*заполняем ассоциации*/
    /*Вложения*/
    newIncoming.createAssociation(document, "lecm-document-aspects:attachments-temp-assoc");
}