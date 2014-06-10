if (typeof IT == "undefined" || !IT) {
	var IT = {};
}

IT.widget = IT.widget || {};

(function() {
	var Dom = YAHOO.util.Dom, Lang = YAHOO.util.Lang, Button = YAHOO.widget.Button, Bubbling = YAHOO.Bubbling; 
	var DEFAULT_CONFIG = {
			"ELEMENTS": { 
				key: "elements", 
				value: {}
			},
			"CLEARFORM": { 
				key: "clearform", 
				value: true
			}
	};
			

	IT.widget.Dialog = function IT_widget_Dialog(id, config) {
		IT.widget.Dialog.superclass.constructor.call(this, id || YAHOO.util.Dom.generateId(), config );

		var header = document.createElement("div");
		// Dom.addClass(header, "hd");
		header.innerHTML = "Добавить";

		// YAHOO.util.Event.addListener(header, "mousemove", YAHOO.util.DragDropMgr.handleMouseMove);
		var oSpan = document.createElement("span");
		if (Button) {
			oYUIButton = new Button({ label: "Добавить", type: "button" });
			//oYUIButton.set("id", id+"_btn");
			oYUIButton.appendTo(oSpan);
			oYUIButton.set("onclick", { 
				fn: this._addClick, 
				obj: this, 
				scope: this 
			});
		}
		this.setHeader(header);
		// Хак для возможности перемещения формы, почему-то по дефолту не ставится
		this.cfg.addProperty("draggable", {
			handler: this.configDraggable,
			value: (YAHOO.util.DD) ? true : false,
			validator: function (o) {
				return typeof o === 'boolean';
			},
			supercedes: ["visible"]
		});
		this.setFooter(oSpan);
		var els = this.cfg.getProperty("elements");
		this.beforeShowEvent.subscribe(this.handleShow);
		
		//Bubbling.on("visibleUpdated", this.onShowElements, this);
	};

	YAHOO.extend(IT.widget.Dialog,  YAHOO.widget.Panel, 
	{
		//_elements: [],
		handleShow: function(event, obj) {
			if(this.cfg.getProperty("clearform")) {
				function isFormElement(p_oElement) {
					var sTag = p_oElement.tagName.toUpperCase();
					return ((sTag == "INPUT" || sTag == "TEXTAREA" || 
							sTag == "SELECT"));
				}
				var oElements = Dom.getElementsBy(isFormElement, "*", this.element);
			
				for(var i in oElements) {
					oElements[i].value = "";
				}
			}
			Bubbling.fire("visibleUpdated");
			Bubbling.fire("refreshElement");
		},
		_addClick: function(evt, obj) {
			function isFormElement(p_oElement) {
				var sTag = p_oElement.tagName.toUpperCase();
				return ((sTag == "INPUT" || sTag == "TEXTAREA" || 
						sTag == "SELECT"));
			}
			var oElements = Dom.getElementsBy(isFormElement, "*", this.element);
			
			Bubbling.fire(obj.id+"hideEditDialog", 
			{
				fieldId: obj.id,
				args: oElements
			});
		},
		initElements: function (type, args, obj) {
			var val = args[0];
			
			//if(val.length>0) {
				//this.setHeader("Add parameter");
				//Dialog
				var oSpan = document.createElement("div");
				var set = document.createElement("div");
				Dom.addClass(set, "set");
				Dom.addClass(oSpan, "form-fields");
				Dom.setStyle(oSpan, "margin", 0);
				Dom.setStyle(oSpan, "width", "357px");

				//Elements
				for(var i in val) {
					switch (val[i].type) {
						case "input":
							var input = new IT.widget.Input(val[i]);
							input.render(set);
							//this._elements.push(input);
							break;
						case "select":
							var select = new IT.widget.Select(val[i]);
							select.render(set);
							//this._elements.push(select);
							break;
					}
				}
				oSpan.appendChild(set)
				this.setBody(oSpan);
			//}
		},
		initDefaultConfig: function () {
			
			IT.widget.Dialog.superclass.initDefaultConfig.call(this);

			var cfg = this.cfg;
			
			cfg.addProperty(DEFAULT_CONFIG.ELEMENTS.key, { 
				
				handler: this.initElements,
				value : DEFAULT_CONFIG.ELEMENTS.value
			});
			
			cfg.addProperty(DEFAULT_CONFIG.CLEARFORM.key, { 
				
				value : DEFAULT_CONFIG.CLEARFORM.value
			});
		}
//        onShowElements: function (event, args, obj) {
//        	var attr = args[1];
//        	for(var i in this._elements) {
//        		var element = this._elements[i];
//        		if(element.get("visible")==true)
//        			element.setStyle("display", "block");
//        		else
//        			element.setStyle("display", "none");
//        	}
//        },
	});
})();