if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.AssociationComplexControl = LogicECM.module.AssociationComplexControl || {};

(function () {
    var ExtSearchUtils = LogicECM.module.AssociationComplexControl.ExtSearch;

    LogicECM.getExtQueryForContractor = function (currentForm) {
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
    };

    LogicECM.getExtQueryForPerson = function (currentForm) {
        var exSearchFilter = '';
        var nameValue, innValue, numberValue, addressValue, regionValue;

        nameValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_lastName");
        if (nameValue) {
            exSearchFilter += '(@lecm\\-contractor\\:lastName:' + ExtSearchUtils.applySearchSettingsToTerm(nameValue, 'CONTAINS')
                + ' OR @lecm\\-contractor\\:firstName:' + ExtSearchUtils.applySearchSettingsToTerm(nameValue, 'CONTAINS')
                + ' OR @lecm\\-contractor\\:middleName:' + ExtSearchUtils.applySearchSettingsToTerm(nameValue, 'CONTAINS') + ')';
        }

        innValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_INN");
        if (innValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '=@lecm\\-contractor\\:INN:' + ExtSearchUtils.applySearchSettingsToTerm(innValue, 'MATCHES');
        }

        numberValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_document-number");
        if (numberValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '=@lecm\\-contractor\\:document\\-number:' + ExtSearchUtils.applySearchSettingsToTerm(numberValue, 'MATCHES');
        }

        addressValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_physical-address");
        if (addressValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '@lecm\\-contractor\\:physical\\-address:' + ExtSearchUtils.applySearchSettingsToTerm(addressValue, 'CONTAINS');
        }

        regionValue = ExtSearchUtils.getInputValue(currentForm, "assoc_lecm-contractor_region-association");
        if (regionValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '@lecm\\-contractor\\:region\\-association\\-ref:' + ExtSearchUtils.applySearchSettingsToTerm(regionValue, 'MATCHES');
        }
        return exSearchFilter;
    };

    LogicECM.getExtArgsForContractor = function (currentForm) {
        var args = ExtSearchUtils.getArgsFromForm(currentForm);
        var nameValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_fullname");
        if (nameValue) {
            args["prop_lecm-contractor_shortname"] = nameValue;
        }
        return args;
    };

    LogicECM.getExtArgsForPerson = function (currentForm) {
        var args = ExtSearchUtils.getArgsFromForm(currentForm);
        var addrValue = ExtSearchUtils.getInputValue(currentForm, "prop_lecm-contractor_physical-address");
        if (addrValue) {
            args["prop_lecm-contractor_physical-address"] = addrValue;
        }
        return args;
    };

    LogicECM.checkDuplicatesForContractor = function (fn, scope) {
        alert("Test Duplicate");
        fn.call(scope);
    }

})();