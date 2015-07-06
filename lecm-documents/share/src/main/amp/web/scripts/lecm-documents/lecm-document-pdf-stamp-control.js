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

		return this;
	};

	YAHOO.extend(LogicECM.module.Documents.PdfMarkupControl, Alfresco.component.Base,
		{
			options:{
				itemId: null,
				datasource: null,
				code: null
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

			onReady:function () {
				YAHOO.Bubbling.fire("stampControlReady",
					{
						eventGroup: this
					});
				this.loadDocument();
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
								if (response.json != null && response.json.hasOwnProperty("document")) {
									this.loadAttachmentPreview(response.json);
								} else {
									Dom.get(this.id +"-error").style.display = "block";
								}
							},
							scope: this
						}
					});
				}
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

			},

			moveCursor: function moveCursor(ev) {
				if (this.markups[ev.currentTarget.id] == null) {
					var markup = document.createElement("div");
					Dom.addClass(markup, "pdf-markup");
					var width = Math.round(this.markupWidth * this.scale);
					var height = Math.round(this.markupHeight * this.scale)
					markup.style.width =  width + "px";
					markup.style.height = height + "px";
					var page = document.getElementById(ev.currentTarget.id);
					page.appendChild(markup)
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
				this.markups[ev.currentTarget.id].style.top = ((Dom.getY(ev.target) - Dom.getY(ev.currentTarget) - Math.round(this.markupHeight * this.scale / 2)) + ev.layerY) + "px";
				this.markups[ev.currentTarget.id].style.left = ((Dom.getX(ev.target) - Dom.getX(ev.currentTarget) - Math.round(this.markupWidth * this.scale / 2)) + ev.layerX) + "px";
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
					var height = Math.round(this.markupHeight * this.scale)
					markup.style.width =  width + "px";
					markup.style.height = height + "px";
					var page = document.getElementById(ev.currentTarget.id);
					page.appendChild(markup)
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
				this.selectedMarkups[ev.currentTarget.id].realX = Math.round(x * (1 / this.scale));
				this.selectedMarkups[ev.currentTarget.id].realY = Math.round(y * (1 / this.scale));
				this.selectedMarkups[ev.currentTarget.id].style.left = x + "px";
				this.selectedMarkups[ev.currentTarget.id].style.top =  y + "px";

				var result = {
					page: parseInt(ev.currentTarget.id.replace(this.id + "-preview-container-viewer-pageContainer-", "")),
					x: x,
					y: y,
					width: parseInt(ev.currentTarget.style.width),
					height: parseInt(ev.currentTarget.style.height),
					stamp: this.stampNodeRef,
					attach: this.documentNodeRef,
					document: this.options.itemId
				};
				Dom.get(this.id).value = JSON.stringify(result);
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