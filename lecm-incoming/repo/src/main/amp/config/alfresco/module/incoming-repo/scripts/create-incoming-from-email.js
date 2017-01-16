if (document.hasAspect("cm:emailed")) {
    /*Создаем входящий*/
    var newIncoming = documentScript.createDocument("lecm-incoming:document", null, null);
    if (newIncoming) {
        /*заполняем свойства*/
        newIncoming.properties["lecm-incoming:is-by-channel"] = true;
        newIncoming.save();

        /*заполняем ассоциации*/
        /*Способ доставки*/
        var types = dictionary.getRecordByParamValue("Способ доставки", "lecm-doc-dic-dm:deliveryMethod-code", "EMAIL");
        if (types && types.length > 0) {
            newIncoming.createAssociation(types[0], "lecm-incoming:delivery-method-assoc");
        }

        /*Представитель и корреспондент*/
        var orginiator = document.properties["cm:originator"];
        if (orginiator) {
            var representatives = dictionary.getRecordByParamValue("Адресанты", "lecm-representative:email", orginiator);
            if (representatives && representatives.length > 0) {
                newIncoming.createAssociation(representatives[0], "lecm-incoming:addressee-assoc");

                var contractors = contractorsRootObject.getContractorsForRepresentative(representatives[0].nodeRef.toString());
                if (contractors && contractors.length > 0) {
                    newIncoming.createAssociation(contractors[0], "lecm-incoming:sender-assoc");
                }
            }
        }

        /*Вложения*/
        var attachments = document.assocs["cm:attachments"];
        if (attachments && attachments.length > 0) {
            for (var i = 0; i < attachments.length; i++) {
                newIncoming.createAssociation(attachments[i], "lecm-document-aspects:attachments-temp-assoc");
            }
        }
    }
}
