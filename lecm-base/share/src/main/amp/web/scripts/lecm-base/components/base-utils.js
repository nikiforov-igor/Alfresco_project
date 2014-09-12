/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Base
 */
LogicECM.module.Base = LogicECM.module.Base || {};

LogicECM.module.Base.validators = LogicECM.module.Base.validators || {};

/**
 * LogicECM Base Util
 *
 * @namespace LogicECM
 */
LogicECM.module.Base.Util = {

    /**
     * Коэффициент, используемый для задания разного стартового tabindex в разных диалоговых окнах
     */
    tabNum: 0,

	/*
     * Загружает перечисленные скрипты на страницу
     * @param scripts - массив путей к скриптам
     * @param onSuccessCallback - обратный вызов
     * @param additionalRequires - дополнительные зависимости от YUI модулей
     */
    loadResources: function(scripts, cssFiles, onSuccessCallback, additionalRequires) {
        var i, script, cssFile;
        if (!scripts) {
            scripts = [];
        }
        if (!cssFiles) {
            cssFiles = [];
        }
        if (!additionalRequires) {
            additionalRequires = [];
        }
        for (i = 0; i < scripts.length; i++) {
            additionalRequires.push(scripts[i]);
        }
        for (i = 0; i < cssFiles.length; i++) {
            additionalRequires.push(cssFiles[i]);
        }
        var loader = new YAHOO.util.YUILoader({require: additionalRequires,
            base: Alfresco.constants.URL_RESCONTEXT + 'yui/',
            skin: {}});
        for (i = 0; i < scripts.length; i++) {
            script = scripts[i];
            loader.addModule({
                name: script,
                type: 'js',
                fullpath: Alfresco.constants.URL_RESCONTEXT + script,
                requires: (i > 0) ? [scripts[i-1]] : null
            });

        }
        for (i = 0; i < cssFiles.length; i++) {
            cssFile = cssFiles[i];
            loader.addModule({
                name: cssFile,
                type: 'css',
                fullpath: Alfresco.constants.URL_RESCONTEXT + cssFile
            });
        }
        loader.onSuccess = onSuccessCallback;
        loader.insert(null);
    },

    /*
     * Загружает перечисленные скрипты на страницу
     * @param scripts - массив путей к скриптам
     * @param onSuccessCallback - обратный вызов
     * @param additionalRequires - дополнительные зависимости от YUI модулей
     */
    loadScripts: function(scripts, onSuccessCallback, additionalRequires) {
        this.loadResources(scripts, [], onSuccessCallback, additionalRequires);
    },

	/*
     * Загружает перечисленные CSS на страницу
     * @param scripts - массив путей к скриптам
     * @param onSuccessCallback - обратный вызов
     */
    loadCSS: function(cssFiles, onSuccessCallback, additionalRequires) {
        this.loadResources([], cssFiles, onSuccessCallback, additionalRequires);
    },

	/*
	 * Set common block height
	 */
	setHeight: function() {
		var Dom = YAHOO.util.Dom,
				Bubbling = YAHOO.Bubbling;
		var bd = Dom.get('bd');
		var block = Dom.get('lecm-page');
		var wrapper = Dom.getElementsByClassName('sticky-wrapper', 'div');

		Bubbling.fire("HeightSetting");
		Dom.setStyle(block, 'height', 'auto');

		var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(block)
				- parseInt(Dom.getStyle(block, 'margin-bottom')) - parseInt(Dom.getStyle(bd, 'margin-bottom'));

		Dom.setStyle(block, 'min-height', h + 'px');
		Bubbling.fire("HeightSetted");
	},
	/*
	 * Set document page height
	 */
	setDocPageHeight: function() {
		var Dom = YAHOO.util.Dom;
		var doc = Dom.get('doc-bd');
		var wrapper = Dom.getElementsByClassName('sticky-wrapper', 'div');

		Dom.setStyle(doc, 'height', 'auto');

		var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(doc)
				- parseInt(Dom.getStyle(doc, 'margin-bottom'))
				- parseInt(Dom.getStyle(doc, 'border-top-width')) - parseInt(Dom.getStyle(doc, 'border-bottom-width'));

		Dom.setStyle(doc, 'min-height', h + 'px');
	},
	setDashletsHeight: function(dashletsBlockId) {
		var Dom = YAHOO.util.Dom;
		var page = Dom.get('lecm-page');
		var dashletsBlock = Dom.get(dashletsBlockId);

		if (!dashletsBlock) {
			return;
		}
		var pageHeight = parseInt(Dom.getStyle(page, 'height')) || parseInt(Dom.getStyle(page, 'min-height'));

		pageHeight = pageHeight - parseInt(Dom.getElementsByClassName('header-bar', 'div', page)[0].offsetHeight)
				- parseInt(Dom.get('lecm-content-ft').offsetHeight);
		Dom.setStyle(dashletsBlock, 'height', 'auto');
		Dom.setStyle(dashletsBlock, 'min-height', pageHeight + 'px');

		var dashlets = Dom.getElementsByClassName('dashlet', 'div', dashletsBlock);
		var dashletsCount = Math.round(dashlets.length / 2);
		var dashlet = dashlets[0];
		var dashletHeader = Dom.getElementsByClassName('dashlet-title', 'div', dashlet);
		var dashletMarginTop = parseInt(Dom.getStyle(dashlet, 'margin-top'));
		var dashletMarginBottom = parseInt(Dom.getStyle(dashlet, 'margin-bottom'));

		var h = (pageHeight - dashletMarginTop - dashletMarginBottom - (dashletsCount - 1) * Math.max(dashletMarginTop, dashletMarginBottom)) / dashletsCount;
		h = h - parseInt(parseInt(dashletHeader[0].offsetHeight));

		for (var i = 0; i < dashlets.length; i++) {
			dashlet = dashlets[i];
			Dom.setStyle(Dom.getElementsByClassName('dashlet-body', 'div', dashlet), 'height', Math.floor(h) + 'px');
		}
	},
	/**
	 * Get the URL parameter by key
	 * @param key - string : the key of parameter
	 */
	getUrlParam: function(key) {
		var query = document.location.search.split("+").join(" ");

		var params = {}, tokens,
				re = /[?&]?([^=]+)=([^&]*)/g;

		while (tokens = re.exec(query)) {
			params[decodeURIComponent(tokens[1])]
					= decodeURIComponent(tokens[2]);
		}

		return params[key];
	},
	/**
	 * Add a URL parameter (or changing it if it already exists)
	 * @param {search} string  this is typically document.location.search
	 * @param {key}    string  the key to set
	 * @param {val}    string  value
	 */
	addUrlParam: function(search, key, val) {
		var newParam = key + '=' + val,
				params = '?' + newParam;


		// If the "search" string exists, then build params from it
		if (search) {
			if (search.match(key + '[^&]*') != null) {
				// Try to replace an existance instance
				params = search.replace(new RegExp(key + '[^&]*'), newParam);
			} else {
				// If nothing was replaced, then add the new param to the end
				params = search + '&' + newParam;
			}
		}
		location.search = params;
	},
	/**
	 * Найти на странице компонент, имеющий определенный bubbling label.
	 * @param {string} p_sName название компонента. Например, "LogicECM.module.Base.DataGrid"
	 * @param {string} bubblingLabel bullbling label компонента
	 */
	findComponentByBubblingLabel: function(p_sName, bubblingLabel) {
		var components = [];
		var found = [];
		var bMatch, component;

		components = Alfresco.util.ComponentManager.list();

		for (var i = 0, j = components.length; i < j; i++) {
			component = components[i];
			bMatch = true;
			if (component['name'].search(p_sName) == -1) {
				bMatch = false;
			}
			if (bMatch) {
				found.push(component);
			}
		}
		if (bubblingLabel) {
			for (i = 0, j = found.length; i < j; i++) {
				component = found[i];
				if (typeof component == "object" && component.options.bubblingLabel) {
					if (component.options.bubblingLabel == bubblingLabel) {
						return component;
					}
				}
			}
		} else {
			return (typeof found[0] == "object" ? found[0] : null);
		}
		return null;
	},
	printReport: function(nodeRef, reportId) {
        window.open(window.location.protocol + "//" + window.location.host + Alfresco.constants.PROXY_URI_RELATIVE + "lecm/report/" + reportId + "?ID=" + encodeURI(nodeRef),
            "report", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
        Alfresco.util.PopupManager.displayMessage({
            text: Alfresco.component.Base.prototype.msg("message.report.success")
        });
	},
	/**
	 * Деструктор для форм
	 * @param {Object} event собитие, на которое мы срегировали (обычно, destroy)
	 * @param {Object} args дополнительные параметры, переданные при иницировании события. обычно, null
	 * @param {Object} params объект, имеющий следующие атрибуты:
	 *  {String} moduleId - ID модуля, по которому его можно найти в ComponentManager, обязательный
	 *  {Function} callback - функция, выполняющаяся перед основной работой деструктора, опциональный
	 *  {Object} callbackArg объект, передающийся параметром в callback, опциональный
	 */
	formDestructor: function(event, args, params) {
		var formRelatedElementsSelectorTemplate = "[id^='{moduleId}']";
		var moduleId = params.moduleId, callback = params.callback, callbackArg = params.callbackArg;

		var k, w;

		var isFn = YAHOO.lang.isFunction;

		var comMan = Alfresco.util.ComponentManager;
		var components = comMan.list();

		if (isFn(callback)) {
			callback.call(this, callbackArg);
		}

		var form = comMan.get(moduleId);
		var formIndex = components.indexOf(form); // IE9+
		var widgets = (form) ? form.widgets : [];

		for (k in widgets) {
			if (widgets.hasOwnProperty(k)) {
				w = widgets[k];

				if (w.hasOwnProperty('nodeName') && w.hasOwnProperty('tagName')) {
					$(w).remove();
					continue;
				}

				if (isFn(w.get)) {
					if (w.get('element') === null) {
						continue;
					}
				}

				if (isFn(w.destroy)) {
					w.destroy();
				}
			}
		}

		if (formIndex > -1) {
			while (components.length > formIndex) { // Не оптимизируй...
				if (components[formIndex].name != "Alfresco.HtmlUpload" &&
					components[formIndex].name != "LogicECM.DndUploader" &&
					components[formIndex].name != "Alfresco.FlashUpload" &&
					components[formIndex].name != "Alfresco.FileUpload") {
					LogicECM.module.Base.Util.removeAllBubbles(components[formIndex]);
//					if (components[formIndex].destroy != undefined) {
//						components[formIndex].destroy();
//					}
					comMan.unregister(components[formIndex]);
				} else {
					formIndex++;
				}
			}
		}

		// жесточайшим образом убить все элементы, ID которых начинается c moduleId
		$(YAHOO.lang.substitute(formRelatedElementsSelectorTemplate, {
			moduleId: moduleId
		})).remove();
	},

    componentsLength: -1,

    saveAdditionalObjects: function saveAdditionalObjects_function() {
        this.componentsLength = Alfresco.util.ComponentManager.list().length;
    },

    resetAdditionalObjects: function resetAdditionalObjects_function() {
        if (this.componentsLength == -1) return;
        var manager = Alfresco.util.ComponentManager;
        var components = manager.list();
        var delta = components.slice(this.componentsLength);

        for (var index in delta) {
            var component = delta[index];
            if (component.name.indexOf("LogicECM") == 0) {
                LogicECM.module.Base.Util.removeAllBubbles(component);
                Alfresco.util.ComponentManager.unregister(component);
            }
        }
        this.componentsLength = -1;
    },

    removeDomObjects: function(objectsIds) {
        if (objectsIds && !objectsIds.isEmpty) {
            var array = [].concat(objectsIds);
            for (var i in array) {
                var els = YAHOO.util.Selector.query("#" + array[i]);
                // Используем Selector.query(), а не Dom.get(),
                // потому что может быть несколько элементов с одним id,
                // и .get() возьмет только первый из них, а нам нужны все
                for (var j in els) {
                    var el = els[j];
                    el.parentNode.removeChild(el);
                }
            }
        }
    },

	removeAllBubbles: function (obj) {
		var event;
		var bubble = YAHOO.Bubbling.bubble;

		for (event in bubble) {
			if (bubble.hasOwnProperty(event)) {
				bubble[event].subscribers.forEach(function(s) {
					if (s.obj === obj) {
						YAHOO.Bubbling.unsubscribe(event, s.fn, s.obj);
					}
				});
			}
		}
	},

	destroyForm: function(formId) {
		var isFn = YAHOO.lang.isFunction;
		var comMan = Alfresco.util.ComponentManager;
		var components = comMan.list();

		for (var i = components.length - 1; i >= 0; i--) {
			var component = components[i];
			if (component.id != null &&
				component.id.indexOf(formId) == 0 &&
				component.name != "Alfresco.HtmlUpload" &&
				component.name != "LogicECM.DndUploader" &&
				component.name != "Alfresco.FlashUpload" &&
				component.name != "Alfresco.FileUpload") {
					this.removeAllBubbles(component);
					if (isFn(component.destroy)) {
						component.destroy();
					}
					comMan.unregister(component);
			}
		}
	},


    getCookie: function (name) {
        var results = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');

        if (results)
            return ( decodeURIComponent(results[2]) );
        else
            return null;
    },

    setCookie: function (name, value, options){
        var lang = YAHOO.lang;
        options = options || {};

        if (!lang.isString(name)) {
            throw new TypeError("Cookie.set(): Cookie name must be a string.");
        }

        if (lang.isUndefined(value)) {
            throw new TypeError("Cookie.set(): Value cannot be undefined.");
        }

        var text = this._createCookieString(name, value, options);
        document.cookie = text;
        return text;
    },

    _createCookieString: function (name, value, options) {
        //shortcut
        var lang = YAHOO.lang,
            text = encodeURIComponent(name) + "=" + encodeURIComponent(value);

        if (lang.isObject(options)) {
            //expiration date
            if (options.expires instanceof Date) {
                text += "; expires=" + options.expires.toUTCString();
            }

            //path
            if (lang.isString(options.path) && options.path !== "") {
                text += "; path=" + options.path;
            }

            //domain
            if (lang.isString(options.domain) && options.domain !== "") {
                text += "; domain=" + options.domain;
            }

            //secure
            if (options.secure === true) {
                text += "; secure";
            }
        }

        return text;
    },

    // функции для контролов:
    getControlValueView: function(nodeRef, displayValue, showTitle) {
        var title = "";
        if (showTitle == null || showTitle) {
            title = "title='" + Alfresco.component.Base.prototype.msg("title.click.for.extend.info") + "'";
        }
        return "<span><a href='javascript:void(0);' " + title + " onclick=\"viewAttributes(\'" + nodeRef + "\', null, \'logicecm.view\')\">" + displayValue + "</a></span>";
    },

    getControlEmployeeView: function(employeeNodeRef, displayValue, showTitle) {
	    var title = "";
	    if (showTitle == null || showTitle) {
		    title = "title='" + Alfresco.component.Base.prototype.msg("title.click.for.extend.info") + "'";
	    }
	    return "<span class='person'><a href='javascript:void(0);' " + title + " onclick=\"viewAttributes(\'" + employeeNodeRef + "\', null, \'logicecm.employee.view\')\">" + displayValue + "</a></span>";
    },
    getControlDefaultView: function (displayValue) {
        return "<span class='not-person' title='" + displayValue + "'>" + displayValue + "</span>";
    },
    getControlItemRemoveButtonHTML: function (id) {
        return '<a href="javascript:void(0);" class="remove-item" id="' + id + '"></a>';
    },
    getCroppedItem: function(leftCroppedPart, rightPart) {
        var buttonsHtml = "";

        if (rightPart) {
            buttonsHtml = '<div class="ci-buttons-div">' + rightPart + '</div>';
        }

        return '<div class="cropped-item">' + buttonsHtml + '<div class="ci-value-div"><span>' + leftCroppedPart + '</span></div></div>';
    },
	encodeUrlParams: function(params) {
		return "p1=" + base64.encode(params) + "&p2=" + this.hashCode(params);
	},
	hashCode: function(str) {
		var hash = 0, i, chr, len;
		if (str.length == 0) return hash;
		for (i = 0, len = str.length; i < len; i++) {
			chr   = str.charCodeAt(i);
			hash  = ((hash << 5) - hash) + chr;
			hash |= 0; // Convert to 32bit integer
		}
		return hash;
	},

    // Скрипт выставляет элементам формы атрибут tabindex, задавая нужный порядок обхода с клавиатуры
    setElementsTabbingOrder: function (elements, inDialog, setFirstFocus) {
        var Dom = YAHOO.util.Dom,
            Selector = YAHOO.util.Selector,
            KeyListener = YAHOO.util.KeyListener;

        var tabbedArray = [];
        var tabindex = 0;
        // если обрабатывается форма в диалоговом окне:
        if (inDialog) {
            tabindex = (++LogicECM.module.Base.Util.tabNum) * 100; // подразумеваем, что на основной странице tabindex, равный этому значению, не был достигнут
        }
        for (var i = 0; i < elements.length; i++) {
            var el = elements[i];
            if (el && !Selector.test(el, ".control *")) {
                if (Dom.hasClass(el, "control")) {
                    //todo настройки для конкретных контролов
                    if (Dom.hasClass(el, "dnd-uploader")) {
                        var link = Selector.query('.uploader-block img.uploader-button', el, true);
                        if (link) {
                            Dom.setAttribute(link, 'tabindex', ++tabindex);
                            tabbedArray.push(link);
                        }
                        var list = Selector.query('.container .attachments-list', el, true);
                        if (list) {
                            Dom.setAttribute(list, 'tabindex', ++tabindex);
                            tabbedArray.push(list);
                        }
                        var previewBtn = Selector.query('.container .show-preview-button button, .container .show-preview-button input[type=button]', el, true);
                        if (previewBtn) {
                            Dom.setAttribute(previewBtn, 'tabindex', ++tabindex);
                            tabbedArray.push(previewBtn);
                        }
                    } else if (Dom.hasClass(el, "")) {

                    } else {
                        // универсально для большинства контролов
                        var valueDiv = Selector.query('div.value-div', el, true);
                        if (valueDiv) {
                            var input = Selector.query('input[type=text], input[type=checkbox], select, textarea', valueDiv, true);
                            if (input) {
                                Dom.setAttribute(input, 'tabindex', ++tabindex);
                                tabbedArray.push(input);
                            }
                        }
                        var buttonDiv = Selector.query('div.buttons-div', el, true);
                        if (buttonDiv) {
                            var buttons = Selector.query('a, input[type=button], span.yui-button button', buttonDiv);
                            for (var j = 0; j < buttons.length; j++) {
                                var btn = buttons[j];
                                if (btn.offsetHeight > 0) {
                                    Dom.setAttribute(btn, 'tabindex', ++tabindex);
                                    tabbedArray.push(btn);
                                }
                            }
                        }
                    }
                } else if (Dom.hasClass(el, "yui-button")) {
                    var button = Selector.query('button', el, true);
                    Dom.setAttribute(button, 'tabindex', ++tabindex);
                    tabbedArray.push(button);
                } else {
                    Dom.setAttribute(el, 'tabindex', ++tabindex);
                    tabbedArray.push(el);
                }
            }
        }

        if (!tabbedArray.isEmpty) {
            var firstEl = tabbedArray[0];
            var lastEl = tabbedArray[tabbedArray.length - 1];

            new KeyListener(lastEl, {keys: KeyListener.KEY.TAB},
                {
                    fn: function (layer, args) {
                        var e = args[1];
                        firstEl.focus();
                        e.preventDefault();
                    },
                    scope: this,
                    correctScope: true
                }, KeyListener.KEYDOWN).enable();

            if (setFirstFocus && firstEl) {
                firstEl.focus();
            }
        }
    },

	getComponentReadyElementId: function(formId, fieldId) {
		return formId + "_" + fieldId + "_" + "componentReady";
	},
	createComponentReadyElementId: function(fieldHtmlId, formId, fieldId) {
		var elementId = this.getComponentReadyElementId(formId, fieldId);
		if (YAHOO.util.Dom.get(elementId) == null) {
			var parent = YAHOO.util.Dom.get(fieldHtmlId);
			if (parent != null) {
				parent = parent.parentNode;
				if (parent != null) {
					var element = document.createElement('div');
					element.id = elementId;
					parent.appendChild(element);
				}
			}
		}
	},
	disableControl: function(formId, fieldId) {
		YAHOO.util.Event.onAvailable(this.getComponentReadyElementId(formId, fieldId), function() {
			YAHOO.Bubbling.fire("disableControl", {
				formId: formId,
				fieldId: fieldId
			});
		});
	},
	enableControl: function(formId, fieldId) {
		YAHOO.util.Event.onAvailable(this.getComponentReadyElementId(formId, fieldId), function() {
			YAHOO.Bubbling.fire("enableControl", {
				formId: formId,
				fieldId: fieldId
			});
		});
	},
	reInitializeControl: function(formId, fieldId, options) {
		YAHOO.util.Event.onAvailable(this.getComponentReadyElementId(formId, fieldId), function() {
			YAHOO.Bubbling.fire("reInitializeControl", {
				formId: formId,
				fieldId: fieldId,
				options: options
			});
		});
	}
};

(function() {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector;

	// Recalculate the vertical size on a browser window resize event
	Event.on(window, "resize", function(e) {
		LogicECM.module.Base.Util.setHeight();
	}, this, true);

	Event.onAvailable('bd', function() {
		LogicECM.module.Base.Util.setHeight();
	});

	/**
	 * Base resizer
	 */
	LogicECM.module.Base.Resizer = function(name) {
		LogicECM.module.Base.Resizer.superclass.constructor.call(this, name);
		return this;
	};

	YAHOO.extend(LogicECM.module.Base.Resizer, Alfresco.widget.Resizer, {
        marginLeft: 8,
        onStartResize: null,
        onEndResize: null,
        onComponentsLoaded: function Resizer_onComponentsLoaded() {
            YAHOO.util.Event.onDOMReady(this.onReady, this, true);

            // Start and End resize event handlers
            this.widgets.horizResize.on("startResize", function(eventTarget) {
                if (this.onStartResize) {
                    this.onStartResize();
                }
            }, this, true);
            this.widgets.horizResize.on("endResize", function(eventTarget) {
                if (this.onEndResize) {
                    this.onEndResize();
                }
            }, this, true);
        },
		onResize: function(width) {
			var cn = Dom.get(this.options.divLeft).childNodes,
					handle = cn[cn.length - 1];

			Dom.setStyle(this.options.divLeft, "height", "auto");
			Dom.setStyle(handle, "height", "");

			var h = Dom.getY("lecm-content-ft") - Dom.getY("lecm-content-main");

			if (h < this.MIN_FILTER_PANEL_HEIGHT) {
				h = this.MIN_FILTER_PANEL_HEIGHT;
			}

			Dom.setStyle(handle, "height", h + "px");

			if (width !== undefined) {
				// 8px breathing space for resize gripper
				Dom.setStyle(this.options.divRight, "margin-left", this.marginLeft + width + "px");
			}
			YAHOO.Bubbling.fire("SetHeaderWidth");
		}
	});

    // Скрипт выставляет элементам формы атрибут tabindex, задавая нужный порядок обхода с клавиатуры
    // Отрабатывает после загрузки формы
    function setFormElementsTabbingOrder(layer, args) {
        var form = Dom.get(args[1].eventGroup);
        var elements = Selector.query('div.control, .form-buttons span.yui-button, .form-buttons input[type=button]', form);

        LogicECM.module.Base.Util.setElementsTabbingOrder(elements, Selector.test(form, ".yui-dialog form"), true);
    }

    YAHOO.Bubbling.on("afterFormRuntimeInit", setFormElementsTabbingOrder);

})();