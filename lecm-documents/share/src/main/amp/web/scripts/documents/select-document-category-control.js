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


	LogicECM.module.SelectDocumentCategory = function (fieldHtmlId) {
		LogicECM.module.SelectDocumentCategory.superclass.constructor.call(this, "LogicECM.module.SelectDocumentCategory", fieldHtmlId, [ "container", "datasource"]);

		Bubbling.on("changeAttachToDocument", this.onChangeAttachToDocument, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.SelectDocumentCategory, Alfresco.component.Base,
		{
			options: {
				mandatory: false,
				notSelectedOptionShow: false
			},

			selectItem: null,
			currentSelectedDocument: null,

			onReady: function () {
				this.selectItem = Dom.get(this.id);
				Event.on(this.id, "change", this.onSelectChange, this, true);
			},

			onSelectChange: function () {
				if (this.options.mandatory) {
					YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
				}
			},

			onChangeAttachToDocument: function (layer, args) {
				this.clearSelect();
				if (args[1] != null && args[1].selectedItems != null) {
					var keys = Object.keys(args[1].selectedItems);
					if (keys.length == 1) {
						this.loadCategories(keys[0]);
					}
				}
			},

			populateSelect: function (items) {
				if (items != null && items.length > 0) {
					for (var i = 0; i < items.length; i++) {
						var item = items[i];
						var opt = document.createElement('option');
						opt.innerHTML = item.name;
						opt.value = item.name;
						if (item.isReadOnly) {
							opt.disabled = "disabled";
						}
						this.selectItem.appendChild(opt);
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

			loadCategories: function (documentNodeRef) {
				if (documentNodeRef != null) {
					var sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/attachments/api/categories?documentNodeRef=" + encodeURIComponent(documentNodeRef);
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
					YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
				}
			}
		});
})();