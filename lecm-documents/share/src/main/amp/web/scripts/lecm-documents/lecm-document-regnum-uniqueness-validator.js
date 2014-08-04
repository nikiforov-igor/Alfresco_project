if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Documents = LogicECM.module.Documents || {};

LogicECM.module.Documents.regnumUniqueness = LogicECM.module.Documents.regnumUniqueness || {};

LogicECM.module.Documents.regnumUniqueness.validator = function (field, args, event, form, silent, message) {
    var valid = false,
        optsObj = {
            regNumber: field.value
        };
    if (LogicECM.module.Documents.regnumUniqueness.cleanValue == null) {
        LogicECM.module.Documents.regnumUniqueness.cleanValue = field.value;
        valid = (field.value.length > 0);
    } else if (LogicECM.module.Documents.regnumUniqueness.cleanValue === field.value) {
        valid = (field.value.length > 0);
    } else if (field.value.length > 0) {
        // Yahoo UI не умеет синхронный (блокирующий) AJAX. Придется использовать jQuery
        jQuery.ajax({
            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/regNumbers/isNumberUnique",
            type: "POST",
            timeout: 30000, // 30 секунд таймаута хватит всем!
            async: false, // ничего не делаем, пока не отработал запрос
            dataType: "json",
            contentType: "application/json",
            data: YAHOO.lang.JSON.stringify(optsObj), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
            processData: false, // данные не трогать, не кодировать вообще
            success: function (result, textStatus, jqXHR) {
                valid = result && result.isNumberUnique;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Alfresco.util.PopupManager.displayMessage({
                    text: "ERROR: can not perform field validation"
                });
            }
        });
    }

    return valid;
};