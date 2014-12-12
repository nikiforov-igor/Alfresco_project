if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.constraints = LogicECM.constraints || {};

LogicECM.constraints.notMandatory = function(field, args, event, form, silent, message) {
    return true;
};
