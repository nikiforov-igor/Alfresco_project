if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Contracts = LogicECM.module.Contracts || {};


LogicECM.module.Contracts.stageDateRangeValidator =
    function stageDateRangeValidator(field, args,  event, form, silent, message) {
        var startDateField = field.form["prop_lecm-contract-table-structure_start-date"];
        var endDateField = field.form["prop_lecm-contract-table-structure_end-date"];

        if (startDateField && startDateField.value && endDateField && endDateField.value) {
            var startDate = Alfresco.util.fromISO8601(startDateField.value);
            var endDate =  Alfresco.util.fromISO8601(endDateField.value);
            return startDate.getTime() <= endDate.getTime();
        }
        return true;
    };
