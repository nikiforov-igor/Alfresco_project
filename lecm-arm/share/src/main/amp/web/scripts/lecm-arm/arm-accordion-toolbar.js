if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.ARM.AccordionToolbar = function(htmlId) {
		LogicECM.module.ARM.AccordionToolbar.superclass.constructor.call(this, "LogicECM.module.ARM.AccordionToolbar", htmlId);
		YAHOO.Bubbling.on("updateArmToolbar", this.onUpdateArmToolbar, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.AccordionToolbar, LogicECM.module.Base.Toolbar);

	YAHOO.lang.augmentObject(LogicECM.module.ARM.AccordionToolbar.prototype, {
		doubleClickLock: false,
		_initButtons: function() {
			this.toolbarButtons["defaultActive"].newDocumentButton = new YAHOO.widget.Button(
					this.id + "-newDocumentButton",
					{
						type: "menu",
						menu: [],
						disabled: true
					}
			);
		},
		onNewRow: function(p_sType, p_aArgs, p_oItem) {
			var destination = p_oItem.destination,
					itemType = p_oItem.type,
					typeLabel = p_oItem.typeLabel;
			this.showCreateDialog({itemType: itemType, nodeRef: destination, typeLabel: typeLabel});
		},
		showCreateDialog: function(meta) {
			if (this.doubleClickLock)
				return;
			this.doubleClickLock = true;
			// Intercept before dialog show
			var me = this;

			var templateRequestParams = {
				itemKind: "type",
				itemId: meta.itemType,
				destination: meta.nodeRef,
				mode: "create",
				formId: meta.createFormId ? meta.createFormId : "",
				submitType: "json",
				showCancelButton: true
			};

			// Using Forms Service, so always create new instance
			var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
			createDetails.setOptions({
				width: "84em",
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
				templateRequestParams: templateRequestParams,
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					scope: this,
					fn: function(p_form, p_dialog) {
						var contId = p_dialog.id + "-form-container";
						var addMsg = meta.addMessage;
						var defaultMsg = this.msg("label.create-row.title") + " " + meta.typeLabel;
						p_dialog.dialog.setHeader(addMsg ? addMsg : defaultMsg);

						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);

						Dom.addClass(contId, "metadata-form-edit");
						this.doubleClickLock = false;
					}
				},
				onSuccess: {
					scope: this,
					fn: function(response) {
						var documentPageTemplate = "{protocol}//{host}{pageContext}document?nodeRef={nodeRef}";

						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("message.save.success")
						});

						window.location.href = YAHOO.lang.substitute(documentPageTemplate, {
							protocol: window.location.protocol,
							host: window.location.host,
							pageContext: Alfresco.constants.URL_PAGECONTEXT,
							nodeRef: response.json.persistedObject
						});

						this.doubleClickLock = false;
					}
				},
				onFailure: {
					scope: this,
					fn: function(response) {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("message.save.failure")
						});
						me.doubleClickLock = false;
					}
				}
			}).show();
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
								typeLabel: type.label,
								destination: type.draftFolder
							},
							scope: this
						}
					});
				}
				if (YAHOO.util.Dom.inDocument(menu.element)) {
					menu.clearContent();
					menu.addItems(items);
					menu.render();
				} else {
					menu.itemData = items;
				}
			} else if (YAHOO.util.Dom.inDocument(menu.element)) {
				menu.clearContent();
				menu.render();
			}
			button.set("disabled", !hasCreateTypes);
		}
	}, true);
})();
