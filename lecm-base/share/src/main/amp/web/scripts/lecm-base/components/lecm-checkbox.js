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

(function()
{
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Checkbox = function(fieldHtmlId)
    {
        LogicECM.module.Checkbox.superclass.constructor.call(this, "LogicECM.module.Checkbox", fieldHtmlId, ["container", "datasource"]);
        this.checkboxId = fieldHtmlId + "-entry";
        this.attentionId = fieldHtmlId + "-attention";
		YAHOO.Bubbling.on("readonlyContol", this.onReadonlyControl, this);
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
                            hideFieldsIfSelect: null,
                            hideFieldsIfNotSelect: null,
	                        fireMandatoryByChange: false,
                            changeFireAction: null,
                            attentionMessage: null
                        },
                checkboxId: null,
                attentionId: null,
                checkbox: null,
                initValue: null,
				readonly: false,
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
                        this.initValue = this.checkbox.checked;

                        this.onChange();
                        Dom.setStyle(this.attentionId, 'display', 'none');
                    } else {
                        this.hideRelatedFields();
                    }

	                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
                },
                loadDefaultValue: function AssociationSelectOne__loadDefaultValue() {
                    if (this.options.defaultValue != null) {
                        this.checkbox.checked = this.options.defaultValue == "true";
                        this.initValue = this.checkbox.checked;
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
                                                    me.initValue = me.checkbox.checked;
                                                    me.onChange();
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
                    if (this.checkbox.checked != this.initValue && this.options.attentionMessage != null) {
                        Dom.get(this.attentionId).innerHTML = this.options.attentionMessage;
                        Dom.setStyle(this.attentionId, 'display', 'block');
                    } else {
                        Dom.setStyle(this.attentionId, 'display', 'none');
                    }
                    this.checkDisableRelatedFields();
                    this.hideRelatedFields();
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
                    if (this.options.changeFireAction != null && this.options.changeFireAction != "") {
                        YAHOO.Bubbling.fire(this.options.changeFireAction, {
                            formId: this.options.formId,
                            fieldId: this.options.fieldId,
                            control: this
                        });
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
								LogicECM.module.Base.Util.readonlyControl(me.options.formId, fieldId, !selected);
				            }
			            }
		            }
		            if (this.options.disabledFieldsIfSelect != null) {
			            for (i = 0; i < this.options.disabledFieldsIfSelect.length; i++) {
				            field = el.form["prop_" + this.options.disabledFieldsIfSelect[i].replace(":", "_")];
				            if (field != null) {
					            if (field.className.indexOf("initially-disabled") == -1) {
						            field.disabled = selected;
					            }

					            fieldId = this.options.disabledFieldsIfSelect[i];
								LogicECM.module.Base.Util.readonlyControl(me.options.formId, fieldId, selected);
				            }
			            }
		            }
	            },

                hideRelatedFields: function() {
		            var el = Dom.get(this.id);
                    var selected = Dom.get(this.id).value == "true";
		            if (this.options.hideFieldsIfNotSelect != null) {
			            for (var i = 0; i < this.options.hideFieldsIfNotSelect.length; i++) {
                            var fieldId = this.options.hideFieldsIfNotSelect[i];
                            if (!selected) {
                                LogicECM.module.Base.Util.hideControl(this.options.formId, fieldId);
                            } else {
                                LogicECM.module.Base.Util.showControl(this.options.formId, fieldId);
                            }
			            }
		            }
		            if (this.options.hideFieldsIfSelect != null) {
			            for (i = 0; i < this.options.hideFieldsIfSelect.length; i++) {
                            fieldId = this.options.hideFieldsIfSelect[i];
                            if (selected) {
                                LogicECM.module.Base.Util.hideControl(this.options.formId, fieldId);
                            } else {
                                LogicECM.module.Base.Util.showControl(this.options.formId, fieldId);
                            }
			            }
		            }
	            },

				onReadonlyControl: function (layer, args) {
					var input, fn;
					if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
						this.readonly = args[1].readonly;
						input = Dom.get(this.id);
						if (input) {
							fn = args[1].readonly ? input.setAttribute : input.removeAttribute;
							fn.call(input, "readonly", "");
						}
						if (this.checkbox) {
							fn = args[1].readonly ? this.checkbox.setAttribute : this.checkbox.removeAttribute;
							fn.call(this.checkbox, "readonly", "");
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
