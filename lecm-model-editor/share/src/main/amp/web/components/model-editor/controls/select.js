if (typeof IT == "undefined" || !IT) {
	var IT = {};
}

IT.widget = IT.widget || {};

(function() {
	var Dom = YAHOO.util.Dom, Lang = YAHOO.util.Lang, Bubbling = YAHOO.Bubbling;

	IT.widget.Select = function IT_widget_Select(p_oConfig) {
		IT.widget.Select.superclass.constructor.call(this, p_oConfig);
		
		if (Lang.isFunction(p_oConfig.changeEvent)) {
			this.on("change", p_oConfig.changeEvent, p_oConfig.showElements );
		} else {
			this.on("change", this.changeEvent );
		}
		
		//this.createEvent('updateEvent');
	};

	YAHOO.extend(IT.widget.Select,  IT.widget.Input, 
	{
		refresh: function(event, args) {
			var inputEl = this.get('inputEl');
			this._renderInputEl(inputEl);
		},
		_renderInputEl: function (containerEl) {
			containerEl.innerHTML = "";
		    var select = containerEl.appendChild(document.createElement('select'));
		    select.name = this.get('name');
		    select.disabled = this.get('disabled');
		    select.id = this.get('id') || select.name;
		    
		    var _options = this.get('options');
		    
		    if(this.get('showdefault')) {
		    	var option = document.createElement("option");
			    select.appendChild(option);
		    }
		    
		    for(var i in _options) {
		    	var option = document.createElement("option");
		    	
		    	var opt = _options[i];
				if(Lang.isObject(opt)) {
					option.innerHTML = (opt.label||opt.value);
					option.value = opt.value;
				}
				if(Lang.isString(opt)) {
					option.innerHTML = opt;
					option.value = opt;
				}
				option = select.appendChild(option);
				if(this.get('value')==option.value) {
					option.selected=true;
				}
		    }
		    
		},
		initAttributes: function (p_oAttributes) {
	        
            var oAttributes = p_oAttributes || {};
        
            IT.widget.Select.superclass.initAttributes.call(this, 
                oAttributes);
            
            this.setAttributeConfig("options", {
                value: (oAttributes.options || [])
                //writeOnce: true,
            });
            this.setAttributeConfig("changeEvent", {
                value: (oAttributes.changeEvent || null),
                writeOnce: true
            });
            this.setAttributeConfig("showElements", {
                value: (oAttributes.showElements || []),
                writeOnce: true
            });
            this.setAttributeConfig("showdefault", {
            	validator: Lang.isBoolean,
                value: (oAttributes.showdefault || true),
                writeOnce: true
            });
            this.setAttributeConfig("disabled", {
            	validator: Lang.isBoolean,
                value: (oAttributes.disabled || false),
                writeOnce: true
            });
		},
		_setOptions: function (p_aOptions) {
			
		}
	});
})();