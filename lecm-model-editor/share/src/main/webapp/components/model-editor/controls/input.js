if (typeof IT == "undefined" || !IT) {
	var IT = {};
}

IT.widget = IT.widget || {};

(function() {
	var Dom = YAHOO.util.Dom, Lang = YAHOO.util.Lang, Bubbling = YAHOO.Bubbling;

	var CSS_PREFIX = 'form-field ';
	
	IT.widget.Input = function IT_widget_Input(p_oConfig) {
		IT.widget.Input.superclass.constructor.call(this, document.createElement('div'), p_oConfig);
		
		Bubbling.on("visibleUpdated", this.onShowElements, this);
		Bubbling.on("refreshElement", this.refresh, this);
		
		if (Lang.isFunction(p_oConfig.changeEvent)) {
			this.on("keyup", p_oConfig.changeEvent );
		} else {
			this.on("keyup", this.changeEvent );
		}
		//this.createEvent('updateEvent');
	};

	YAHOO.extend(IT.widget.Input,  YAHOO.util.Element, 
	{
		changeEvent: function(event) {
			Bubbling.fire("inputChangeEvent", {el:this, target: event.target, evt:event});
		},
		refresh: function(event, args) {
			
		},
		_renderInputEl: function (containerEl) {
			var input = containerEl.appendChild(document.createElement('input'));
		    input.name = this.get('name');
		    input.id = this.get('id') || input.name;
		    input.type = 'text';
		    input.value = this.get('value');
		    
		    //{label:(val[i].label||val[i].name),name:val[i].name}
		},
        initAttributes: function (oConfigs) {
        	IT.widget.Input.superclass.initAttributes.call(this, oConfigs);
        	
        	var container = this.get('element');
        	container.innerHTML = "";
        	
            this.setAttributeConfig('labelEl', {
                readOnly: true,
                value: container.appendChild(document.createElement('label'))
            });
            
            this.setAttributeConfig('inputEl', {
                readOnly: true,
                value: container.appendChild(document.createElement('span'))
            });
			this.setAttributeConfig('helpEl', {
                readOnly: true,
                value: container.appendChild(document.createElement('span'))
            });
            
            this.setAttributeConfig('name', {
                writeOnce: true,
                validator: Lang.isString
            });
                    
            this.setAttributeConfig('id', {
                writeOnce: true,
                validator: function (value) {
                    return /^[a-zA-Z][\w0-9\-_.:]*$/.test(value);
                },
                value: Dom.generateId(),
                method: function (value) {
                    this.get('inputEl').id = value;
                }
            });
            
            this.setAttributeConfig('label', {
                //validator: Lang.isString,
                method: function (value) {
                    this.get('labelEl').innerHTML = (value||this.get("name"));
                },
                value: ''
            });
			
			this.setAttributeConfig('help', {
                //validator: Lang.isString,
                method: function (value) {
                    this.get('helpEl').innerHTML = '<img src="../res/components/form/images/help.png" title="'+value+'" tabindex="0"/>';
                },
                value: ''
            });
            
            this.setAttributeConfig('value', {
                value: oConfigs.value
            });
            
            this.setAttributeConfig('visible', {
                value: (oConfigs.visible||true)
            });
        },
        render: function (parentEl) { 
        	var parentEl = Dom.get(parentEl);
        	if (!parentEl) {
                YAHOO.log('Missing mandatory argument:  parentEl','error','Field');
                return null;
            }
        	
        	var containerEl = this.get('element');
            this.addClass(CSS_PREFIX + 'container');
            
            var inputEl = this.get('inputEl');
            //Dom.addClass(inputEl,CSS_PREFIX + 'input');
            
            var labelEl = this.get('labelEl');
            labelEl.setAttribute('for', this.get('id'));
            //Dom.addClass(labelEl,CSS_PREFIX + 'label');
            
            this._renderInputEl(inputEl);
            
            parentEl.appendChild(containerEl);
        },
        destroy: function () {
        	var el = this.get('element');
            Event.purgeElement(el, true);
            el.parentNode.removeChild(el);
        },
        //getValue: function () { /* ... */ },
        //setValue: function (newValue) { /* ... */ },
        onShowElements: function (event, args, obj) {
        	var attr = args[1];
        	
        	if(this.get("visible")==true)
    			this.setStyle("display", "block");
    		else
    			this.setStyle("display", "none");
        	
        	if(Lang.isArray(attr)) {
        		
        		for(var i in attr) {
        			if(this.get('name')===attr[i]) {
        				this.setStyle("display", "block");
        			}
        		}
        	}
        }
	});
})();