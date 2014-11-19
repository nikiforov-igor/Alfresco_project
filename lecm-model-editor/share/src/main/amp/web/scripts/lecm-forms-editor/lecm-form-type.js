/**
* LogicECM root namespace.
    *
* @namespace LogicECM
*/
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.FormsEditor = LogicECM.module.FormsEditor || {};

(function() {

    var Dom = YAHOO.util.Dom;
    LogicECM.module.FormsEditor.FormType = function (fieldHtmlId)
    {
		LogicECM.module.FormsEditor.FormType.superclass.constructor.call(this, "LogicECM.module.FormsEditor.FormType", fieldHtmlId, ["container", "history"]);
	    this.formIds = [];
	    return this;
    };

    YAHOO.extend(LogicECM.module.FormsEditor.FormType, Alfresco.component.Base, {
		options: {
			selectedValue: null,
			mandatory: false,
			modelName: null,
			fromIdField: null
		},

	    formIds: null,

		onReady: function() {
			YAHOO.util.Event.on(this.id, "change", this.onSelectChange, this, true);
			this.loadExistForms();
		},

	    loadExistForms: function() {
		    var modelType = YAHOO.util.History.getQueryStringParameter('doctype');
		    if (modelType != null) {
			    Alfresco.util.Ajax.jsonGet({
				    url:  Alfresco.constants.PROXY_URI + "/lecm/docforms/forms?modelName=" + encodeURIComponent(modelType),
				    successCallback: {
					    fn: function (response) {
						    var oResults = response.json;
						    if (oResults != null) {
							    this.loadConfig(oResults);
						    }
					    },
					    scope: this
				    }
			    });
		    } else {
			    this.loadConfig([]);
		    }
	    },

	    loadConfig: function(existForms) {
		    Alfresco.util.Ajax.jsonGet({
			    url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/forms/getConfig?action=getFormTypes",
			    successCallback: {
				    fn: function (response) {
					    var oResults = response.json;
					    if (oResults != null) {
						    var select = Dom.get(this.id);

						    var selectedFormId = "";
						    var formIdField = select.form[this.options.fromIdField];
						    if (formIdField != null) {
							    selectedFormId = formIdField.value;
						    }

						    for (var i = 0; i < oResults.length; i++) {

							    oResults[i].id = oResults[i].id != null ? oResults[i].id : "";
							    var selected = oResults[i].evaluatorType == this.options.selectedValue && oResults[i].id == selectedFormId;

							    if (!this.checkExistForm(existForms, oResults[i]) || selected) {
								    var option = document.createElement("option");
								    option.value = oResults[i].evaluatorType;
								    option.innerHTML = oResults[i].localName;

								    if (selected) {
									    option.selected = true;
								    }

								    this.formIds[oResults[i].localName] = oResults[i].id;
								    select.appendChild(option);
							    }
						    }

						    this.onSelectChange();
					    }
				    },
				    scope: this
			    }
		    });
	    },

	    checkExistForm: function(existFroms, checkForm) {
			if (existFroms != null && checkForm != null) {
				for (var i = 0; i < existFroms.length; i++) {
					if (existFroms[i].evaluator == checkForm.evaluatorType && existFroms[i].id == checkForm.id) {
						return true;
					}
				}
			}
		    return false
		},

	    onSelectChange: function() {
		    var select = Dom.get(this.id);
		    var formIdField = select.form[this.options.fromIdField];
		    if (formIdField != null ) {
			    var value = select[select.selectedIndex].innerHTML;

			    formIdField.value = this.formIds[value];
		    }
		    if (this.options.mandatory) {
			    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
		    }
	    }
	 });
})();