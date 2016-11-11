if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.AssociationComplexControl = LogicECM.module.AssociationComplexControl || {};

(function () {
    var ExtSearchUtils = LogicECM.module.AssociationComplexControl.ExtSearch;

    if (ExtSearchUtils) {
        ExtSearchUtils.registerQueryFunction('contractor', function (currentForm) {
            var exSearchFilter = '';
            var nameValue, innValue, kppValue;

            nameValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_fullname");
            if (nameValue) {
                exSearchFilter += '(@lecm\\-contractor\\:fullname:' + ExtSearchUtils.applySearchSettingsToTerm(nameValue, 'CONTAINS')
                    + ' OR @lecm\\-contractor\\:shortname:' + ExtSearchUtils.applySearchSettingsToTerm(nameValue, 'CONTAINS') + ')';
            }

            innValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_INN");
            if (innValue) {
                if (exSearchFilter) {
                    exSearchFilter += " AND ";
                }
                exSearchFilter += '=@lecm\\-contractor\\:INN:' + ExtSearchUtils.applySearchSettingsToTerm(innValue, 'MATCHES');
            }

            kppValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_KPP");
            if (kppValue) {
                if (exSearchFilter) {
                    exSearchFilter += " AND ";
                }
                exSearchFilter += '=@lecm\\-contractor\\:KPP:' + ExtSearchUtils.applySearchSettingsToTerm(kppValue, 'MATCHES');
            }
            return exSearchFilter;
        });

        ExtSearchUtils.registerArgsFunction('contractor', function (args, currentForm) {
            var nameValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_fullname");
            if (nameValue) {
                args["prop_lecm-contractor_shortname"] = nameValue;
            }
            return args;
        });

        ExtSearchUtils.registerQueryFunction('organization', ExtSearchUtils.extendQueryFunctions['contractor']);
        ExtSearchUtils.registerArgsFunction('organization', ExtSearchUtils.extendArgsFunctions['contractor']);
    }
})();