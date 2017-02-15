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
            var titleElement = Dom.get(obj.formId + "_prop_lecm-errands_title");
            var contentElement = Dom.get(obj.formId + "_prop_lecm-errands_content");
            var reportRequiredElement = Dom.get(obj.formId + "_prop_lecm-errands_report-required");
            var limitationDateRadio = Dom.get(obj.formId + "_prop_lecm-errands_limitation-date-radio");
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
                    YAHOO.Bubbling.fire("changeLimitationDateRadio", {
                        value: limitationDateRadio.value,
                        formId: obj.formId,
                        fieldId: "lecm-errands:limitation-date-radio"
                    });
                }
                if (reportRequiredElement) {
                    var reportRequiredCheckBox = Dom.get(reportRequiredElement.id + "-entry");
                    if (errandsTypes[nodeRef]["report-required"]) {
                        reportRequiredElement.value = false;
                        reportRequiredCheckBox.checked = false;
                    } else {
                        reportRequiredElement.value = true;
                        reportRequiredCheckBox.checked = true;
                    }
                    reportRequiredCheckBox.click();
                    YAHOO.Bubbling.fire("errandReportRequiredChanged", {
                        formId: obj.formId,
                        fieldId: "lecm-errands:report-required"
                    });
                }
            }

		}
	}
})();