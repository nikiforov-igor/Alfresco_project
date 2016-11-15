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

    LogicECM.checkDuplicatesForContractor = function (fn, buttonScope, p_form) {
        var fnSubmit = function () {
            if (YAHOO.lang.isFunction(fn) && buttonScope) {
                fn.call(buttonScope);
            }
        };

        if (p_form != null && p_form.validate()) {
            var contractorFull = p_form.getFormData()["prop_lecm-contractor_fullname"];
            var contractorShort = p_form.getFormData()["prop_lecm-contractor_shortname"];
            var contractorINN = p_form.getFormData()["prop_lecm-contractor_INN"];
            var contractorKPP = p_form.getFormData()["prop_lecm-contractor_KPP"];

            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/contractors/hasDuplicate",
                dataObj: {
                    fullName: contractorFull,
                    shortName: contractorShort,
                    inn: contractorINN ? contractorINN : '',
                    kpp: contractorKPP ? contractorKPP : ''
                },
                successCallback: {
                    fn: function (response) {
                        var results = response.json;
                        if (results && results.hasDuplicate) {
                            alert("Имеются дубликаты!");
                            fnSubmit();
                            /*var formId = "contractors-repeats-" + Alfresco.util.generateDomId();
                            var doBeforeDialogShow = function (p_form, p_dialog) {
                                var contId = p_dialog.id + "-form-container";
                                Alfresco.util.populateHTML(
                                    [contId + "_h", "Найдены похожие элементы"]
                                );
                                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                            };

                            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
                            var templateRequestParams = {
                                htmlid: formId,
                                itemKind: 'workflow',
                                itemId: 'activiti$incomingOcSearchRepeats',
                                mode: 'create',
                                submitType: 'json',
                                args: JSON.stringify({
                                    fullName: encodeURIComponent(contractorFull),
                                    shortName: encodeURIComponent(contractorShort),
                                    inn: contractorINN,
                                    kpp: contractorKPP
                                }),
                                showSubmitButton: true,
                                showCancelButton: true
                            };

                            // Using Forms Service, so always create new instance
                            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                            YAHOO.Bubbling.on("beforeFormRuntimeInit", LogicECM.submitFunctionOnBeforeFormRuntimeInit, {
                                form: p_form,
                                fnSubmit: fnSubmit,
                                dialog: createDetails
                            });
                            createDetails.setOptions(
                                {
                                    width: "50em",
                                    templateUrl: templateUrl,
                                    templateRequestParams: templateRequestParams,
                                    actionUrl: null,
                                    destroyOnHide: true,
                                    doBeforeDialogShow: {
                                        fn: doBeforeDialogShow,
                                        scope: this
                                    },
                                    failureMessage: "message.failure"
                                }).show();*/
                        } else {
                            fnSubmit();
                        }
                    },
                    scope: this
                }
            });
        } else {
            fnSubmit();
        }
    }

})();