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

    LogicECM.module.RadioButton = function(fieldHtmlId)
    {
        LogicECM.module.RadioButton.superclass.constructor.call(this, "LogicECM.module.RadioButton", fieldHtmlId, ["container", "datasource"]);
        this.checkboxId = fieldHtmlId + "-entry";
	    YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
	    YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);
	    YAHOO.Bubbling.on("disableRelatedFields", this.onDisableRelatedFields, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.RadioButton, Alfresco.component.Base,
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
	                        fireMandatoryByChange: false,
							groupName: null
                        },
                checkboxId: null,
                checkbox: null,
                setOptions: function(obj)
                {
                    LogicECM.module.RadioButton.superclass.setOptions.call(this, obj);
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
                        YAHOO.util.Event.addListener(this.checkbox, "click", this.onSelect, this, true);
						YAHOO.Bubbling.on('deselect', this.onDeselect, this);
                    }
                    this.onChange();
	                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
                },
				onSelect: function() {
					var radios = Selector.query('input[name=' + this.options.groupName + ']:not([id=' + this.checkboxId + '])');
					radios.forEach(function(el) {
						YAHOO.Bubbling.fire('deselect', {
							groupName: this.options.groupName,
							target: el
						});
					}, this);

					this.onChange();

				},
				onDeselect: function(layer, args) {
					obj = args[1];
					if(obj.groupName == this.options.groupName && obj.target == this.checkbox) {
						this.onChange();
					}
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

							var fieldId = this.options.disabledFieldsIfNotSelect[i];
							if (!selected) {
								LogicECM.module.Base.Util.disableControl(me.options.formId, fieldId);
							} else {
								LogicECM.module.Base.Util.enableControl(me.options.formId, fieldId);
							}
			            }
		            }
		            if (this.options.disabledFieldsIfSelect != null) {
			            for (i = 0; i < this.options.disabledFieldsIfSelect.length; i++) {

							var fieldId = this.options.disabledFieldsIfSelect[i];
							if (selected) {
								LogicECM.module.Base.Util.disableControl(me.options.formId, fieldId);
							} else {
								LogicECM.module.Base.Util.enableControl(me.options.formId, fieldId);
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