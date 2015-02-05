/**
* LogicECM root namespace.
*
* @namespace LogicECM
*/
// Ensure LogicECM root object exists
if (typeof LogicECM == 'undefined' || !LogicECM) {
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

	LogicECM.module.FormsEditor.FormType = function (fieldHtmlId) {
		LogicECM.module.FormsEditor.FormType.superclass.constructor.call(this, 'LogicECM.module.FormsEditor.FormType', fieldHtmlId, ['container', 'history']);
		this.formIds = [];
		return this;
	};

	YAHOO.extend(LogicECM.module.FormsEditor.FormType, Alfresco.component.Base, {
		options: {
			selectedValue: null,
			mandatory: false,
			modelName: null,
			formIdField: null
		},

		formIds: null,

		onReady: function() {
			YAHOO.util.Event.on(this.id, 'change', this.onSelectChange, this, true);
			this.loadExistForms();
		},

		loadExistForms: function() {
			var modelType = YAHOO.util.History.getQueryStringParameter('doctype');
			if (modelType) {
				Alfresco.util.Ajax.jsonGet({
					url:  Alfresco.constants.PROXY_URI + '/lecm/docforms/forms?modelName=' + encodeURIComponent(modelType),
					successCallback: {
						scope: this,
						fn: function (response) {
							var existForms = response.json ? response.json : [];
							this.loadConfig(existForms);
						}
					}
				});
			} else {
				this.loadConfig([]);
			}
		},

		loadConfig: function(existForms) {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.URL_SERVICECONTEXT + '/lecm/forms/getConfig?action=getFormTypes',
				successCallback: {
					fn: function (response) {
						var select, option,
							oResult, oResults = response.json,
							formIdField,
							selectedFormId = '',
							selected, i;
						if (oResults) {
							select = Dom.get(this.id);


							formIdField = select.form[this.options.formIdField];
							if (formIdField) {
								selectedFormId = formIdField.value;
							}

							for (i in oResults) {
								oResult = oResults[i];
								oResult.id = oResult.id != null ? oResult.id : '';
								selected = oResult.evaluatorType == this.options.selectedValue && oResult.id == selectedFormId;

								if (!this.checkExistForm(existForms, oResult) || selected) {
									this.formIds[oResult.localName] = oResult.id;

									option = document.createElement('option');
									option.value = oResult.evaluatorType;
									option.innerHTML = oResult.localName;
									option.selected = selected;
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
			if (existFroms && checkForm) {
				for (var i in existFroms) {
					if (existFroms[i].evaluator == checkForm.evaluatorType && existFroms[i].id == checkForm.id) {
						return true;
					}
				}
			}
			return false;
		},

		onSelectChange: function() {
			var select = Dom.get(this.id),
				formIdField = select.form[this.options.formIdField],
				value;

			if (formIdField) {
				value = select[select.selectedIndex].innerHTML;
				formIdField.value = this.formIds[value];
			}

			if (this.options.mandatory) {
				YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
			}
		}
	 });
})();
