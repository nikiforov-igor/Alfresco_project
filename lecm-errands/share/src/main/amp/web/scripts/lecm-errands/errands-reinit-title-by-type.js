(function() {
    var lastSelectedErrandType = null;

    var errandsTypes = [];
    var errandsTypesLoaded = false;

    loadTypes();

    function loadTypes(callback, params) {
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getTypes",
            successCallback: {
                fn: function (response) {
                    var oResults = response.json;
                    if (oResults && oResults.length) {
                        var i;
                        for (i = 0; i < oResults.length; i++) {
                            errandsTypes[oResults[i].nodeRef] = oResults[i];
                        }
                        errandsTypesLoaded = true;
                        if (callback && typeof callback == "function")
                        {
                            callback.apply(this, params);
                        }
                    }
                },
                scope: this
            },
            failureCallback: {
                fn: function() {
                    errandsTypesLoaded = true;
                },
                scope: this
            }
        });
    }

	YAHOO.Bubbling.on('errandTypeChanged', reInit, {reinitLimitationDate: true, editForm: false});
	YAHOO.Bubbling.on('resolutionErrandTypeChanged', reInit, {reinitLimitationDate: false, editForm: false});
	YAHOO.Bubbling.on('resolutionEditErrandTypeChanged', reInit, {reinitLimitationDate: false, editForm: true});
    YAHOO.Bubbling.on('createErrandsWFErrandTypeChanged', reInit, {reinitLimitationDate: true, editForm: false});
    YAHOO.Bubbling.on('errandTitleChanged', titleChangeHandler);

    function titleChangeHandler(layer, args) {
        var formId = args[1].formId;
        var textContentField = Dom.get(formId + "_prop_lecm-errands_content");
        if (!textContentField) {
            textContentField = Dom.get(formId + "_prop_lecmErrandWf_content");
        }
        if (textContentField && !textContentField.value.length) {
            var selectedItems = args[1].selectedItems;
            if (selectedItems) {
                var key = Object.keys(selectedItems)[0];
                if (key) {
                    /*Автокомплит передает данные в несколько ином формате - поэтому вытаскиваем еще и из selectedName*/
                    textContentField.value = (selectedItems[key].name ? selectedItems[key].name : selectedItems[key].selectedName);
                }
            }
        }
    }
	function reInit(layer, args, param) {
	    if (errandsTypesLoaded) {
            var obj = args[1];
            var nodeRef;

            if (obj.selectedItems) {
                var keys = Object.keys(obj.selectedItems);
                if (keys.length == 1) {
                    nodeRef = keys[0];
                }
            }

            var limitationDateProp = "lecm-errands_limitation-date";
            var limitationDateField = Dom.get(obj.formId + "_prop_" + limitationDateProp);
            var limitationDateRadioProp = "lecm-errands_limitation-date-radio";
            var limitationDateRadio = Dom.get(obj.formId + "_prop_" + limitationDateRadioProp);

            if (nodeRef) {
                var titleProp = "lecm-errands_title";
                var contentProp = "lecm-errands_content";
                var reportRequiredProp = "lecm-errands_report-required";
                var reportRequiredChangedFireEvent = "errandReportRequiredChanged";
                var limitationDateRadioChangedEvent = "changeLimitationDateRadio";
                if (layer == "createErrandsWFErrandTypeChanged") {
                    titleProp = "lecmErrandWf_title";
                    contentProp = "lecmErrandWf_content";
                    reportRequiredProp = "lecmErrandWf_reportRequired";
                    limitationDateRadioProp = "lecmErrandWf_limitationDateRadio";
                    limitationDateRadio = Dom.get(obj.formId + "_prop_" + limitationDateRadioProp);
                    reportRequiredChangedFireEvent = "createErrandsWFErrandReportRequiredChanged";
                    limitationDateRadioChangedEvent = "createErrandsWFChangeLimitationDateRadio";
                }
                var titleElement = Dom.get(obj.formId + "_prop_" + titleProp);
                var contentElement = Dom.get(obj.formId + "_prop_" + contentProp);
                var reportRequiredElement = Dom.get(obj.formId + "_prop_" + reportRequiredProp);
                if (errandsTypes[nodeRef]) {
                    if (!obj.onlyDateReinit) {
                        if (titleElement) {
                            titleElement.value = errandsTypes[nodeRef].defaultTitle;
                        }
                        if (contentElement && !contentElement.value) {
                            contentElement.value = errandsTypes[nodeRef].defaultTitle;
                        }
                        if (reportRequiredElement) {
                            if (!param.editForm || (lastSelectedErrandType && lastSelectedErrandType !== nodeRef)) {
                                var reportRequiredCheckBox = Dom.get(reportRequiredElement.id + "-entry");
                                if (errandsTypes[nodeRef]["report-required"]) {
                                    reportRequiredElement.value = true;
                                    reportRequiredCheckBox.checked = true;
                                } else {
                                    reportRequiredElement.value = false;
                                    reportRequiredCheckBox.checked = false;
                                }
                                YAHOO.Bubbling.fire(reportRequiredChangedFireEvent, {
                                    formId: obj.formId,
                                    fieldId: reportRequiredProp.replace("_", ":")
                                });
                            } else if (param.editForm) {
                                lastSelectedErrandType = nodeRef;
                            }
                        }
                    }
                    if (limitationDateRadio && param && param.reinitLimitationDate) {
                        var checkedRadioButton = YAHOO.util.Selector.query("input[checked]", limitationDateRadio.parentElement, true);
                        checkedRadioButton.checked = false;
                        if (errandsTypes[nodeRef].limitless) {
                            limitationDateRadio.value = "LIMITLESS";
                            var limitlessRadioButton = YAHOO.util.Selector.query("input[type=radio][value='LIMITLESS']", limitationDateRadio.parentElement, true);
                            limitlessRadioButton.checked = true;
                        } else {
                            limitationDateRadio.value = "DAYS";
                            var daysRadioButton = YAHOO.util.Selector.query("input[type=radio][value='DAYS']", limitationDateRadio.parentElement, true);
                            daysRadioButton.checked = true;
                        }
                        YAHOO.Bubbling.fire(limitationDateRadioChangedEvent, {
                            value: limitationDateRadio.value,
                            formId: obj.formId,
                            fieldId: limitationDateRadioProp.replace("_", ":")
                        });
                    }
                }

            } else {
                if (limitationDateRadio && limitationDateField && limitationDateField.value) {
                    var dateRadioButton = YAHOO.util.Selector.query("input[type=radio][value='DATE']", limitationDateRadio.parentElement, true);
                    dateRadioButton.checked = true;
                    limitationDateRadio.value = "DATE";

                    YAHOO.Bubbling.fire("changeLimitationDateRadio", {
                        value: limitationDateRadio.value,
                        formId: obj.formId,
                        fieldId: "lecm-errands:limitation-date-radio"
                    });
                }
            }
            YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
        } else {
            loadTypes(reInit,[layer, args, param]);
        }
	}
})();