if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};
LogicECM.module.OrgStructure.Absence = LogicECM.module.OrgStructure.Absence || {};


LogicECM.module.OrgStructure.Absence.noderefsHasNoAbsenceValidation =
    function Absence_nodeRefsHasNoAbsenceValidation(field, args,  event, form, silent, message) {

        // Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
        var htmlNode = YAHOO.util.Dom.get(field.id + "error-message-container");
        if (htmlNode){
            htmlNode.innerHTML = "";
        }
        else{
            var fld = YAHOO.util.Dom.get(field.id);
            var newDiv = document.createElement("div");
            newDiv.innerHTML = '<div id="' + field.id + 'error-message-container" style="margin-left: 12em; color: #800000; font-weight: bold;"></div>';
            fld.parentNode.appendChild(newDiv);
            htmlNode = YAHOO.util.Dom.get(field.id + "error-message-container");
        }

        var fieldAdded = YAHOO.util.Dom.get(field.id + "-cntrl-added");
        var fieldRemoved = YAHOO.util.Dom.get(field.id + "-cntrl-removed");

        if (!fieldAdded.value && !fieldRemoved.value)
        {
            return true;
        }

        var nodeRefs = [];

        if (fieldAdded.value){
            nodeRefs.push( fieldAdded.value.split(","));
        }

        if (fieldRemoved.value){
            nodeRefs.push( fieldRemoved.value.split(",") );
        }


        var valid = false;

        var nodeRefsJoined = nodeRefs.join(",");

        // Yahoo UI не умеет синхронный (блокирующий) AJAX. Придется использовать jQuery
        jQuery.ajax({
            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/orgstructure/api/nodeRefsHasNoAbsences",
            type: "POST",
            timeout: 30000, // 30 секунд таймаута хватит всем!
            async: false, // ничего не делаем, пока не отработал запром
            dataType: "json",
            contentType: "application/json",
            data: YAHOO.lang.JSON.stringify({ nodeRef: nodeRefsJoined  }), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
            processData: false, // данные не трогать, не кодировать вообще
            success: function (result, textStatus, jqXHR) {
                if (result && result.hasNoActiveAbsences) {
                    valid = true;
                } else {
                    if (result && result.reason){

                        htmlNode.innerHTML = result.reason.split('\n').join("<br>");
                    }
                    valid = false;
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                Alfresco.util.PopupManager.displayMessage({
                    text: "ERROR: can not perform field validation"
                });
                valid = false;
            }
        });

        return valid;
    };
