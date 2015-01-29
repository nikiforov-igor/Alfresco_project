define(["dojo/_base/declare",
	"dijit/_WidgetBase",
	"alfresco/menus/AlfMenuBarPopup",
	"alfresco/menus/AlfMenuItem",
	"dojo/dom-class",
	"dojo/dom-construct"
],
	function (declare, _Widget, AlfMenuBarPopup, AlfMenuItem, domClass, domConstruct) {

		var Dom = YAHOO.util.Dom,
			Event = YAHOO.util.Event,
			KeyListener = YAHOO.util.KeyListener;

		return declare([_Widget, AlfMenuBarPopup], {
			searchInputId: "",
			emptyResultId: "",
			cssRequirements: [{
					cssFile: "./css/search-barcode.css",
					mediaType: "screen"
				}],
			showArrow: false,
			widgets: [{
					name: "alfresco/header/AlfMenuItem",
					config: {
						iconClass: "alf-loading-icon",
						label: "loading.label"
					}
				}],
			postCreate: function () {
				this.inherited(arguments);
				if (this.popup && this.popup.domNode) {
					domClass.add(this.popup.domNode, "alf-header-menu-bar");
					this.popup.onOpen = dojo.hitch(this, "initSearchField");

					// По комбинации клавиш Ctrl + Shift + F открывать диалог поиска по ШтрихКоду:
					new KeyListener(document, {ctrl: true, shift: true, keys: 70 /*F*/}, {
						fn: function (layer, args) {
							var e = args[1];
							this.domNode.click();
							Event.stopEvent(e);
						},
						scope: this,
						correctScope: true
					}, KeyListener.KEYDOWN).enable();
				}
			},
			initSearchField: function () {
				if (!this.searchInputId) {
					var searchInput;
					var popupId = this.popup.id;
					var container = Dom.get(popupId);
					container.innerHTML = "";

					this.searchInputId = popupId + '_search_bc';

					domConstruct.place('<input id="' + this.searchInputId + '" type="text" value="" class="search-barcode" onClick="this.select();"/>', this.popup.domNode);

					searchInput = Dom.get(this.searchInputId);

					new KeyListener(searchInput, {keys: KeyListener.KEY.ENTER}, {
						fn: this.searchBarcode,
						scope: this,
						correctScope: true
					}, KeyListener.KEYDOWN).enable();
					searchInput.focus();
				} else {
					this.hideEmptyResultMessage();
					Dom.get(this.searchInputId).select();
				}
			},
			searchBarcode: function () {
				var code = Dom.get(this.searchInputId).value;

				if (code && code.trim()) {
					Alfresco.util.Ajax.jsonGet({
						url: Alfresco.constants.PROXY_URI + "lecm/barcode/getNodeByDBID?dbid=" + encodeURIComponent(code) + "&noCache=" + new Date().getTime(),
						successCallback: {
							fn: function (response) {
								var nodeRef = response.json.nodeRef;

								if (nodeRef) {
									window.location.href = Alfresco.util.siteURL("document?nodeRef={nodeRef}", {
										nodeRef: nodeRef.toString()
									});
								} else {
									this.showEmptyResultMessage();
								}
							},
							scope: this
						},
						failureCallback: {
							fn: this.showEmptyResultMessage,
							scope: this
						}
					});
				} else {
					this.showEmptyResultMessage();
				}
			},
			showEmptyResultMessage: function () {
				this.domNode.click();

				if (this.emptyResultId) {
					Dom.setStyle(this.emptyResultId, "display", "block");
				} else {
					this.emptyResultId = this.popup.id + '_empty';
					domConstruct.place('<div id="' + this.emptyResultId + '" type="text" value="" class="empty-result">Документ не найден</div>', this.popup.domNode);
				}

				Dom.get(this.searchInputId).select();
			},
			hideEmptyResultMessage: function () {
				if (this.emptyResultId) {
					Dom.setStyle(this.emptyResultId, "display", "none");
				}
			}

		});
	});
