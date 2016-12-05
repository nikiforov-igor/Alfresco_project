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
	 *  {Boolean} force - флаг принудительного удаления, опциональный
	 *                    Если true, то форма будет удалена вместе с другими компонентами, которые были созданы позже ее.
	 *                    Если false, то будет отменена регистрация компонентов в ComponentsManager
	 */
	formDestructor: function(event, args, params) {
		var formRelatedElementsSelectorTemplate = "[id^='{moduleId}']";
		var moduleId = params.moduleId, callback = params.callback, callbackArg = params.callbackArg, force = params.force;

		var k, w;

		var isFn = YAHOO.lang.isFunction;

		var comMan = Alfresco.util.ComponentManager;
		var component, components = comMan.list();

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
				component = components[formIndex];
				if (component.name != "Alfresco.HtmlUpload" &&
					component.name != "LogicECM.DndUploader" &&
					component.name != "Alfresco.FlashUpload" &&
					component.name != "Alfresco.FileUpload") {
					LogicECM.module.Base.Util.removeAllBubbles(component);
					if (force && isFn(component.destroy)) {
						component.destroy();
					}
					comMan.unregister(component);
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

	removeAllBubbles: function (obj) {
		var event;
		var bubble = YAHOO.Bubbling.bubble;

		for (event in bubble) {
			if (bubble.hasOwnProperty(event)) {
				// Лучше бежать по копии, т.к unsubscribe выпиливает элемент из
				// bubble[event].subscribers, что может привести к некорректной работе
				// в случае наличия нескольких подписчиков с одного модуля
				var subscribers = Alfresco.util.deepCopy(bubble[event].subscribers);
				subscribers.forEach(function(s) {
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

		function onControlValueViewAvailable(params) {
			YAHOO.util.Event.on(this.id, 'click', LogicECM.module.Base.Util.viewAttributes.bind(LogicECM.module.Base.Util, params));
		}

        var title = "",
			id = YAHOO.util.Dom.generateId();

        if (showTitle == null || showTitle) {
            title = "title='" + Alfresco.component.Base.prototype.msg("title.click.for.extend.info") + "'";
        }
		YAHOO.util.Event.onAvailable(id, onControlValueViewAvailable, {
			itemId: nodeRef,
			title: 'logicecm.view'
		});
		return YAHOO.lang.substitute("<span><a href='javascript:void(0);' id='{id}' {title}>{displayValue}</a></span>", {
			id: id,
			title: title,
			displayValue: displayValue
		});
    },

    getControlEmployeeView: function(employeeNodeRef, displayValue, showTitle) {
	    return this.getControlMarkeredEmployeeView(employeeNodeRef, displayValue, showTitle, null, null);
    },

    getControlMarkeredEmployeeView: function(employeeNodeRef, displayValue, showLinkTitle, personClass, personTitle) {

		function onControlEmployeeViewAvailable(params) {
			YAHOO.util.Event.on(this.id, 'click', LogicECM.module.Base.Util.viewAttributes.bind(LogicECM.module.Base.Util, params));
		}

		var linkTitle = "",
			id = YAHOO.util.Dom.generateId();

        if (showLinkTitle == null || showLinkTitle) {
            linkTitle = "title='" + Alfresco.component.Base.prototype.msg("title.click.for.extend.info") + "'";
        }
		YAHOO.util.Event.onAvailable(id, onControlEmployeeViewAvailable, {
			itemId: employeeNodeRef,
			title: 'logicecm.employee.view'
		});
		return YAHOO.lang.substitute("<span class='{personClass}' {personTitle}><a href='javascript:void(0);' id='{id}' {linkTitle}>{displayValue}</a></span>", {
			personClass: personClass ? ("person " + personClass) : "person",
			personTitle: personTitle ? ("title='" + personTitle + "'") : "",
			id: id,
			linkTitle: linkTitle,
			displayValue: displayValue
		});
    },

    getControlDefaultView: function (displayValue) {
        return "<span class='not-person' title='" + displayValue + "'>" + displayValue + "</span>";
    },
    getControlItemRemoveButtonHTML: function (id) {
        return '<a href="javascript:void(0);" class="remove-item" id="' + id + '"></a>';
    },
    getControlItemUpButtonHTML: function (id) {
        return '<a href="javascript:void(0);" class="up-item" id="' + id + '"></a>';
    },
    getControlItemDownButtonHTML: function (id) {
        return '<a href="javascript:void(0);" class="down-item" id="' + id + '"><span></span></a>';
    },

 	getCroppedItemWithTwoButtons: function(leftCroppedPart, firstButton, secondButton) {
        var firstButtonsHtml = "";
        var secondButtonsHtml = "";

        if (firstButton) {
            firstButtonsHtml = '<div class="ci-buttons-div">' + firstButton + '</div>';
        }
        if (secondButton) {
            secondButtonsHtml = '<div class="ci-buttons-div">' + secondButton + '</div>';
        }

        return '<div class="cropped-item">' + firstButtonsHtml + secondButtonsHtml + '<div class="ci-value-div"><span>' + leftCroppedPart + '</span></div></div>';
    },


    getCroppedItem: function(leftCroppedPart, rightPart) {
        var buttonsHtml = "";

        if (rightPart) {
            buttonsHtml = '<div class="ci-buttons-div">' + rightPart + '</div>';
        }

        return '<div class="cropped-item">' + buttonsHtml + '<div class="ci-value-div"><span>' + leftCroppedPart + '</span></div></div>';
    },
	encodeUrlParams: function(params) {
		return "p1=" + encodeURIComponent(B64.encode(params)) + "&p2=" + encodeURIComponent(this.hashCode(params));
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
                        var container = Selector.query(' > div.container', el, true);
                        if (container) {
                            var valueDiv = Selector.query(' > div.value-div', container, true);
                            if (valueDiv) {
                                var input = Selector.query('input[type=text], input[type=checkbox], select, textarea', valueDiv, true);
                                if (input) {
                                    Dom.setAttribute(input, 'tabindex', ++tabindex);
                                    tabbedArray.push(input);
                                }
                            }
                            var buttonDiv = Selector.query(' > div.buttons-div', container, true);
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
	readonlyControl: function (formId, fieldId, readonly) {
		YAHOO.util.Event.onAvailable(this.getComponentReadyElementId(formId, fieldId), function (params) {
			YAHOO.Bubbling.fire("readonlyControl", params);
		},{
				formId: formId,
				fieldId: fieldId,
				readonly: readonly
		});
	},
	hideControl: function(formId, fieldId) {
		YAHOO.util.Event.onAvailable(this.getComponentReadyElementId(formId, fieldId), function() {
			YAHOO.Bubbling.fire("hideControl", {
				formId: formId,
				fieldId: fieldId
			});
		});
	},
	showControl: function(formId, fieldId) {
		YAHOO.util.Event.onAvailable(this.getComponentReadyElementId(formId, fieldId), function() {
			YAHOO.Bubbling.fire("showControl", {
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
	},

    loadXMLDoc: function loadXMLDoc_function(url) {
        if (window.ActiveXObject)
        {
            xhttp = new ActiveXObject("Msxml2.XMLHTTP");
        }
        else
        {
            xhttp = new XMLHttpRequest();
        }
        xhttp.open("GET", url, false);
        try {xhttp.responseType = "msxml-document"} catch(err) {} // Helping IE11
        xhttp.send("");
        return xhttp.responseXML;
    },

    getTransformHTML: function getTransformHTML(parentElement, xmlNode, xslNode) {
        var xmlURL = Alfresco.constants.PROXY_URI + "api/node/content/" + this.covertNodeRef(xmlNode);
        var xml = this.loadXMLDoc(xmlURL);
        var xslURL = Alfresco.constants.PROXY_URI + "api/node/content/" + this.covertNodeRef(xslNode);
        var xsl = this.loadXMLDoc(xslURL);
        // code for IE
        if (window.ActiveXObject || xhttp.responseType == "msxml-document")
        {
            parentElement.innerHTML = xml.transformNode(xsl);
        }
        // code for Chrome, Firefox, Opera, etc.
        else if (document.implementation && document.implementation.createDocument)
        {
            xsltProcessor = new XSLTProcessor();
            xsltProcessor.importStylesheet(xsl);
            resultDocument = xsltProcessor.transformToFragment(xml, document);
            parentElement.appendChild(resultDocument);
        }
    },

    covertNodeRef: function convertNodeRef_function(nodeRef) {
        return nodeRef.replace("://", "/");
    },

    lastDialog: null,

    registerDialog: function registerDialog_function(dialog) {
        this.lastDialog = dialog;
    },

    getLastDialog: function getLastDialog_function() {
        return this.lastDialog;
    },

    // Для таблиц с фиксированным заголовком
    // задаем ширину ячеек в хедере (по ширине соответствующих столбцов)
    setFixedHeaderWidths: function(table) {
        if (table) {
            var Dom = YAHOO.util.Dom,
                Selector = YAHOO.util.Selector;

            var firstTr = Selector.query("tbody.yui-dt-data > tr", table, true);
            Dom.removeClass(table, "fixedHeader");

            if (firstTr) {
                var tds = Selector.query(" > td", firstTr);
                var ths = Selector.query("thead > tr > th", table);

                for (var i = 0; i < tds.length; i++) {
                    var td = tds[i];
                    var th = ths[i];
                    var width = parseInt(Dom.getStyle(td, "width")) + "px";

                    Dom.setStyle(th, "width", width);
                    Dom.setStyle(td, "width", width);
                }
                Dom.addClass(table, "fixedHeader");
            }
        }
    },

	setPostLocation: function setPostLocation_function(url) {
		var splitUrl = url.split("?");
		var path = splitUrl[0];
		var vars = splitUrl[1];

		var form = document.createElement("form");
		form.setAttribute("method", "POST");

		if (vars != undefined) {
			var params = [];
			var splitParams = vars.split('&');
			for (var key in splitParams) {
				var value = splitParams[key].split('=');
				params[value[0]] = value[1];
			}
			for (var key in params) {
				if (key == "nodeRef") { //todo Костыль - nodeRef всегда передаём в URL
					path += "?nodeRef=" +params[key];
				} else {
					var hiddenField = document.createElement("input");
					hiddenField.setAttribute("type", "hidden");
					hiddenField.setAttribute("name", key);
					hiddenField.setAttribute("value", decodeURIComponent(params[key]));

					form.appendChild(hiddenField);
				}
			}
		}

		form.setAttribute("action", path);

		document.body.appendChild(form);
		form.submit();
	},

	dateToUTC0: function(date) {
		var dateStr = Alfresco.util.toISO8601(date).substring(0,10) + "T00:00:00.000+00:00";
		return Alfresco.util.fromISO8601(dateStr);
	},

	showAttachmentsModalForm: function(documentRef, attachmentRef) {

		var self = this;
		var attachmentsModalForm = new Alfresco.module.SimpleDialog("modalWindow");

		attachmentsModalForm.setOptions({
			width: '50em',
			templateUrl: Alfresco.constants.URL_SERVICECONTEXT + '/lecm/components/document/attachments-preview',
			templateRequestParams: {
				nodeRef : documentRef,
				forTask : false,
				selectedAttachmentNodeRef : attachmentRef
			},
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: function (form, simpleDialog) {
					var formNode = YAHOO.util.Dom.get(form.formId);
					var nameInput = YAHOO.util.Dom.getElementsBy(function (a) {
						return a.name.indexOf('cm_title') >= 0;
					}, 'input', formNode)[0];

					simpleDialog.dialog.setHeader("Вложения");
					this.createDialogOpening = false;
					simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
						self.destroyForm(simpleDialog.id);
						self.formDestructor(event, args, params);
					}, {moduleId: simpleDialog.id}, this);
				},
				scope: this
			}
		});

		attachmentsModalForm.show();
	},

	uuid: function () {
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
			var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
			return v.toString(16);
		});
	},

	viewAttributes: function (obj) {

		var viewDialog = LogicECM.module.Base.Util.createDialog("view-node-panel_" + Alfresco.util.generateDomId());
		var requestObj = {
			itemKind: obj.itemKind ? obj.itemKind : "node",
			itemId: obj.itemId,
			mode: "view",
			htmlid: obj.htmlid ? obj.htmlid : obj.itemId.replace("workspace://SpacesStore/", "").replace("-", ""),
		};
		if(obj.formId){
			requestObj.formId = obj.formId;
		}
		if (obj.nodeId) {
			requestObj.nodeId = obj.nodeId;
		}
		if (obj.setId) {
			requestObj.setId = obj.setId;
		}

		Alfresco.util.Ajax.request({
				scope: this,
				url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
				dataObj: requestObj,
				successCallback: {
					scope: this,
					fn: function (response) {
						if (viewDialog) {
							var message = obj.title ?
								(Alfresco.messages.global[obj.title] ? Alfresco.messages.global[obj.title] : obj.title) :
								Alfresco.util.message("logicecm.view");
							viewDialog.setHeader(message);
							viewDialog.setBody(response.serverResponse.responseText);
							viewDialog.show();
						}
					}
				},
				failureMessage: obj.failureMessage ? Alfresco.messages.global[obj.failureMessage] : Alfresco.util.message("message.failure"),
				execScripts: true
			});
	},

	createDialog: function (formid) {
		var viewDialog = Alfresco.util.createYUIPanel(formid, {
				width: "60em",
				destroyOnHide: true,
				buttons: [
					{
						text  : Alfresco.util.message("button.close"),
						handler : function (e) {
							viewDialog.hide();
						}
					}

				]
			});
		viewDialog.hideEvent.subscribe(function (event, args, params) {
			LogicECM.module.Base.Util.formDestructor(event, args, params);
		}, {moduleId: viewDialog.id, force: true}, this);
		return viewDialog;
	},

	showEmployeeViewByLink: function (employeeLinkNodeRef, title) {
		Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getEmployeeByLink?nodeRef=" + employeeLinkNodeRef,
				successCallback:
				{
					fn: function (oResponse) {
						if (oResponse.json.nodeRef) {
							LogicECM.module.Base.Util.viewAttributes({
								itemId: oResponse.json.nodeRef,
								title: title
							});
						} else {
							Alfresco.util.PopupManager.displayMessage(
								{
									text: Alfresco.util.message("message.details.failure")
								});
						}
					},
					scope: this
				},
				failureCallback:
				{
					fn: function (oResponse) {
						Alfresco.util.PopupManager.displayMessage(
							{
								text: Alfresco.util.message("message.details.failure")
							});
					},
					scope: this
				}
			});
	},

	displayErrorMessageWithDetails: function (msgHeader, msgTitle, msgDetails) {
		var errorMessageDialog = new YAHOO.widget.SimpleDialog(Alfresco.util.generateDomId()+"-errorMessageWithDetailsDialog", {
			width: "60em",
			fixedcenter: true,
			destroyOnHide: true,
			modal: true
		});

		errorMessageDialog.setHeader(msgHeader);

		var customMsg = msgDetails.match("\\[\\[.+\\]\\]");
		if (customMsg != null) {
			msgDetails = customMsg[0].replace("[[", "").replace("]]", "");
		}
		var errorDialogBodyTemplate = '<div class="grid-create-error-dialog"><h3>{title}</h3>' +
			'<a href="javascript:void(0);" id="{ID}-error-message-show-details-link">{text}</a></div>' +
			'<div id="{ID}-error-message-show-details" class="error-dialog-details">{details}</div>';
		var errorDialogBody = YAHOO.lang.substitute(errorDialogBodyTemplate, {
			title: msgTitle,
			ID: errorMessageDialog.id,
			text: Alfresco.util.message("logicecm.base.error.show.details"),
			details: msgDetails
		});
		errorMessageDialog.setBody(errorDialogBody);
		errorMessageDialog.render(document.body);
		errorMessageDialog.show();

		YAHOO.util.Event.on(errorMessageDialog.id + "-error-message-show-details-link", "click", function(){
			Dom.setStyle(errorMessageDialog.id + "-error-message-show-details", "display", "block");
		}, null, this);

		errorMessageDialog.hideEvent.subscribe(function (event, args, params) {
			LogicECM.module.Base.Util.formDestructor(event, args, params);
		}, {moduleId: errorMessageDialog.id, force: true}, this);
	}

};

LogicECM.module.Base.SimplePromise = function () {
	this._callbacks = [];
	this._isDone = false;
	return this;
};

LogicECM.module.Base.SimplePromise.prototype = {

	_callbacks: null,

	_isDone: null,

	isDone: function () {
		return this._isDone;
	},

	then: function (func, context) {
		var p;
		if (this._isDone) {
			p = func.apply(context, this.result);
		} else {
			p = new LogicECM.module.Base.SimplePromise();
			this._callbacks.push(function () {
				var res = func.apply(context, arguments);
				if (res && typeof res.then === 'function') {
					res.then(p.done, p);
				}
			});
		}
		return p;
	},

	done: function () {
		this.result = arguments;
		this._isDone = true;
		for (var i = 0; i < this._callbacks.length; i++) {
			this._callbacks[i].apply(null, arguments);
		}
		this._callbacks = [];
	}
};

(function() {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector;

	//костыль для инициализации форм, пока живет здесь
	Alfresco.util.Ajax.jsonGet({
		url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/config/init",
		dataObj: { reset: false }
	});

	// Recalculate the vertical size on a browser window resize event
	Event.on(window, "resize", function(e) {
		LogicECM.module.Base.Util.setHeight();
	}, this, true);

	Event.onAvailable('bd', function() {
		LogicECM.module.Base.Util.setHeight();
	});

	YAHOO.Bubbling.on("showPanel", function(layer, args) {
		args[1].panel.cfg.setProperty("y", 100);
	});

	/**
	 * Base resizer
	 */
	LogicECM.module.Base.Resizer = function(htmlId) {
		LogicECM.module.Base.Resizer.superclass.constructor.call(this, "LogicECM.module.Base.Resizer");
		this.id = htmlId;
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
            Alfresco.util.ComponentManager.register(this);
        },
		onResize: function(width) {
            var divLeft = Dom.get(this.options.divLeft);
            if (!divLeft) {
                return;
            }
			var cn = divLeft.childNodes,
					handle = cn[cn.length - 1];

			Dom.setStyle(divLeft, "height", "auto");
			Dom.setStyle(handle, "height", "");

            var contentMainDiv = Dom.getAncestorBy(handle,
                function(node) {
                    return YAHOO.util.Selector.test(node, "#lecm-content-main");
                });  // вернуть contentMainDiv именно для этого ресайзера (в разделе администрирования их может быть два единовременно)
			var h = Dom.getY("lecm-content-ft") - Dom.getY(contentMainDiv);

			if (h < this.MIN_FILTER_PANEL_HEIGHT) {
				h = this.MIN_FILTER_PANEL_HEIGHT;
			}

			Dom.setStyle(handle, "height", h + "px");

			if (width !== undefined) {
				// 8px breathing space for resize gripper
				Dom.setStyle(this.options.divRight, "margin-left", this.marginLeft + width + "px");
			}
			YAHOO.Bubbling.fire("SetHeaderWidth");

			// Callback
			this.onResizeNotification();
		},

		onResizeNotification: function () {
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
