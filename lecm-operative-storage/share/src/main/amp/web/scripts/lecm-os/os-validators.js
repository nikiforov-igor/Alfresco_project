if (typeof LogicECM === 'undefined' || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.OS = LogicECM.module.OS || {};

LogicECM.module.OS.isCorrectYear = function (field) {
    if (!field) {
        return false;
    }
    return field.value.length == 4;
};

LogicECM.module.OS.isOrganizationCentralized = function (field) {
    var valid = true;
    if (!LogicECM.Nomenclature.isCentralized) {
        valid = field.value.length > 0;
    }
    return valid;
};

LogicECM.module.OS.uniqueYear = function (field, args, event, form, silent, message) {
    var valid = true;

    if (field.form["assoc_os-aspects_nomenclature-organization-assoc"]
        && field.form["assoc_os-aspects_nomenclature-organization-assoc"].value.length > 0
        && field.value.length > 0) {

        var nodeRef = args != null ? args.nodeRef : null;

        var validationUrl = Alfresco.constants.PROXY_URI_RELATIVE
            + "lecm/os/nomenclature/isYearUniq?year=" + field.value
            + "&orgNodeRef=" + field.form["assoc_os-aspects_nomenclature-organization-assoc"].value;
        if (nodeRef) {
            validationUrl += "&nodeRef=" + nodeRef;
        }

        jQuery.ajax({
            url: validationUrl,
            type: "GET",
            timeout: 30000,
            async: false,
            dataType: "json",
            contentType: "application/json",
            processData: false,
            success: function (result) {
                if (result != null) {
                    valid = result.uniq;
                }
            },
            error: function () {
                valid = false;
            }
        });
    }

    return valid;
};