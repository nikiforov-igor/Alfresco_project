if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.AssociationComplexControl = LogicECM.module.AssociationComplexControl || {};

(function () {
    LogicECM.module.AssociationComplexControl.getExSearchQuery = function (scope) {
        var exSearchFilter = '';
        if (scope.widgets.exSearch && scope.currentState.exSearchFormId) {
            var currentForm = Dom.get(scope.currentState.exSearchFormId);
            if (currentForm) {
                var nameValue, innValue, kppValue;

                nameValue = scope._getInputValue(currentForm, "prop_lecm-contractor_fullname");
                if (nameValue) {
                    exSearchFilter += '(@lecm\\-contractor\\:fullname:' + scope._applySearchSettingsToTerm(nameValue, 'CONTAINS')
                        + ' OR @lecm\\-contractor\\:shortname:' + scope._applySearchSettingsToTerm(nameValue, 'CONTAINS') + ')';
                }

                innValue = scope._getInputValue(currentForm, "prop_lecm-contractor_INN");
                if (innValue) {
                    if (exSearchFilter) {
                        exSearchFilter += " AND ";
                    }
                    exSearchFilter += '=@lecm\\-contractor\\:INN:' + scope._applySearchSettingsToTerm(innValue, 'MATCHES');
                }

                kppValue = scope._getInputValue(currentForm, "prop_lecm-contractor_KPP");
                if (kppValue) {
                    if (exSearchFilter) {
                        exSearchFilter += " AND ";
                    }
                    exSearchFilter += '=@lecm\\-contractor\\:KPP:' + scope._applySearchSettingsToTerm(kppValue, 'MATCHES');
                }
            }
        }
        return exSearchFilter;
    };
})();