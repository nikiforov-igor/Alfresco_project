if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

if (typeof LogicECM.Scanner == "undefined" || !LogicECM.Scanner) {
		LogicECM.Scanner = {};
	}

(function()
{
	var MENU_IFRAME_ID = "IFRMenu",
		HEADER_IFRAME_ID = "IFRHeader"; 

	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Element = YAHOO.util.Element,
		page,
		headerComponent,
		headerElement,
		headerId,
		menuFrame,
		menuElement,
		headerFrame,
		menuMetrics,
		headerMetrics;

	
	LogicECM.Scanner.Page = function(htmlId)
	{
		LogicECM.Scanner.Page.superclass.constructor.call(this, "LogicECM.Scanner.Page", htmlId, ["container", "json"]);
		me = this;
		return this;
	};

	YAHOO.extend(LogicECM.Scanner.Page, Alfresco.component.Base,
		{

			setListeners: function() {
				var items,
					itemsLength,
					btn,
					menuId,
					i;

				function onMenuHide() {
					me.menuFrame.style.display = "none";
				}

				function onMenuShow() {
					me.resetMetrics();
					me.menuFrame.width = me.menuMetrics.width;
					me.menuFrame.height = me.menuMetrics.height;
					me.menuFrame.style.top = me.menuMetrics.top.toString() + 'px';
					me.menuFrame.style.left = me.menuMetrics.left.toString() + 'px';
					me.menuFrame.style.zIndex = me.menuElement.style.zIndex - 1;
					me.menuFrame.style.display = "block";
				}

				menuId = this.headerId + "-app_more-button";
				items = this.headerComponent.appItems;
				itemsLength = items.length;
				for(i = 0; i < itemsLength; i++){
					btn = items[i];
					if(YAHOO.lang.isObject(btn._button) && btn._button.id == menuId){
						btn.on("click", onMenuShow);
						btn.getMenu().subscribe("hide", onMenuHide);
					}
				}
			},

			resetMetrics: function(){
				this.headerMetrics = this.headerElement.getBoundingClientRect();
				this.menuMetrics = this.menuElement.getBoundingClientRect();
			},

			init: function() {
				this.page = document.body;
				this.headerComponent = Alfresco.util.ComponentManager.find({name: "Alfresco.component.Header"})[0];
				this.headerId = this.headerComponent.id;
				this.headerElement = document.getElementById(this.headerId).getElementsByClassName("header")[0];
				this.menuElement = document.getElementById(this.headerId + '-appmenu_more');

				me.resetMetrics();

				this.menuFrame = document.getElementById("IFRMenu");
				this.headerFrame = document.getElementById("IFRHeader");

				this.page.appendChild(this.menuFrame);
				this.page.appendChild(this.headerFrame);

				this.headerFrame.width = this.headerMetrics.width;
				this.headerFrame.height = this.headerMetrics.height;
				this.headerFrame.style.top = this.headerMetrics.top.toString() + 'px';
				this.headerFrame.style.left = this.headerMetrics.left.toString() + 'px';
				this.headerFrame.style.display = "block";

				this.setListeners();

			},

			_onMenuHide: function() {
				this.menuFrame.style.display = "none";
			},

			_onMenuShow: function() {
				this.menuFrame.width = this.menuMetrics.width;
				this.menuFrame.height = this.menuMetrics.height;
				this.menuFrame.style.top = this.menuMetrics.top.toString() + 'px';
				this.menuFrame.style.left = this.menuMetrics.left.toString() + 'px';
				this.menuFrame.style.zIndex = this.menuElement.style.zIndex - 1;
				this.menuFrame.style.display = "block";
			},

			onReady: function ()
			{
				
			}
		});
})();

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Element = YAHOO.util.Element;

	LogicECM.Scanner.Util = function(htmlId)
	{
		LogicECM.Scanner.Util.superclass.constructor.call(this, "LogicECM.Scanner.Util", htmlId, ["container", "json"]);
		return this;
	};

	YAHOO.extend(LogicECM.Scanner.Util, Alfresco.component.Base,
		{

			showModal: function(opts) {
				var dialog, 
					appletTemplate,
					applet,
					page;

				var close = function(event) {
					this.destroy();
				};

				if(opts.targetURL == null || opts.targetURL.length == 0){
					opts.targetURL = Alfresco.constants.PROXY_URI + '/scanUpload';
				}

				if(opts.fileName == null || opts.fileName.length == 0){
					opts.fileName = 'scan';
				}

				opts.appletURL = Alfresco.constants.URL_RESCONTEXT + '/scripts/lecm-scaner/scanner-applet.jar';
				appletTemplate = '<applet code="com.aplana.scanner.ScannerApplet" id="scanner-applet"' +
								 'archive="{appletURL}"' +
								 'width="100%" height="100%">' +
								 '<param name="targetUrl" value="{targetURL}"/>' +
								 '<param name="filename" value="{fileName}"/>' +
								 '</applet>';

				applet = YAHOO.lang.substitute(appletTemplate, opts);

				dialog = new YAHOO.widget.SimpleDialog("simpledialog1", 
						{ 
							width: opts.width,
							height: opts.height,
						    fixedcenter: true,
						    visible: false,
						    draggable: true,
						    close: true,
						    text: applet				    
					    });

				dialog.hide = close;
				dialog.setHeader('Сканирование');
				dialog.render(document.body);
				dialog.show();
			},

			onReady: function ()
			{
				
			}
		});
})();