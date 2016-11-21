(function () {
    function getInputValue(form, propName) {
        var value = null;
        var inputName = form[propName];
        if (inputName != null) {
            value = inputName.value;
            value = YAHOO.lang.trim(value);
        }
        return value;
    }

    function getArgsFromForm(currentForm) {
        var args = {};
        for (var i = 0; i < currentForm.elements.length; i++) {
            var element = currentForm.elements[i],
                propName = element.name,
                propValue = YAHOO.lang.trim(element.value);

            if (propName && (propName.indexOf("prop_") == 0 || propName.indexOf("assoc_") == 0)) {
                if (propValue) {
                    args[propName] = propValue;
                }
            }
        }
        return args;
    }

    function applySearchSettingsToTerm(searchTerm, searchSettings) {
        var decoratedTerm;

        searchTerm = escape(searchTerm);

        switch (searchSettings) {
            case 'BEGINS':
                decoratedTerm = searchTerm + '*';
                break;
            case 'ENDS':
                decoratedTerm = '*' + searchTerm;
                break;
            case 'CONTAINS':
                decoratedTerm = '*' + searchTerm + '*';
                break;
            case 'MATCHES':
                decoratedTerm = searchTerm;
                break;
            default:
                decoratedTerm = '*' + searchTerm + '*';
                break;
        }

        return decoratedTerm;
    }

    function escape(value) {
        var result = "";

        for (var i = 0, c; i < value.length; i++) {
            c = value.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') || (c >= '0' && c <= '9') || c == '_')) {
                result += '\\';
            }

            result += c;
        }
        return result;
    }

    LogicECM.getExtQueryForContractor = function (currentForm) {
        var exSearchFilter = '';
        var nameValue, innValue, kppValue;

        nameValue = getInputValue(currentForm, "prop_lecm-contractor_fullname");
        if (nameValue) {
            exSearchFilter += '(@lecm\\-contractor\\:fullname:' + applySearchSettingsToTerm(nameValue, 'CONTAINS')
                + ' OR @lecm\\-contractor\\:shortname:' + applySearchSettingsToTerm(nameValue, 'CONTAINS') + ')';
        }

        innValue = getInputValue(currentForm, "prop_lecm-contractor_INN");
        if (innValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '=@lecm\\-contractor\\:INN:' + applySearchSettingsToTerm(innValue, 'MATCHES');
        }

        kppValue = getInputValue(currentForm, "prop_lecm-contractor_KPP");
        if (kppValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '=@lecm\\-contractor\\:KPP:' + applySearchSettingsToTerm(kppValue, 'MATCHES');
        }
        return exSearchFilter;
    };

    LogicECM.getExtQueryForPerson = function (currentForm) {
        var exSearchFilter = '';
        var nameValue, innValue, numberValue, addressValue, regionValue;

        nameValue = getInputValue(currentForm, "prop_lecm-contractor_lastName");
        if (nameValue) {
            exSearchFilter += '(@lecm\\-contractor\\:lastName:' + applySearchSettingsToTerm(nameValue, 'CONTAINS')
                + ' OR @lecm\\-contractor\\:firstName:' + applySearchSettingsToTerm(nameValue, 'CONTAINS')
                + ' OR @lecm\\-contractor\\:middleName:' + applySearchSettingsToTerm(nameValue, 'CONTAINS') + ')';
        }

        innValue = getInputValue(currentForm, "prop_lecm-contractor_INN");
        if (innValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '=@lecm\\-contractor\\:INN:' + applySearchSettingsToTerm(innValue, 'MATCHES');
        }

        numberValue = getInputValue(currentForm, "prop_lecm-contractor_document-number");
        if (numberValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '=@lecm\\-contractor\\:document\\-number:' + applySearchSettingsToTerm(numberValue, 'MATCHES');
        }

        addressValue = getInputValue(currentForm, "prop_lecm-contractor_physical-address");
        if (addressValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '@lecm\\-contractor\\:physical\\-address:' + applySearchSettingsToTerm(addressValue, 'CONTAINS');
        }

        regionValue = getInputValue(currentForm, "assoc_lecm-contractor_region-association");
        if (regionValue) {
            if (exSearchFilter) {
                exSearchFilter += " AND ";
            }
            exSearchFilter += '@lecm\\-contractor\\:region\\-association\\-ref:' + applySearchSettingsToTerm(regionValue, 'MATCHES');
        }
        return exSearchFilter;
    };

    LogicECM.getExtArgsForContractor = function (currentForm) {
        var args = getArgsFromForm(currentForm);
        var nameValue = getInputValue(currentForm, "prop_lecm-contractor_fullname");
        if (nameValue) {
            args["prop_lecm-contractor_shortname"] = nameValue;
        }
        return args;
    };

    LogicECM.getExtArgsForPerson = function (currentForm) {
        var args = getArgsFromForm(currentForm);
        var addrValue = getInputValue(currentForm, "prop_lecm-contractor_physical-address");
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
                    kpp: contractorKPP ? contractorKPP : '',
                    nodeRef: this.options.mode &&  this.options.mode !== "create" ? this.options.nodeRef : ''
                },
                successCallback: {
                    fn: function (response) {
                        var results = response.json;
                        if (results && results.hasDuplicate) {
                            var duplicates = results.duplicates;
                            var duplicatesFilter = '';
                            for (var i in duplicates) {
                                if (duplicatesFilter.length > 0) {
                                    duplicatesFilter += ",";
                                }
                                duplicatesFilter += duplicates[i].nodeRef;
                            }

                            var formId = "contractor-duplicates-" +  Alfresco.util.generateDomId();

                            var doBeforeDialogShow = function (p_form, p_dialog) {
                                var message = this.msg("title.find.duplicates");
                                p_dialog.dialog.setHeader(message);

                                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id, force: true}, this);
                                Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
                                p_dialog.widgets.okButton.set('label', this.msg("label.continue"));

                                //подменяем submit
                                var submitElement = p_form.submitElements[0];
                                submitElement.submitForm = function () {
                                    fnSubmit();
                                    p_dialog.hide();
                                };
                            };

                            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
                            var templateRequestParams = {
                                htmlid: formId,
                                itemKind: "type",
                                itemId: 'lecm-contractor:contractor-type',
                                formId:"show-duplicates",
                                mode: 'create',
                                submitType: 'json',
                                args: JSON.stringify({
                                    contractor_duplicates:duplicatesFilter
                                }),
                                showSubmitButton: true,
                                showCancelButton: true
                            };

                            var createDetails = new Alfresco.module.SimpleDialog(formId + "-showDialog");
                            createDetails.setOptions(
                                {
                                    width: "45em",
                                    templateUrl: templateUrl,
                                    templateRequestParams: templateRequestParams,
                                    actionUrl: null,
                                    destroyOnHide: true,
                                    doBeforeDialogShow: {
                                        fn: doBeforeDialogShow,
                                        scope: this
                                    }
                                }).show();
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
    };

    LogicECM.checkDuplicatesForPerson = function (fn, buttonScope, p_form) {
        var fnSubmit = function () {
            if (YAHOO.lang.isFunction(fn) && buttonScope) {
                fn.call(buttonScope);
            }
        };

        if (p_form != null && p_form.validate()) {
            var lastName = p_form.getFormData()["prop_lecm-contractor_lastName"];
            var firstName = p_form.getFormData()["prop_lecm-contractor_firstName"];
            var middleName = p_form.getFormData()["prop_lecm-contractor_middleName"];
            var region = p_form.getFormData()["assoc_lecm-contractor_region-association"];
            var inn = p_form.getFormData()["prop_lecm-contractor_INN"];
            var ogrn = p_form.getFormData()["prop_lecm-contractor_OGRN"];

            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/physical-persons/hasDuplicate",
                dataObj: {
                    lastName: lastName,
                    firstName: firstName,
                    middleName: middleName ? middleName : '',
                    region: region ? region : '',
                    ogrn: ogrn ? ogrn : '',
                    inn: inn ? inn : '',
                    nodeRef: this.options.mode &&  this.options.mode !== "create" ? this.options.nodeRef : ''
                },
                successCallback: {
                    fn: function (response) {
                        var results = response.json;
                        if (results && results.hasDuplicate) {
                            var duplicates = results.duplicates;
                            var duplicatesFilter = '';
                            for (var i in duplicates) {
                                if (duplicatesFilter.length > 0) {
                                    duplicatesFilter += ",";
                                }
                                duplicatesFilter += duplicates[i].nodeRef;
                            }

                            var formId = "person-duplicates-" +  Alfresco.util.generateDomId();

                            var doBeforeDialogShow = function (p_form, p_dialog) {
                                var message = this.msg("title.find.duplicates");
                                p_dialog.dialog.setHeader(message);

                                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id, force: true}, this);
                                Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
                                p_dialog.widgets.okButton.set('label', this.msg("label.continue"));

                                //подменяем submit
                                var submitElement = p_form.submitElements[0];
                                submitElement.submitForm = function () {
                                    fnSubmit();
                                    p_dialog.hide();
                                };
                            };

                            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
                            var templateRequestParams = {
                                htmlid: formId,
                                itemKind: "type",
                                itemId: 'lecm-contractor:physical-person-type',
                                formId:"show-duplicates",
                                mode: 'create',
                                submitType: 'json',
                                args: JSON.stringify({
                                    person_duplicates: duplicatesFilter
                                }),
                                showSubmitButton: true,
                                showCancelButton: true
                            };

                            var createDetails = new Alfresco.module.SimpleDialog(formId + "-showDialog");
                            createDetails.setOptions(
                                {
                                    width: "45em",
                                    templateUrl: templateUrl,
                                    templateRequestParams: templateRequestParams,
                                    actionUrl: null,
                                    destroyOnHide: true,
                                    doBeforeDialogShow: {
                                        fn: doBeforeDialogShow,
                                        scope: this
                                    }
                                }).show();
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
    };
})();