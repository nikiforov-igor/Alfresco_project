(function() {
    var errandsTypes = [];
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
                }
            },
            scope: this
        }
    });


	YAHOO.Bubbling.on('errandTypeChanged', reInit);
    YAHOO.Bubbling.on('createErrandsWFErrandTypeChanged', reInit);

    function reInit(layer, args) {
		var obj = args[1];
		var nodeRef;

		if(obj.selectedItems) {
            var keys = Object.keys(obj.selectedItems);
            if (keys.length == 1) {
                nodeRef = keys[0];
            }
		}

		if(nodeRef) {
		    var titleProp = "lecm-errands_title";
            var contentProp = "lecm-errands_content";
            var reportRequiredProp = "lecm-errands_report-required";
            var limitationDateRadioProp = "lecm-errands_limitation-date-radio";
            var reportRequiredChangedFireEvent = "errandReportRequiredChanged";
            var limitationDateRadioChangedEvent = "changeLimitationDateRadio";
		    if (layer == "createErrandsWFErrandTypeChanged") {
                titleProp = "lecmErrandWf_title";
                contentProp = "lecmErrandWf_content";
                reportRequiredProp = "lecmErrandWf_reportRequired";
                limitationDateRadioProp = "lecmErrandWf_limitationDateRadio";
                reportRequiredChangedFireEvent = "createErrandsWFErrandReportRequiredChanged";
                limitationDateRadioChangedEvent = "createErrandsWFChangeLimitationDateRadio";
            }
            var titleElement = Dom.get(obj.formId + "_prop_" + titleProp);
            var contentElement = Dom.get(obj.formId + "_prop_" + contentProp);
            var reportRequiredElement = Dom.get(obj.formId + "_prop_" + reportRequiredProp);
            var limitationDateRadio = Dom.get(obj.formId + "_prop_" + limitationDateRadioProp);
            if (errandsTypes[nodeRef]) {
                if (titleElement) {
                    titleElement.value = errandsTypes[nodeRef].defaultTitle;
                }
                if (contentElement && !contentElement.value) {
                    contentElement.value = errandsTypes[nodeRef].defaultTitle;
                }
                if (limitationDateRadio) {
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
                        fieldId: limitationDateRadioProp.replace("_",":")
                    });
                }
                if (reportRequiredElement) {
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
                        fieldId: reportRequiredProp.replace("_",":")
                    });
                }
            }

		}
	}
})();