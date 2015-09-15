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
 * LogicECM top-level control namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.control
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.Documents = LogicECM.module.Documents || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.module.Documents.PdfMarkupControl = function (fieldHtmlId) {
		LogicECM.module.Documents.PdfMarkupControl.superclass.constructor.call(this, "LogicECM.module.Documents.PdfMarkupControl", fieldHtmlId, [ "container"]);
		YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
		YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);

		return this;
	};

	YAHOO.extend(LogicECM.module.Documents.PdfMarkupControl, Alfresco.component.Base,
		{
			options:{
				itemId: null,
				datasource: null,
				code: null,
				fieldId: null,
				formId: null
			},

			markupWidth: 100,

			markupHeight: 50,

			stampImage: null,

			stampNodeRef: null,

			documentNodeRef: null,

			markups: [],

			selectedMarkups: [],

			webPreview: null,

			_zoomFunction: null,

			scale: 1,

			previousStamps: [],


			onReady:function () {
				YAHOO.Bubbling.fire("stampControlReady",
					{
						eventGroup: this
					});

				if (!this.isNodeRef(this.options.itemId)) {
					Alfresco.util.Ajax.request({
						method: "GET",
						url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/workflow/GetDocumentDataByTaskId?taskID=" + this.options.itemId,
						requestContentType: "application/json",
						responseContentType: "application/json",
						successCallback: {
							fn: function (response) {
								var result = response.json;
								if (result != null && result.nodeRef != null) {
									this.options.itemId = result.nodeRef;
									this.loadDocument();
								}
							},
							scope: this
						}
					});
				} else {
					this.loadDocument();
				}
				LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
			},

			onDisableControl: function (layer, args) {
				if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					var input = Dom.get(this.id);
					if (input != null) {
						input.disabled = true;
					}
				}
			},

			onEnableControl: function (layer, args) {
				if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					if (!this.options.disabled) {
						var input = Dom.get(this.id);
						if (input != null) {
							input.disabled = false;
						}
					}
				}
			},

			loadDocument: function() {
				if (this.options.itemId != null) {
					var url = Alfresco.constants.PROXY_URI_RELATIVE + this.options.datasource;
					if (url.indexOf("?") > 0) {
						url += "&";
					} else {
						url += "?";
					}
					url += "nodeRef=" + this.options.itemId;
					url += "&code=" + encodeURIComponent(this.options.code);
					Alfresco.util.Ajax.request({
						method: "GET",
						url: url,
						successCallback: {
							fn: function (response) {
								if (response.json != null) {
									if (response.json.hasOwnProperty("document")) {
										this.loadAttachmentPreview(response.json);
									} else {
										if (response.json.hasOwnProperty("error") && "STAMP.NULL" == response.json.error) {
											Dom.get(this.id +"-error-stamp").style.display = "block";
										} else {
											Dom.get(this.id +"-error").style.display = "block";
										}
									}
								} else {
									Dom.get(this.id +"-error").style.display = "block";
								}
							},
							scope: this
						}
					});
				}
			},

			isNodeRef: function (value)	{
				var regexNodeRef = new RegExp(/^[^\:^ ]+\:\/\/[^\:^ ]+\/[^ ]+$/);
				var result = false;
				try {
					result = regexNodeRef.test(String(value));
				}
				catch (e){}
				return result;
			},

			loadAttachmentPreview: function(settings) {
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "components/preview/web-preview",
						dataObj: {
							nodeRef: settings.document,
							htmlid: this.id + "-preview-container"
						},
						successCallback: {
							fn:function(response){
								var previewId = this.id + "-preview-container-viewer-pageContainer-1";
								Event.onContentReady(previewId, function() {
									this.initMarkup(settings);
								}, {}, this);
								Dom.get(this.id + "-preview-container").innerHTML = response.serverResponse.responseText;
								var dialog = LogicECM.module.Base.Util.getLastDialog();
								if (dialog != null) {
									dialog.dialog.center();
								}
							},
							scope: this
						},
						failureMessage: this.msg("message.failure"),
						scope: this,
						execScripts: true
					});
			},

			initMarkup: function initMarkup_function(settings) {
				var nodes = Dom.getElementsByClassName('page', 'div', this.id + "-preview-container-viewer");
				Dom.addClass(nodes, "pdf-markup-canvas");
				Event.on(nodes, 'mousemove', this.moveCursor.bind(this));
				Event.on(nodes, 'mouseout', this.outCursor.bind(this));
				Event.on(nodes, 'click', this.clickCursor.bind(this));

				this.webPreview = Alfresco.util.ComponentManager.findFirst("Alfresco.WebPreview");
				this._zoomFunction = this.webPreview.plugin._updateZoomControls;
				this.webPreview.plugin._updateZoomControls = this.onZoom.bind(this);
				this.scale = this.webPreview.plugin.documentView.currentScale;
				var width = parseInt(Dom.get(this.id + "-preview-container-viewer-pageContainer-1").style.width);
				var tScale = width / settings.pageWidth;
				this.markupWidth = Math.round(settings.stampWidth * tScale / this.scale) ;
				this.markupHeight = Math.round(settings.stampHeight * tScale / this.scale);

				this.stampNodeRef = settings.stamp;
				this.documentNodeRef = settings.document;

				if (settings.stamp) {
					this.stampImage = settings.stamp;
				}
				if (settings.prevStamps) {
					this.previousStamps = eval("(" + settings.prevStamps + ")");
				}
			},

			moveCursor: function moveCursor(ev) {
				if (this.markups[ev.currentTarget.id] == null) {
					var markup = document.createElement("div");
					Dom.addClass(markup, "pdf-markup");
					var width = Math.round(this.markupWidth * this.scale);
					var height = Math.round(this.markupHeight * this.scale);
					markup.style.width =  width + "px";
					markup.style.height = height + "px";
					var page = document.getElementById(ev.currentTarget.id);
					page.appendChild(markup);
					if (this.stampImage != null) {
						var stamp = document.createElement("img");
						stamp.width = width;
						stamp.height = height;
						stamp.src = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + this.stampImage.replace("://", "/");
						markup.appendChild(stamp);
					}
					this.markups[ev.currentTarget.id] = markup;
				}

				this.markups[ev.currentTarget.id].style.display = "block";

				var x = ((Dom.getX(ev.target) - Dom.getX(ev.currentTarget) - Math.round(this.markupWidth * this.scale / 2)) + ev.layerX);
				var y = ((Dom.getY(ev.target) - Dom.getY(ev.currentTarget) - Math.round(this.markupHeight * this.scale / 2)) + ev.layerY);
				var p = parseInt(ev.currentTarget.id.replace(this.id + "-preview-container-viewer-pageContainer-", ""));

				this.markups[ev.currentTarget.id].style.top = y + "px";
				this.markups[ev.currentTarget.id].style.left = x + "px";

				if (!this.crossWithPrevious(p, x, y)) {
					Dom.removeClass(this.markups[ev.currentTarget.id], "pdf-error-markup");
				} else {
					Dom.addClass(this.markups[ev.currentTarget.id], "pdf-error-markup");
				}
			},

			outCursor: function outCursor(ev) {
				var id = "";
				if (this.stampImage != null) {
					id = ev.target.parentElement.parentElement.id
				} else {
					id = ev.target.parentElement.id
				}
				if (this.markups[id] != null) {
					this.markups[id].style.display = "none";
				}
			},

			clickCursor: function clickCursor(ev) {
				for (var key in this.selectedMarkups) {
					this.selectedMarkups[key].style.display = "none";
				}
				if (this.selectedMarkups[ev.currentTarget.id] == null) {
					var markup = document.createElement("div");
					Dom.addClass(markup, "pdf-markup-selected");
					var width = Math.round(this.markupWidth * this.scale);
					var height = Math.round(this.markupHeight * this.scale);
					markup.style.width = width + "px";
					markup.style.height = height + "px";
					var page = document.getElementById(ev.currentTarget.id);
					page.appendChild(markup);
					if (this.stampImage != null) {
						var stamp = document.createElement("img");
						stamp.width = width;
						stamp.height = height;
						stamp.src = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + this.stampImage.replace("://", "/");
						markup.appendChild(stamp);
					}
					this.selectedMarkups[ev.currentTarget.id] = markup;
				}

				this.selectedMarkups[ev.currentTarget.id].style.display = "block";
				var x = ((Dom.getX(ev.target) - Dom.getX(ev.currentTarget) - Math.round(this.markupWidth * this.scale / 2)) + ev.layerX);
				var y = ((Dom.getY(ev.target) - Dom.getY(ev.currentTarget) - Math.round(this.markupHeight * this.scale / 2)) + ev.layerY);
				var p = parseInt(ev.currentTarget.id.replace(this.id + "-preview-container-viewer-pageContainer-", ""));

				if (!this.crossWithPrevious(p, x, y)) {
					this.selectedMarkups[ev.currentTarget.id].realX = Math.round(x * (1 / this.scale));
					this.selectedMarkups[ev.currentTarget.id].realY = Math.round(y * (1 / this.scale));
					this.selectedMarkups[ev.currentTarget.id].style.left = x + "px";
					this.selectedMarkups[ev.currentTarget.id].style.top = y + "px";

					var result = {
						page: p,
						x: x,
						y: y,
						width: parseInt(ev.currentTarget.style.width),
						height: parseInt(ev.currentTarget.style.height),
						stamp: this.stampNodeRef,
						attach: this.documentNodeRef,
						document: this.options.itemId
					};
					Dom.get(this.id).value = JSON.stringify(result);
				} else {
					var container = Dom.get(this.id + "-preview-container-viewer-pageContainer");
					Alfresco.util.PopupManager.displayMessage(
						{
							text: this.msg("message.stamp.not.allowed")
						}, container ? container : ev.currentTarget);
				}
			},

			crossWithPrevious: function (page, x, y) {
				var width = Math.round(this.markupWidth * this.scale);
				var height = Math.round(this.markupHeight * this.scale);

				for (var i = 0; i < this.previousStamps.length; i++) {
					var prevStamp = this.previousStamps[i];
					if (prevStamp && prevStamp.p == page) {
						var x1 = prevStamp.x * this.scale - width;
						var x2 = (prevStamp.x + prevStamp.width) * this.scale;
						var y1 = prevStamp.y * this.scale- height;
						var y2 = (prevStamp.y + prevStamp.height) * this.scale;

						if (x1 < x && x < x2 && y1 < y && y < y2) {
							return true;
						}
					}
				}
				return false;
			},

			onZoom: function setScale(ev) {
				this._zoomFunction.bind(this.webPreview.plugin)();
				this.scale = this.webPreview.plugin.documentView.currentScale
				for (var key in this.selectedMarkups) {
					var markup = this.selectedMarkups[key];
					var width = Math.round(this.markupWidth * this.scale);
					var height = Math.round(this.markupHeight * this.scale)
					markup.style.width = width + "px";
					markup.style.height =  height + "px";
					markup.style.left = Math.round(markup.realX * this.scale) + "px";
					markup.style.top = Math.round(markup.realY * this.scale) + "px";
					if (markup.children[0] != null) {
						markup.children[0].width = width;
						markup.children[0].height = height;
					}
				}
				for (var key in this.markups) {
					var markup = this.markups[key];
					var width = Math.round(this.markupWidth * this.scale);
					var height = Math.round(this.markupHeight * this.scale)
					markup.style.width = width + "px";
					markup.style.height =  height + "px";
					markup.style.left = Math.round(markup.realX * this.scale) + "px";
					markup.style.top = Math.round(markup.realY * this.scale) + "px";
					if (markup.children[0] != null) {
						markup.children[0].width = width;
						markup.children[0].height = height;
					}
				}
			}


		});
})();