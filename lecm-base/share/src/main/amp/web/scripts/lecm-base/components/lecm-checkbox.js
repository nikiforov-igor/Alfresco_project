/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function()
{
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Checkbox = function(fieldHtmlId)
    {
        LogicECM.module.Checkbox.superclass.constructor.call(this, "LogicECM.module.Checkbox", fieldHtmlId, ["container", "datasource"]);
        this.checkboxId = fieldHtmlId + "-entry";
	    YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
	    YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);
	    YAHOO.Bubbling.on("disableRelatedFields", this.onDisableRelatedFields, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.Checkbox, Alfresco.component.Base,
            {
                options:
                        {
                            fieldId: null,
	                        formId: false,
                            disabled: false,
                            mode: false,
                            defaultValue: null,
                            defaultValueDataSource: null,
                            disabledFieldsIfSelect: null,
                            disabledFieldsIfNotSelect: null,
	                        fireMandatoryByChange: false
                        },
                checkboxId: null,
                checkbox: null,
                setOptions: function(obj)
                {
                    LogicECM.module.Checkbox.superclass.setOptions.call(this, obj);
                    YAHOO.Bubbling.fire("afterOptionsSet",
                            {
                                eventGroup: this
                            });
                    return this;
                },
                onReady: function()
                {
                    this.checkbox = Dom.get(this.checkboxId);
                    if (this.checkbox) {
                        if (this.options.mode == "create") {
                            this.loadDefaultValue();
                        }
                        YAHOO.util.Event.addListener(this.checkbox, "click", this.onChange, this, true);
                    }
                    this.onChange();
	                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
                },
                loadDefaultValue: function AssociationSelectOne__loadDefaultValue() {
                    if (this.options.defaultValue != null) {
                        this.checkbox.checked = this.options.defaultValue == "true";
                    } else {
                        if (this.options.defaultValueDataSource != null) {
                            var me = this;
                            Alfresco.util.Ajax.request(
                                    {
                                        url: Alfresco.constants.PROXY_URI + this.options.defaultValueDataSource,
                                        successCallback: {
                                            fn: function(response) {
                                                var oResults = eval("(" + response.serverResponse.responseText + ")");
                                                if (oResults != null && oResults.checked != null) {
	                                                me.options.defaultValue = oResults.checked;
                                                    me.checkbox.checked = oResults.checked == "true";
                                                }
                                            }
                                        },
                                        failureMessage: "message.failure"
                                    });
                        }
                    }
                },
                onChange: function() {
                    var el = Dom.get(this.id);
	                el.value = this.checkbox.checked;
                    this.checkDisableRelatedFields();
                    YAHOO.Bubbling.fire("formValueChanged", {
                       eventGroup: this,
                       addedItems: [],
                       removedItems: [],
                       selectedItems: [],
                       selectedItemsMetaData: {}
                    });
	                if (this.options.fireMandatoryByChange) {
		                YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
	                }
                },
	            checkDisableRelatedFields: function() {
		            var el = Dom.get(this.id);
		            var me = this;
		            var selected = this.checkbox.checked;
		            if (this.options.disabledFieldsIfNotSelect != null) {
			            for (var i = 0; i < this.options.disabledFieldsIfNotSelect.length; i++) {
				            var field = el.form["prop_" + this.options.disabledFieldsIfNotSelect[i].replace(":", "_")];
				            if (field != null) {
					            if (field.className.indexOf("initially-disabled") == -1) {
						            field.disabled = !selected;
					            }

					            var fieldId = this.options.disabledFieldsIfNotSelect[i];
					            if (!selected) {
						            LogicECM.module.Base.Util.disableControl(me.options.formId, fieldId);
					            } else {
						            LogicECM.module.Base.Util.enableControl(me.options.formId, fieldId);
					            }
				            }
			            }
		            }
		            if (this.options.disabledFieldsIfSelect != null) {
			            for (i = 0; i < this.options.disabledFieldsIfSelect.length; i++) {
				            field = el.form["prop_" + this.options.disabledFieldsIfSelect[i].replace(":", "_")];
				            if (field != null) {
					            if (field.className.indexOf("initially-disabled") == -1) {
						            field.disabled = !selected;
					            }

					            fieldId = this.options.disabledFieldsIfSelect[i];
					            if (selected) {
						            LogicECM.module.Base.Util.disableControl(me.options.formId, fieldId);
					            } else {
						            LogicECM.module.Base.Util.enableControl(me.options.formId, fieldId);
					            }
				            }
			            }
		            }
	            },

	            onDisableControl: function (layer, args) {
		            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
			            if (this.checkbox != null) {
				            this.checkbox.disabled = true;
				            Dom.get(this.id).disabled = true;
			            }
		            }
	            },

	            onEnableControl: function (layer, args) {
		            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
			            if (!this.options.disabled && this.checkbox != null) {
				            this.checkbox.disabled = false;
				            Dom.get(this.id).disabled = false;
			            }
		            }
	            },

	            onDisableRelatedFields: function (layer, args) {
		            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
			            this.checkDisableRelatedFields();
		            }
	            }
            });
})();