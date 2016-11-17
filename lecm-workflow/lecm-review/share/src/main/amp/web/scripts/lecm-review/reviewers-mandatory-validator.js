if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Incoming = LogicECM.module.Incoming || {};

LogicECM.module.Incoming.reviewerMandatory = function (field, args, event, form, silent, message) {
    var Dom = YAHOO.util.Dom;

    /* В field передали поле с id ...-reviewers-picker-items, чтобы на него навесить pop-up с ошибкой,
    а на наличие элементов проверяем field с id ...-reviewers */
    var fieldForValidation = Dom.get(field.id.replace('reviewers-picker-items', 'reviewers'));

    if (!fieldForValidation) {
        console.log('Error: reviewer mandatory validator : field for validate not found!');
        return false;
    }

    return YAHOO.lang.trim(fieldForValidation.value).length !== 0;
};
