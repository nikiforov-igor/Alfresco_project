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


(function () {
	var Dom = YAHOO.util.Dom,
		Bubbling = YAHOO.Bubbling,
		Event = YAHOO.util.Event;


	LogicECM.module.SelectDocumentCategory = function (fieldHtmlId, updateOnEvent) {
		LogicECM.module.SelectDocumentCategory.superclass.constructor.call(this, "LogicECM.module.SelectDocumentCategory", fieldHtmlId, [ "container", "datasource"]);
		Bubbling.on(updateOnEvent, this.onChangeAttach, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.SelectDocumentCategory, Alfresco.component.Base,
		{
			options: {
				mandatory: false,
				notSelectedOptionShow: false,
				selectedValue: null,
				documentNodeRef: null,
				documentType: null,
				changerKind: null
			},

			selectItem: null,

			onReady: function () {
				this.selectItem = Dom.get(this.id);
				Event.on(this.id, "change", this.onSelectChange, this, true);

				if ((this.options.documentNodeRef && this.options.changerKind == "node") ||
					(this.options.documentType && this.options.changerKind == "type")) {
					this.loadCategories();
				}
			},

			onSelectChange: function () {
				if (this.options.mandatory) {
					YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
				}
			},

			onChangeAttach: function (layer, args) {
				this.clearSelect();
				if (args[1]) {
					if (this.options.changerKind == "node") {
						var keys = Object.keys(args[1].selectedItems);
						if (keys.length == 1) {
							this.options.documentNodeRef = keys[0];
						}
					} else if (this.options.changerKind == "type") {
						this.options.documentType = args[1].selectedItem;
					}
					this.loadCategories();

				}
			},

			populateSelect: function (items) {
				var i, item, opt;
				if (items && items.length) {
					for (i = 0; i < items.length; i++) {
						item = items[i];
						if (this.options.changerKind == "node") {
							if (!item.isReadOnly) {
								opt = document.createElement('option');
								opt.innerHTML = item.name;
								opt.value = item.name;
								if (item.nodeRef == this.options.selectedValue) {
									opt.selected = true;
								}
								this.selectItem.appendChild(opt);
							}
						} else if (this.options.changerKind == "type") {
							opt = document.createElement('option');
							opt.innerHTML = item.name;
							opt.value = item.name;
							this.selectItem.appendChild(opt);
						}
					}
				}

				this.onSelectChange();
			},

			clearSelect: function () {
				if (this.options.notSelectedOptionShow && this.selectItem != null) {
					var emptyOption = this.selectItem.options[0];
					var emptOpt = document.createElement('option');
					emptOpt.innerHTML = emptyOption.innerHTML;
					emptOpt.value = emptyOption.value;

					this.selectItem.innerHTML = "";
					this.selectItem.appendChild(emptOpt);
				}
			},

			loadCategories: function () {
				var me = this;
				var callback = {
					success:function (oResponse) {
						var oResults = eval("(" + oResponse.responseText + ")");
						if (oResults != null && oResults.categories != null) {
							me.populateSelect(oResults.categories);
						}
					},
					failureMessage: "message.failure",
					timeout:10000
				};
				if (this.options.documentNodeRef && this.options.changerKind == "node"){
					sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/attachments/api/categories?documentNodeRef=" + this.options.documentNodeRef;
					YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
				} else if (this.options.documentType && this.options.changerKind == "type") {
					sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/attachments/api/categoriesByType?documentType=" + this.options.documentType;
					YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
				}
			}
		});
})();