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

(function ()
{
	var Dom = YAHOO.util.Dom;

	LogicECM.module.AgendaCheckbox = function (fieldHtmlId)
	{
		LogicECM.module.AgendaCheckbox.superclass.constructor.call(this, "LogicECM.module.AgendaCheckbox", fieldHtmlId, ["container", "datasource"]);
		this.checkboxId = fieldHtmlId + "-entry";
		this.attentionId = fieldHtmlId + "-attention";
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
							itemId: null,
							disabled: false,
							mode: false,
							defaultValue: null,
							defaultValueDataSource: null,
							disabledFieldsIfSelect: null,
							disabledFieldsIfNotSelect: null,
							hideFieldsIfSelect: null,
							hideFieldsIfNotSelect: null,
							fireMandatoryByChange: false,
							attentionMessage: null
						},
				checkboxId: null,
				attentionId: null,
				checkbox: null,
				initValue: null,
				setOptions: function (obj)
				{
					LogicECM.module.Checkbox.superclass.setOptions.call(this, obj);
					YAHOO.Bubbling.fire("afterOptionsSet",
							{
								eventGroup: this
							});
					return this;
				},

				onReady: function () {
					var item = this.options.itemId;
					if (item) {
						var nodeUUID = item.replace("workspace://SpacesStore/","");
						Alfresco.util.Ajax.request(
							{
								method: "GET",
								url: Alfresco.constants.PROXY_URI + "slingshot/doclib/node/workspace/SpacesStore/" + nodeUUID,
								successCallback: {
									fn: this.checkPermissions,
									scope: this
								},
								failureMessage: this.msg("message.failure"),
								scope: this,
								execScripts: false
							});
					}
				},

				checkPermissions: function (response) {
					var res = response.json;
					if (res && res.item && res.item.permissions && res.item.permissions.userAccess) {
						if (!res.item.permissions.userAccess["delete"]) {
							if (Dom.get(this.checkboxId)) {
								Dom.get(this.checkboxId).disabled = true;
							}
							else {
								// Check Initiator or secretary
							}
						}
					}
					this.init();
				},

				init: function () {
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
											fn: function (response) {
												var oResults = eval("(" + response.serverResponse.responseText + ")");
												if (oResults != null && oResults.checked != null) {
													me.options.defaultValue = oResults.checked;
													me.checkbox.checked = oResults.checked == "true";
													me.initValue = me.checkbox.checked;
												}
											}
										},
										failureMessage: "message.failure"
									});
						}
					}
				},
				onChange: function () {
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
					
					if (this.options.fireMandatoryByChange) {
						YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
					}

					if (this.options.itemId) {
						Alfresco.util.Ajax.request(
								{
									method: "POST",
									url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/meetings/setApproovementRequired",
									dataObj: {
										nodeRef: this.options.itemId,
										value: el.value
									},
									successCallback: {
										fn: function (response) {
											YAHOO.Bubbling.fire("redrawDocumentActions");
											YAHOO.Bubbling.fire("formValueChanged", {
												eventGroup: this,
												addedItems: [],
												removedItems: [],
												selectedItems: [],
												selectedItemsMetaData: {}
											});
										},
										scope: this
									},
									failureMessage: this.msg("message.failure"),
									scope: this,
									execScripts: false
								});
					}

				},
				checkDisableRelatedFields: function () {
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
									field.disabled = selected;
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
				hideRelatedFields: function () {
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