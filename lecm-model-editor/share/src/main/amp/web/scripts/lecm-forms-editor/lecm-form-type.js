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
		return this;
	};

	YAHOO.extend(LogicECM.module.FormsEditor.FormType, Alfresco.component.Base, {
		options: {
			selectedValue: null,
			mandatory: false,
			modelName: null,
			formIdField: null,
			idField: null,
			defaultIds: []
		},

		formIds: {},

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
							idField,
							idFieldValue,
							selected, i;
						if (oResults) {
							select = Dom.get(this.id);

							idField = select.form[this.options.idField];
							idFieldValue = idField ? idField.value : '';

							for (i in oResults) {
								oResult = oResults[i];
								oResult.id = oResult.id ? oResult.id : '';
								selected = oResult.evaluatorType == this.options.selectedValue && oResult.id == idFieldValue;

								if (!this.checkExistForm(existForms, oResult) || selected) {
									this.formIds[oResult.localName] = {
										id: oResult.id,
										formId: oResult.formId
									};

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

		checkExistForm: function(existForms, checkForm) {
			var i, existForm;
			if (existForms && checkForm) {
				for (i in existForms) {
					existForm = existForms[i];
					if (existForm.evaluator == checkForm.evaluatorType && existForm.id == checkForm.id) {
						checkForm.formId = existForm.formId;
						return true;
					}
				}
			}
			return false;
		},

		onSelectChange: function() {
			var select = Dom.get(this.id),
				idField = select.form[this.options.idField],
				formIdField = select.form[this.options.formIdField],
				formIdFieldContainer,
				value;

			if (idField && formIdField) {
				value = select[select.selectedIndex].innerHTML;
				idField.value = this.formIds[value].id;
				formIdField.value = this.formIds[value].formId;
				if (formIdField.parentElement && formIdField.parentElement.parentElement && formIdField.parentElement.parentElement.parentElement) {
					formIdFieldContainer = formIdField.parentElement.parentElement.parentElement;
					if (this.options.defaultIds.indexOf(idField.value) >= 0) {
						Dom.addClass(formIdFieldContainer, 'hidden');
					} else {
						Dom.removeClass(formIdFieldContainer, 'hidden');
					}
				}
			}

			if (this.options.mandatory) {
				YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
			}
		}
	});
})();
