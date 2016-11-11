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
                var nameValue, innValue, numberValue, addressValue, regionValue;

                nameValue = scope._getInputValue(currentForm, "prop_lecm-contractor_lastName");
                if (nameValue) {
                    exSearchFilter += '(@lecm\\-contractor\\:lastName:' + scope._applySearchSettingsToTerm(nameValue, 'CONTAINS')
                        + ' OR @lecm\\-contractor\\:firstName:' + scope._applySearchSettingsToTerm(nameValue, 'CONTAINS')
                        + ' OR @lecm\\-contractor\\:middleName:' + scope._applySearchSettingsToTerm(nameValue, 'CONTAINS') + ')';
                }

                innValue = scope._getInputValue(currentForm, "prop_lecm-contractor_INN");
                if (innValue) {
                    if (exSearchFilter) {
                        exSearchFilter += " AND ";
                    }
                    exSearchFilter += '=@lecm\\-contractor\\:INN:' + scope._applySearchSettingsToTerm(innValue, 'MATCHES');
                }

                numberValue = scope._getInputValue(currentForm, "prop_lecm-contractor_document-number");
                if (numberValue) {
                    if (exSearchFilter) {
                        exSearchFilter += " AND ";
                    }
                    exSearchFilter += '=@lecm\\-contractor\\:document\\-number:' + scope._applySearchSettingsToTerm(numberValue, 'MATCHES');
                }

                addressValue = scope._getInputValue(currentForm, "prop_lecm-contractor_physical-address");
                if (addressValue) {
                    if (exSearchFilter) {
                        exSearchFilter += " AND ";
                    }
                    exSearchFilter += '@lecm\\-contractor\\:physical\\-address:' + scope._applySearchSettingsToTerm(addressValue, 'CONTAINS');
                }

                regionValue = scope._getInputValue(currentForm, "assoc_lecm-contractor_region-association");
                if (regionValue) {
                    if (exSearchFilter) {
                        exSearchFilter += " AND ";
                    }
                    exSearchFilter += '@lecm\\-contractor\\:region\\-association\\-ref:' + scope._applySearchSettingsToTerm(regionValue, 'MATCHES');
                }
            }
        }
        return exSearchFilter;
    };
})();