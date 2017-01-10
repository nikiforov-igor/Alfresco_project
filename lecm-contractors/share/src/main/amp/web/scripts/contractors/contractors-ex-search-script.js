(function () {
    function getInputValue(form, propName) {
        var value = null;
        var inputName = form[propName];
        if (inputName) {
            value = inputName.value;
            value = YAHOO.lang.trim(value);
        }
        return value;
    }

    function getArgsFromForm(currentForm) {
        var args = {}, element, propName, propValue;

        for (var i = 0; i < currentForm.elements.length; i++) {
            element = currentForm.elements[i],
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
            args["prop_lecm-contractor_legal-address"] = addrValue;
        }
        return args;
    };

    LogicECM.checkDuplicatesForContractor = function (fn, buttonScope, p_form) {
        var fnSubmit = function () {
            if (YAHOO.lang.isFunction(fn) && buttonScope) {
                fn.call(buttonScope);
            }
        };

        if (p_form && p_form.validate()) {
            var contractorFull = p_form.getFormData()["prop_lecm-contractor_fullname"];
            var contractorShort = p_form.getFormData()["prop_lecm-contractor_shortname"];
            var contractorINN = p_form.getFormData()["prop_lecm-contractor_INN"];
            var contractorKPP = p_form.getFormData()["prop_lecm-contractor_KPP"];

            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/contractors/hasDuplicate",
                dataObj: {
                    fullName: contractorFull ? contractorFull : '',
                    shortName: contractorShort ? contractorShort : '',
                    inn: contractorINN ? contractorINN : '',
                    kpp: contractorKPP ? contractorKPP : '',
                    nodeRef: this.options.mode && this.options.mode !== "create" ? this.options.nodeRef : ''
                },
                successCallback: {
                    fn: function (response) {
                        var result = response.json;
                        if (result && result.hasDuplicate) {
                            _showDuplicatesDialog({
                                duplicates: result.duplicates,
                                dialogId: "contractor-duplicates-" + Alfresco.util.generateDomId(),
                                itemType: "lecm-contractor:contractor-type",
                                fnSubmit: fnSubmit
                            });
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

        if (p_form && p_form.validate()) {
            var lastName = p_form.getFormData()["prop_lecm-contractor_lastName"];
            var firstName = p_form.getFormData()["prop_lecm-contractor_firstName"];
            var middleName = p_form.getFormData()["prop_lecm-contractor_middleName"];
            var region = p_form.getFormData()["assoc_lecm-contractor_region-association"];
            var inn = p_form.getFormData()["prop_lecm-contractor_INN"];
            var ogrn = p_form.getFormData()["prop_lecm-contractor_OGRN"];

            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/physical-persons/hasDuplicate",
                dataObj: {
                    lastName: lastName ? lastName : '',
                    firstName: firstName ? firstName : '',
                    middleName: middleName ? middleName : '',
                    region: region ? region : '',
                    ogrn: ogrn ? ogrn : '',
                    inn: inn ? inn : '',
                    nodeRef: this.options.mode && this.options.mode !== "create" ? this.options.nodeRef : ''
                },
                successCallback: {
                    fn: function (response) {
                        var result = response.json;
                        if (result && result.hasDuplicate) {
                            _showDuplicatesDialog({
                                duplicates: result.duplicates,
                                dialogId: "person-duplicates-" + Alfresco.util.generateDomId(),
                                itemType: "lecm-contractor:physical-person-type",
                                fnSubmit: fnSubmit
                            });
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

    function _showDuplicatesDialog(options) {
        var duplicatesFilter = '';
        var duplicates = options.duplicates;
        for (var i in duplicates) {
            if (duplicatesFilter.length > 0) {
                duplicatesFilter += ",";
            }
            duplicatesFilter += duplicates[i].nodeRef;
        }

        var doBeforeDialogShow = function (p_form, p_dialog) {
            var message = p_dialog.msg("title.find.duplicates");
            p_dialog.dialog.setHeader(message);

            p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {
                moduleId: p_dialog.id,
                force: true
            }, this);
            Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
            p_dialog.widgets.okButton.set('label', p_dialog.msg("label.continue"));

            //подменяем submit
            var submitElement = p_form.submitElements[0];
            submitElement.submitForm = function () {
                options.fnSubmit.call(this);
                p_dialog.hide();
            };
        };

        var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
        var templateRequestParams = {
            htmlid: options.dialogId,
            itemKind: "type",
            itemId: options.itemType,
            formId: options.formId ? options.formId : "show-duplicates",
            mode: 'create',
            submitType: 'json',
            args: JSON.stringify({
                duplicates_str: duplicatesFilter
            }),
            showSubmitButton: true,
            showCancelButton: true,
			showCaption: false
        };

        var duplicateDetails = new Alfresco.module.SimpleDialog(options.dialogId + "-showDialog");
        duplicateDetails.setOptions(
            {
                width: "800px",
                templateUrl: templateUrl,
                templateRequestParams: templateRequestParams,
                actionUrl: null,
                destroyOnHide: true,
                doBeforeDialogShow: {
                    fn: doBeforeDialogShow,
                    scope: this
                }
            }).show();
    }
})();