/* global Alfresco, YAHOO */

if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM || {};

(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        KeyListener = YAHOO.util.KeyListener;
	LogicECM.module.ARM.AccordionToolbar = function(htmlId) {
		LogicECM.module.ARM.AccordionToolbar.superclass.constructor.call(this, "LogicECM.module.ARM.AccordionToolbar", htmlId);
		YAHOO.Bubbling.on("updateArmToolbar", this.onUpdateArmToolbar, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.AccordionToolbar, LogicECM.module.Base.Toolbar);

	YAHOO.lang.augmentObject(LogicECM.module.ARM.AccordionToolbar.prototype, {
		doubleClickLock: false,
		_initButtons: function() {
			var newDocumentButton = this.toolbarButtons["defaultActive"].newDocumentButton = new YAHOO.widget.Button(
                    this.id + "-newDocumentButton",
					{
						type: "menu",
						menu: [],
						disabled: true
					}
			);
            // По комбинации клавиш Ctrl + Y открывать меню создания документа:
            new KeyListener(document, {ctrl: true, keys: 89 /*Y*/},
                {
                    fn: function (layer, args) {
                        var e = args[1];
                        newDocumentButton._showMenu(e);
                        Event.stopEvent(e);
                    },
                    scope: this,
                    correctScope: true
                }, KeyListener.KEYDOWN).enable();

        },
		onNewRow: function(p_sType, p_aArgs, p_oItem) {
			var attributes = p_oItem.attributes ? p_oItem.attributes : [];
			if (this.currentNodeArgs.nodeType !== 'lecm-arm:node') {
				attributes.push({
					initial: {
						formsName: "selectedArmTreeNode",
						value: this.currentNodeArgs.nodeRef
					}
				});
			}
			var params = attributes.reduce(function(prev, curr) {
				return YAHOO.lang.substitute('{prev}&{key}={value}', {
					prev: prev,
					key: curr.readonly ? 'readonly_' + curr.initial.formsName : curr.initial.formsName,
					value:  encodeURIComponent(curr.initial.value)
				});
			}, 'documentType=' + encodeURIComponent(p_oItem.type));
			window.location.href = Alfresco.constants.URL_PAGECONTEXT + p_oItem.page + "?documentType=" + p_oItem.type + "&" + LogicECM.module.Base.Util.encodeUrlParams(params);
		},
		onUpdateArmToolbar: function(layer, args) {
			var createTypes = args[1].createTypes;
			var button = this.toolbarButtons["defaultActive"].newDocumentButton;
			var menu = button.getMenu();
			var hasCreateTypes = createTypes && createTypes.length > 0;
			if (hasCreateTypes) {
				var items = createTypes.map(function (type) {
					var page = (type.page) ? type.page : "document-create";
					var item = {
						text: type.label,
						value: type.type,
						disabled: type.disabled,
						onclick: {
							fn: this.onNewRow,
							obj: {
								type: type.type,
								typeLabel: type.label,
								page: page
							},
							scope: this
						}
					};
					if (type.templates && type.templates.length) {
						item.submenu = {
							id: type.type,
							itemData: type.templates.map(function (template) {

								if (template.attributes) {
									template.attributes.push({
										initial: {
											dataType: "d:text",
											formsName: "prop_lecm-document-aspects_template-name",
											attribute: "lecm-document-aspects:template-name",
											type: "property",
											value: template.name
										}
									});
									template.attributes.push({
										initial: {
											dataType: "d:text",
											formsName: "prop_lecm-document-aspects_template-ref",
											attribute: "lecm-document-aspects:template-ref",
											type: "property",
											value: template.ref
										}
									});
								}

								return {
									text: template.name,
									value: type.type,
									disabled: type.disabled,
									onclick: {
										fn: this.onNewRow,
										obj: {
											type: type.type,
											typeLabel: type.label,
											page: page,
											attributes: template.attributes
										},
										scope: this
									}
								};
							}, this)
						};
					}
					return item;
				}, this);
				if (Dom.inDocument(menu.element)) {
					menu.clearContent();
					menu.addItems(items);
					menu.render();
				} else {
					menu.itemData = items;
				}
			} else if (Dom.inDocument(menu.element)) {
				menu.clearContent();
				menu.render();
			}
			button.set("disabled", !hasCreateTypes);
		}
	}, true);
})();
