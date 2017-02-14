if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function()
{
	var Dom = YAHOO.util.Dom;

	LogicECM.module.TextfieldConstraintValue = function(fieldHtmlId) {
        LogicECM.module.TextfieldConstraintValue.superclass.constructor.call(this, "LogicECM.module.TextfieldConstraintValue", fieldHtmlId, ["container", "datasource"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.TextfieldConstraintValue, Alfresco.component.Base, {
		options: {
			defaultValueDataSource: null,
			allowInNonCreateMode: false
		},
		textField: null,
		setOptions: function(obj) {
			LogicECM.module.TextfieldConstraintValue.superclass.setOptions.call(this, obj);
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
				Alfresco.util.Ajax.request({
					url: Alfresco.constants.PROXY_URI+"lecm/type/model?doctype="+this.options.defaultValue,// + this.options.defaultValueDataSource,
					successCallback: {
						scope: this,
						fn: function(response) {
							var oResults = response.json;
							if (oResults && oResults.constraints) {
								var c = oResults.constraints.constraint;
								if (c instanceof Array) {
									for ( var i = 0, n = c.length; i < n; i++) {
										var constName = this.options.defaultValueDataSource.substr(0,this.options.defaultValueDataSource.indexOf('/')-1);
										var paramName = this.options.defaultValueDataSource.substr(this.options.defaultValueDataSource.indexOf('/')+1,this.options.defaultValueDataSource.length);
										if(c[i]._name.indexOf(constName)!==-1) {
											for(var p in c[i].parameter) {
												if(c[i].parameter[p]._name===paramName) {
													this.textField.value = c[i].parameter[p].value;
												}
											}
										}
									}
								}
//								this.textField.value = oResults.value;
							} else {
								this.textField.value = this.options.defaultValue;
							}
//							YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
						}
					}
				});
			} else {
				this.textField.value = (this.options.defaultValue?this.options.defaultValue:null);
			}
		}
	});
})();
