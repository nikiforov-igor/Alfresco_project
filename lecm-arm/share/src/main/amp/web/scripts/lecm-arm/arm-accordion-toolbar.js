if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
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
            Event.on(document,'keypress',function(e) {
                var kc = Event.getCharCode(e);
                if (e.ctrlKey && kc == 9 /*Y*/) {
                    newDocumentButton._showMenu(e);
                    e.preventDefault();
                    e.stopPropagation();
                }
            },this,true);
		},
		onNewRow: function(p_sType, p_aArgs, p_oItem) {
			window.location.href = Alfresco.constants.URL_PAGECONTEXT + "document-create?documentType=" + p_oItem.type + "&" + LogicECM.module.Base.Util.encodeUrlParams("documentType=" + p_oItem.type);
		},
		onUpdateArmToolbar: function(layer, args) {
			var createTypes = args[1].createTypes;
			var button = this.toolbarButtons["defaultActive"].newDocumentButton;
			var menu = button.getMenu();
			var hasCreateTypes = createTypes && createTypes.length > 0;
			if (hasCreateTypes) {
				var items = [];
				for (var i = 0; i < createTypes.length; i++) {
					var type = createTypes[i];
					items.push({
						text: type.label,
						value: type.type,
						disabled: type.disabled,
						onclick: {
							fn: this.onNewRow,
							obj: {
								type: type.type,
								typeLabel: type.label
							},
							scope: this
						}
					});
				}
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
