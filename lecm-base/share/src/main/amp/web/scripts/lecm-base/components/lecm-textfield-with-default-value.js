if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function()
{
	var Dom = YAHOO.util.Dom;

	LogicECM.module.TextfieldWithDefaultValue = function(fieldHtmlId) {
        LogicECM.module.TextfieldWithDefaultValue.superclass.constructor.call(this, "LogicECM.module.TextfieldWithDefaultValue", fieldHtmlId, ["container", "datasource"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.TextfieldWithDefaultValue, Alfresco.component.Base, {
		options: {
			defaultValueDataSource: null,
			allowInNonCreateMode: false
		},
		textField: null,
		setOptions: function(obj) {
			LogicECM.module.TextfieldWithDefaultValue.superclass.setOptions.call(this, obj);
			YAHOO.Bubbling.fire("afterOptionsSet", {
				eventGroup: this
			});
			return this;
		},
		onReady: function() {
			this.textField = Dom.get(this.options.fieldId);
			if (this.textField) {
				if (!this.options.disabled && !this.textField.value && (this.options.allowInNonCreateMode || this.options.mode === "create")) {
					this.loadDefaultValue();
				}
			}
		},
		loadDefaultValue: function() {
			if (this.options.defaultValueDataSource) {
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI + this.options.defaultValueDataSource,
					successCallback: {
						scope: this,
						fn: function(response) {
							var oResults = response.json;
							if (oResults && oResults.value) {
								this.textField.value = oResults.value;
							} else {
								this.textField.value = this.options.defaultValue;
							}
							YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
						}
					}
				});
			} else {
				this.textField.value = (this.options.defaultValue?this.options.defaultValue:null);
			}
		}
	});
})();
