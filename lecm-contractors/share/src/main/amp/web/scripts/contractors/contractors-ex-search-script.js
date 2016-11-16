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
                    nodeRef: p_form.getFormData()["alf_destination"]
                },
                successCallback: {
                    fn: function (response) {
                        var results = response.json;
                        if (results && results.hasDuplicate) {
                            var duplicates = results.duplicates;
                            var message = "В справочнике найдены похожие элементы" + ":<br/>";
                            for (var item in duplicates) {
                                message += duplicates[item].fullName + "<br/>";
                            }
                            Alfresco.util.PopupManager.displayPrompt(
                                {
                                    title: "Найдены похожие элементы",
                                    text: message,
                                    noEscape: true,
                                    buttons: [
                                        {
                                            text: Alfresco.util.message("button.ok"),
                                            handler: function dlA_onAction_action() {
                                                this.destroy();
                                                fnSubmit();
                                            }
                                        },
                                        {
                                            text: Alfresco.util.message("button.cancel"),
                                            handler: function dlA_onActionDelete_cancel() {
                                                this.destroy();
                                            },
                                            isDefault: true
                                        }
                                    ]
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
    }

})();