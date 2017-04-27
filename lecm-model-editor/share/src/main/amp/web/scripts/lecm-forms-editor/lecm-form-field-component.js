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

	LogicECM.module.FormsEditor.FieldComponent = function (fieldHtmlId) {
		LogicECM.module.FormsEditor.FieldComponent.superclass.constructor.call(this, "LogicECM.module.FormsEditor.FieldComponent", fieldHtmlId, ["container"]);
		this.formIds = [];
		return this;
	};

	YAHOO.extend(LogicECM.module.FormsEditor.FieldComponent, Alfresco.component.Base, {
		options: {
			disabled: false,
			mandatory: false,
			itemNodeRef: null,
			value: null,
			fromForm: true,
			updateOnAction: null,
			defaultOption: null
		},

		targetType: null,
		config: null,

		onReady: function() {
			YAHOO.util.Event.on(this.id + "-select", "change", this.onSelectChange, this, true);
			if (this.options.fromForm && this.options.itemNodeRef) {
				this.getTargetType();
			} else {
				if (this.options.updateOnAction) {
					YAHOO.Bubbling.unsubscribe(this.options.updateOnAction, this.getTargetTypeFromEvent, this);
					YAHOO.Bubbling.on(this.options.updateOnAction, this.getTargetTypeFromEvent, this);
				}
			}
		},

		getTargetTypeFromEvent: function(layer, args) {
			var type = args[1].selectedItem;
			if (type) {
				this.loadConfig(type);
			}
		},

		getTargetType: function() {
			Alfresco.util.Ajax.jsonGet({
				url:  Alfresco.constants.PROXY_URI + "/lecm/docforms/attribute?nodeRef=" + encodeURIComponent(this.options.itemNodeRef),
				successCallback: {
					fn: function (response) {
						var oResults = response.json;
						if (oResults && oResults.targetType) {
							this.loadConfig(oResults.targetType)
						}
					},
					scope: this
				}
			});
		},

		loadConfig: function(type) {
			this.targetType = type;
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/forms/getConfig?action=getControlsById&typeId=" + encodeURIComponent(type),
				successCallback: {
					fn: function (response) {
						var oResults = response.json;
						if (oResults) {
							this.applyConfig(oResults);
						}
					},
					scope: this
				}
			});
		},

		applyConfig: function(configObj) {
			this.config = configObj;
			if (this.config) {
				var select = Dom.get(this.id + "-select");
				if (select) {
					while (select.firstChild) {
						select.removeChild(select.firstChild);
					}

					if (this.options.defaultOption) {
						option = document.createElement("option");
						option.innerHTML = this.options.defaultOption;
						select.appendChild(option);
					}

					for (var i = 0; i < this.config.length; i++) {
						var conf = this.config[i];

						var option = document.createElement("option");
						option.innerHTML = conf.displayName;
						if (this.options.value && this.options.value.id == conf.id) {
							option.selected = true;

							var valueParams = this.options.value.params;
							var confParams = conf.params;
							if (valueParams && confParams) {
								for (var j = 0; j < confParams.length; j++) {
									for (var k = 0; k < valueParams.length; k++) {
										if (valueParams[k].name == confParams[j].id) {
											confParams[j].value = valueParams[k].value;
										}
									}
								}
							}
						}
						select.appendChild(option);
					}
					this.onSelectChange();
				}
			}
		},

		onSelectChange: function() {
			var select = Dom.get(this.id + "-select");
			if (select) {
				var tableParams = Dom.get(this.id + "-params");
				var hiddenParams = Dom.get(this.id + "-hidden-params");
				if (tableParams && hiddenParams) {
					tableParams.innerHTML = "";
					hiddenParams.innerHTML = "";

					var conf = this.config[select.selectedIndex -1];
					if (conf && conf.params) {
						for (var i = 0; i < conf.params.length; i++) {
							var param = conf.params[i];
							if (param.visible){
								var paramRow = document.createElement("tr");

								var parNameTd = document.createElement("td");
								parNameTd.innerHTML = param.localName;
								if (param.mandatory) {
									parNameTd.innerHTML += "<span class='mandatory-indicator'>*</span>";
								}
								paramRow.appendChild(parNameTd);

								YAHOO.util.Event.onAvailable(param.id, this.attachChangeParamsListener, param.id, this);

								var parValueTd = document.createElement("td");
								parValueTd.innerHTML = "<input type='text' id='" + param.id + "' value='" + param.value + "' class='formFieldControlParams'/>";
								paramRow.appendChild(parValueTd);

								tableParams.appendChild(paramRow);
							} else {
								hiddenParams.innerHTML += "<input type='hidden' id='" + param.id + "' value='" + param.value + "' class='formFieldControlParams'>";
							}
						}
					}
				}
			}

			this.updateComponentValue();
		},

		attachChangeParamsListener: function (id) {
			YAHOO.util.Event.on(id, 'change', this.updateComponentValue, {}, this);
		},

		updateComponentValue: function() {
			var el = Dom.get(this.id);
			if (el) {
				var value = "";
				var select = Dom.get(this.id + "-select");
				if (select) {
					var conf = this.config[select.selectedIndex -1];
					if (conf) {
						var obj = {
							template: conf.templatePath,
							displayName: conf.displayName,
							id: conf.id,
							params: []
						};

						if (conf.params) {
							for (var i = 0; i < conf.params.length; i++) {
								var paramEl = Dom.get(conf.params[i].id);

								if (paramEl) {
									obj.params.push({
										name: conf.params[i].id,
										value: paramEl.value
									});
								}
							}
						}
						value = JSON.stringify(obj);
					}
				}
				el.value = value;
			}
		}
	});
})();