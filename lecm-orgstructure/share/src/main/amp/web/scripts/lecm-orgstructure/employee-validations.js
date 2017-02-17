if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};
LogicECM.module.OrgStructure.Absence = LogicECM.module.OrgStructure.Absence || {};


LogicECM.module.OrgStructure.Absence.employeeHasNoAbsenceValidation =
    function Absence_employeeHasNoAbsenceValidation(field, args,  event, form, silent, message) {

        // Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
        var htmlNode = YAHOO.util.Dom.get("error-message-container");
        if (htmlNode){
            htmlNode.innerHTML = "";
        }
        else{
            var formDom = YAHOO.util.Dom.get(field.id);
            var newDiv = document.createElement("div");
            newDiv.innerHTML = '<div id="error-message-container" class="employee-absence-error"></div>';
            formDom.parentNode.parentNode.appendChild(newDiv);
            htmlNode = YAHOO.util.Dom.get("error-message-container");
        }

        if (!field.value)
        {
            return false;
        }

        var valid = false;

        // Yahoo UI не умеет синхронный (блокирующий) AJAX. Придется использовать jQuery
        jQuery.ajax({
            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/orgstructure/api/employeeHasNoAbsences",
            type: "POST",
            timeout: 30000, // 30 секунд таймаута хватит всем!
            async: false, // ничего не делаем, пока не отработал запром
            dataType: "json",
            contentType: "application/json",
            data: YAHOO.lang.JSON.stringify({ nodeRef: field.value }), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
            processData: false, // данные не трогать, не кодировать вообще
            success: function (result, textStatus, jqXHR) {
                if (result && result.hasNoActiveAbsences) {
                    valid = true;
                } else {
                    if (result && result.reason){
                        htmlNode.innerHTML += message;
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

LogicECM.module.OrgStructure.employeeNotHasAnotherOrganizationValidation = function (field, args,  event, form, silent, message) {
    var valid = false;
    var employeeRef = field.value;
    var employeeOrganization = null;

    if (employeeRef.length > 0) {
        jQuery.ajax({
            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/orgstructure/getOrganization?nodeRef=" + employeeRef,
            type: "GET",
            timeout: 10000,
            async: false,
            dataType: "json",
            contentType: "application/json",
            processData: true,

            success: function(result, textStatus, jqXHR) {
                employeeOrganization = result.nodeRef;
                if (employeeOrganization) {
                    jQuery.ajax({
                        url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/orgstructure/api/getStaffPositionUnit?nodeRef=" + field.form["alf_destination"].value,
                        type: "GET",
                        timeout: 10000,
                        async: false,
                        dataType: "json",
                        contentType: "application/json",
                        processData: true,

                        success: function(result, textStatus, jqXHR) {
                            var staffUnit = result.nodeRef;
                            jQuery.ajax({
                                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/orgstructure/getOrganization?nodeRef=" + staffUnit,
                                type: "GET",
                                timeout: 10000,
                                async: false,
                                dataType: "json",
                                contentType: "application/json",
                                processData: true,

                                success: function(result, textStatus, jqXHR) {
                                    var unitOrganization = result.nodeRef;
                                    valid = !unitOrganization || (unitOrganization && (employeeOrganization == unitOrganization));
                                },

                                error: function(jqXHR, textStatus, errorThrown) {
                                    valid = false;
                                    Alfresco.util.PopupManager.displayMessage({
                                        text: "ERROR: can not perform field validation"
                                    });
                                }
                            });
                        },

                        error: function(jqXHR, textStatus, errorThrown) {
                            valid = false;
                            Alfresco.util.PopupManager.displayMessage({
                                text: "ERROR: can not perform field validation"
                            });
                        }
                    });
                } else {
                    valid = true;
                }
            },

            error: function(jqXHR, textStatus, errorThrown) {
                valid = false;
                Alfresco.util.PopupManager.displayMessage({
                    text: "ERROR: can not perform field validation"
                });
            }
        });
    }

    return valid;
};
