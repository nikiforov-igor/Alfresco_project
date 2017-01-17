if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Errands = LogicECM.module.Errands || {};

LogicECM.module.Errands.limitationDateValidation = function (field, args,  event, form, silent, message) {
    var getDate = function(argString) {
        var dateString = argString ? argString.split('T')[0] : '';
        return new Date(dateString);
    };

    if (field.value && field.value.length) {
        var foundDatePickers = Alfresco.util.ComponentManager.find({id : this.fieldId + '-cntrl'});
        if (foundDatePickers && foundDatePickers.length == 1) {
            var datePicker = foundDatePickers[0];
            if (datePicker.options && datePicker.options.maxLimit) {
                var curSelectedDate = getDate(field.value);
                var maxLimitDate = getDate(datePicker.options.maxLimit);
                if (curSelectedDate && maxLimitDate) {
                    return curSelectedDate <= maxLimitDate;
                }
            }
        }
    }
    return true;
};