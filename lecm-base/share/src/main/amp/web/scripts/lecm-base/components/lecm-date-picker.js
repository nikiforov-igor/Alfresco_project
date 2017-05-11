/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * DatePicker component.
 *
 * @namespace Alfresco
 * @class LogicECM.DatePicker
 */
(function() {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        KeyListener = YAHOO.util.KeyListener,
        Bubbling = YAHOO.Bubbling;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML;

    /**
     * DatePicker constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @param {String} currentValueHtmlId The HTML id of the parent element
     * @return {LogicECM.DatePicker} The new DatePicker instance
     * @constructor
     */
    LogicECM.DatePicker = function(htmlId, currentValueHtmlId) {
        // Mandatory properties
        this.name = "LogicECM.DatePicker";
        this.id = htmlId;
        this.currentValueHtmlId = currentValueHtmlId;

        /* Register this component */
        Alfresco.util.ComponentManager.register(this);

        /* Load YUI Components */
        Alfresco.util.YUILoaderHelper.require(["button", "calendar"], this.onComponentsLoaded, this);

        // Initialise prototype properties
        this.widgets = {};

		Bubbling.on("readonlyControl", this.onReadonlyControl, this);
	    Bubbling.on("disableControl", this.onDisableControl, this);
	    Bubbling.on("enableControl", this.onEnableControl, this);
        Bubbling.on("hideControl", this.onHideControl, this);
        Bubbling.on("showControl", this.onShowControl, this);
	    Bubbling.on("handleFieldChange", this.onHandleFieldChange, this);
	    Bubbling.on("showDatePicker", this.hidePickerWhenAnotherIsOpening, this);
		
        // ALFFIVE-139
        // Изначально загружается версия 1.6.2 с плагином inputmask
        // Однако, потом отрабатывает dojo и перекрывает версию на 1.11		
        // noConflict вернёт версию 1.6.2 со всеми плагинами

        if(!$.inputmask) {
            $.noConflict();
            $ = $ || jQuery; // Возможен вариант, когда предыдущей версии нету, восстановим что есть
        }        

        return this;
    };

    LogicECM.DatePicker.prototype =
            {
                /**
                 * Object container for initialization options
                 *
                 * @property options
                 * @type object
                 */
                options: {
                    /**
                     * The current value
                     *
                     * @property currentValue
                     * @type string
                     */
                    currentValue: "",
                    defaultScript: null,
                    destination: null,
                    itemKind: null,
                    /**
                     * Flag to determine whether a time field should be visible
                     *
                     * @property showTime
                     * @type boolean
                     * @default false
                     */
                    showTime: false,
                    /**
                     * Flag to determine whether the picker is in disabled mode
                     *
                     * @property disabled
                     * @type boolean
                     * @default false
                     */
                    disabled: false,
                    /**
                     * Flag to indicate whether the field is mandatory
                     *
                     * @property mandatory
                     * @type boolean
                     * @default false
                     */
                    mandatory: false,
                    /**
                     * Ограничения по мин/макс значению
                     */
                    minLimit: null,
                    maxLimit: null,
	                fieldId: null,
	                formId: false,
                    changeFireAction: null,
                    mask: "dd.mm.yyyy",
                    placeholder: Alfresco.util.message("lecm.form.control.date-picker.display.date.format")
                },

                /**
                 * Vertical indent for picker from text of date
                 */
                datePickerVerticalIndent: 5,

                tempDisabled: false,

				readonly: false,

                /**
                 * Object container for storing YUI widget instances.
                 *
                 * @property widgets
                 * @type object
                 */
                widgets: null,
                /**
                 * Set multiple initialization options at once.
                 *
                 * @method setOptions
                 * @param obj {object} Object literal specifying a set of options
                 * @return {LogicECM.DatePicker} returns 'this' for method chaining
                 */
                setOptions: function DatePicker_setOptions(obj) {
                    var me = this;
                    me.options = YAHOO.lang.merge(me.options, obj);
                    return me;
                },
                /**
                 * Set messages for this component.
                 *
                 * @method setMessages
                 * @param obj {object} Object literal specifying a set of messages
                 * @return {LogicECM.DatePicker} returns 'this' for method chaining
                 */
                setMessages: function DatePicker_setMessages(obj) {
                    var me = this;
                    Alfresco.util.addMessages(obj, me.name);
                    return me;
                },
                /**
                 * Fired by YUILoaderHelper when required component script files have
                 * been loaded into the browser.
                 *
                 * @method onComponentsLoaded
                 */
                onComponentsLoaded: function DatePicker_onComponentsLoaded() {
                    //Event.onContentReady(this.id, this.draw, this, true);
                },
                /**
                 * Fired by YUI when parent element is available for scripting.
                 * Component initialisation, including instantiation of YUI widgets and event listener binding.
                 *
                 * @method onReady
                 */
                draw: function() {
                    LogicECM.module.Base.Util.createComponentReadyElementId(this.id + "-date", this.options.formId, this.options.fieldId);

                    var me = this;
                    var theDate = null;

                    // calculate current date
                    if (me.options.currentValue !== null && me.options.currentValue !== "" && me.options.currentValue !== "now") {
                        theDate = Alfresco.util.fromISO8601(me.options.currentValue);
                    }
                    else {
                        if (me.options.defaultScript && (me.options.destination || me.options.itemKind === "workflow")) {
                            var dataObj = {
                                nodeRef: me.options.destination
                            };
                            Alfresco.util.Ajax.request({
                                url: Alfresco.constants.PROXY_URI_RELATIVE + me.options.defaultScript,
                                dataObj: dataObj,
                                successCallback: {
                                    scope: me,
                                    fn: function(response) {
                                        if (response.json != null) {
                                            me.options.currentValue=response.json["date"];
                                            me.draw();
                                        }
                                    }
                                },
                                failureMessage: "message.failure",
                                execScripts: false,
                                scope: me
                            });
                            return;
                        } else {
                            theDate = new Date();
                        }
                    }

                    var page = (theDate.getMonth() + 1) + "/" + theDate.getFullYear();
                    var selected = (theDate.getMonth() + 1) + "/" + theDate.getDate() + "/" + theDate.getFullYear();
                    var dateEntry = theDate.toString(me._msg("lecm.form.control.date-picker.entry.date.format"));
                    var timeEntry = theDate.toString(me._msg("form.control.date-picker.entry.time.format"));

                    // populate the input fields
                    if (me.options.currentValue !== "") {
                        // show the formatted date
                        Dom.get(me.id + "-date").value = dateEntry;

                        if (me.options.showTime) {
                            Dom.get(me.id + "-time").value = timeEntry;
                        }
                    }

                    // construct the picker
                    if (!me.options.disabled) {
                        me.widgets.calendar = new YAHOO.widget.Calendar(me.id, {
                            title: me._msg("form.control.date-picker.choose"),
                            close: true,
                            navigator: {
                                strings : {
                                    month: me._msg("lable.calendar-month-label"),
                                    year: me._msg("lable.calendar-year-label"),
                                    submit: me._msg("lable.calendar-ok-label"),
                                    cancel: me._msg("lable.calendar-cancel-label"),
                                    invalidYear: me._msg("lable.calendar-wrongyear-label")
                                }
                            }
                        });
                        me.widgets.calendar.cfg.setProperty("pagedate", page);
                        me.widgets.calendar.cfg.setProperty("selected", selected);

                        if (me.options.minLimit && me.options.minLimit != "") {
                            var minDate = Alfresco.util.fromISO8601(me.options.minLimit);
                            if (minDate) {
                                me.widgets.calendar.cfg.setProperty("mindate", Alfresco.util.formatDate(minDate, "mm/dd/yyyy"));
                            }
                        }

                        if (me.options.maxLimit && me.options.maxLimit != "") {
                            var maxDate = Alfresco.util.fromISO8601(me.options.maxLimit);
                            if (maxDate) {
                                me.widgets.calendar.cfg.setProperty("maxdate", Alfresco.util.formatDate(maxDate, "mm/dd/yyyy"));
                            }
                        }

                        Alfresco.util.calI18nParams(me.widgets.calendar);

                        // setup events
                        me.widgets.calendar.selectEvent.subscribe(me._handlePickerChange, me, true);

                        // если в body уже есть календарь(и) с таким id, нужно удалить
                        var samePickers = Selector.query("body > #" + me.id);
                        if (samePickers && !samePickers.isEmpty) {
                            var body = Selector.query('body')[0];
                            for (var i = 0; i < samePickers.length; i++) {
                                body.removeChild(samePickers[i]);
                            }
                        }

                        // render the calendar control
                        me.widgets.calendar.render();
                    }

                    if (this.options.mask) {
                        $("#" + me.id + "-date").inputmask(this.options.mask, {
                            placeholder:this.options.placeholder
                        });
                    }

                    Event.addListener(me.id + "-date", "keyup", me._inputKeyup, me, true);
                    Event.addListener(me.id + "-time", "keyup", me._inputKeyup, me, true);

                    // Hide Calendar if we mouseup anywhere in the document other than the calendar
                    Event.on(document, "mouseup", function(e) {
                        var inputEl = Dom.get(me.id + "-date");
                        var iconEl = Dom.get(me.id + "-icon");
                        var el = Event.getTarget(e);
                        if (me.widgets.calendar) {
                            var dialogEl = me.widgets.calendar.oDomContainer;

                            if (el && el != dialogEl && !Dom.isAncestor(dialogEl, el) && el != inputEl && el != iconEl) {
                                me._hidePicker();
                            }
                        }
                    });

                    Event.addListener(me.id + "-date", "click", me._showPicker, me, true);

                    var iconEl = Dom.get(me.id + "-icon");
                    if (iconEl) {
                        // setup keyboard enter events on the image instead of the link to get focus outline displayed
                        Alfresco.util.useAsButton(iconEl, me._showPicker, null, me);
                        //Event.addListener(me.id + "-icon", "click", me._showPicker, me, true);
                    }

                    // register a validation handler for the date entry field so that the submit
                    // button disables when an invalid date is entered
                    Bubbling.fire("registerValidationHandler",
                            {
                                fieldId: me.id + "-date",
                                handler: Alfresco.forms.validation.validDateTime,
                                when: "keyup"
                            });

                    // register a validation handler for the time entry field (if applicable)
                    // so that the submit button disables when an invalid date is entered
                    if (me.options.showTime) {
                        Bubbling.fire("registerValidationHandler",
                                {
                                    fieldId: me.id + "-time",
                                    handler: Alfresco.forms.validation.validDateTime,
                                    when: "keyup"
                                });
                    }

                    // If value was set in visible fields, make sure they are validated and put in the hidden field as well
                    if (me.options.currentValue !== "") {
                        me._handleFieldChange(null);
                        Bubbling.fire("mandatoryControlValueUpdated", me);
                    }

	                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
                },
                /**
                 * Handles the date picker icon being clicked.
                 *
                 * @method _showPicker
                 * @param event The event that occurred
                 * @private
                 * так как в диалоговых окнах календарь рисуется плохо,
                 * мы выносим его в body и позиционируем по кнопке его вызова
                 */
                _showPicker: function DatePicker__showPicker(event) {
                    //проверяем на возможность редактирования, если редактировать нельзя - ничего не показываем
                    var dateEl = Dom.get(this.id + "-date");
                    if (dateEl && dateEl.readOnly) {
                        return;
                    }
                    // При открытии календаря посылаем событие, чтобы закрыть все другие открытые календари
                    Bubbling.fire("showDatePicker", {datepicker : this});

	                if (!this.tempDisabled && !this.readonly) {
		                var me = this;
		                var picker = Dom.get(me.id);
		                var parent = picker.parentNode;
		                var clicked = Event.getTarget(event) || dateEl;

		                if (!Dom.hasClass(parent, "alfresco-share")) {                      // если календарь лежит не в body, нужно перенести
                            var body = Selector.query('body')[0];

			                body.appendChild(picker);

                            // Если поле, вызывающее календарь, находится в диалоговом окне,
                            // при клике в календарь фокус постоянно уходит на кнопку закрытия диалога
                            // поэтому удаляем атрибут href из кнопки закрытия
                            var dialog = Dom.getAncestorByClassName(clicked, "yui-panel");
                            if (dialog) {
                                var closeLink = Dom.getFirstChild(dialog);
                                if (closeLink && Dom.hasClass(closeLink, "container-close")) {
                                    closeLink.removeAttribute("href");
                                }
                            }
		                }
                        me.widgets.calendar.show();                                         // сначала сделать видимым, потом позиционировать, иначе позиционирование не отрабатывает

                        var x = (Dom.getX(clicked) + clicked.offsetWidth) - picker.offsetWidth - this.datePickerVerticalIndent;
                        if (Dom.getX(picker) != x) {
                            Dom.setX(picker, x);
                        }

                        var y = Dom.getY(clicked) + clicked.offsetHeight - this.datePickerVerticalIndent;
		                var height = picker.offsetHeight;

		                if (y + height > Dom.getViewportHeight()) {                        // если календарь не помещается до низа окна
                            y -= height + this.datePickerVerticalIndent * 3;                                           // откроем его вверх
		                }
                        if (Dom.getY(picker) != y) {
                            Dom.setY(picker, y);
                        }
                        dateEl.focus();
                    }
                },
                /**
                 * Handles the date picker hiding.
                 *
                 * @method _hidePicker
                 * @private
                 * Hide calendar if the calendar was open (Unfortunately there is no proper yui api method for me)
                 */
                _hidePicker: function() {
                    if (this.widgets.calendar && this.widgets.calendar.oDomContainer &&
                            Dom.getStyle(this.widgets.calendar.oDomContainer, "display") != "none") {
                        this.widgets.calendar.hide();
                    }
                },
                /**
                 * Handles the date being changed in the date picker YUI control.
                 *
                 * @method _handlePickerChange
                 * @param type
                 * @param args
                 * @param obj
                 * @private
                 */
                _handlePickerChange: function DatePicker__handlePickerChange(type, args, obj) {
                    // update the date field
                    var me = this;
                    var selected = args[0];
                    var selDate = me.widgets.calendar.toDate(selected[0]);
                    var dateEntry = selDate.toString(me._msg("lecm.form.control.date-picker.entry.date.format"));
                    Dom.get(me.id + "-date").value = dateEntry;

                    // update the time field if necessary
                    if (me.options.showTime) {
                        var time = Dom.get(me.id + "-time").value;
                        if (time.length > 0) {
                            var dateTime = Dom.get(me.id + "-date").value + " " + time;
                            var dateTimePattern = me._msg("lecm.form.control.date-picker.entry.date.format") + " " + me._msg("form.control.date-picker.entry.time.format");
                            selDate = Date.parseExact(dateTime, dateTimePattern);
                        }
                    } else {
                        selDate.setHours(12);
                        selDate.setMinutes(0);
                        selDate.setSeconds(0);
                    }

                    // if we have a valid date, convert to ISO format and set value on hidden field
                    if (selDate != null) {
                        Dom.removeClass(me.id + "-date", "invalid");
                        if (me.options.showTime) {
                            Dom.removeClass(me.id + "-time", "invalid");
                        }
                        var isoValue = Alfresco.util.toISO8601(selDate, {"milliseconds": true});
                        Dom.get(me.currentValueHtmlId).value = isoValue;

                        if (Alfresco.logger.isDebugEnabled())
                            Alfresco.logger.debug("Hidden field '" + me.currentValueHtmlId + "' updated to '" + isoValue + "'");

                        // always inform the forms runtime that the control value has been updated
                        Bubbling.fire("mandatoryControlValueUpdated", me);

                        if (me.options.changeFireAction) {
                            Bubbling.fire(me.options.changeFireAction, {
                                date: isoValue
                            });
                        }
                    }
                    else {
                        Dom.addClass(me.id + "-date", "invalid");

                        if (me.options.showTime) {
                            Dom.addClass(me.id + "-time", "invalid");
                        }
                    }

                    me._hidePicker();
                },

                _inputKeyup: function (event) {
                    this._hidePicker();
                    this._handleFieldChange(event);
                },

                /**
                 * Handles the date or time being changed in either input field.
                 *
                 * @method _handleFieldChange
                 * @param event The event that occurred
                 * @private
                 */
                _handleFieldChange: function DatePicker__handleFieldChange(event) {
                    var me = this;
                    var changedDate = Dom.get(me.id + "-date").value;
                    if (changedDate.length > 0) {
                        // Only set for actual value changes so tab or shift events doesn't remove the "text selection" of the input field
                        if (event == undefined || (event.keyCode != KeyListener.KEY.TAB && event.keyCode != KeyListener.KEY.SHIFT)) {
                            // convert to format expected by YUI
                            var parsedDate = Date.parseExact(changedDate, me._msg("lecm.form.control.date-picker.entry.date.format"));
                            if (me.options.showTime) {
                                var time = Dom.get(me.id + "-time").value;
                                if (time.length > 0) {
                                    var dateTime = Dom.get(me.id + "-date").value + " " + time;
                                    var dateTimePattern = me._msg("lecm.form.control.date-picker.entry.date.format") + " " + me._msg("form.control.date-picker.entry.time.format");
                                    parsedDate = Date.parseExact(dateTime, dateTimePattern);
                                }
                            } else if (parsedDate) {
                                parsedDate.setHours(12);
                                parsedDate.setMinutes(0);
                                parsedDate.setSeconds(0);
                            }

                            var minLimitDate = null;
                            if (this.options.minLimit) {
                                minLimitDate = Alfresco.util.fromISO8601(this.options.minLimit);
                            }

                            if ((parsedDate != null && minLimitDate == null) ||
                                (parsedDate != null && minLimitDate != null && parsedDate >= minLimitDate)) {
                                if (me.options.disabled) {
                                    Dom.removeClass(me.id + "-date", "invalid");
                                } else {
                                    me.widgets.calendar.cfg.setProperty("selected", [[parsedDate.getFullYear(), parsedDate.getMonth() + 1, parsedDate.getDate()]])
                                    var isoValue = Alfresco.util.toISO8601(parsedDate, {"milliseconds": true});
                                    Dom.get(me.currentValueHtmlId).value = isoValue;
                                    var selectedDates = me.widgets.calendar.getSelectedDates();
                                    if (selectedDates.length > 0) {
                                        Dom.removeClass(me.id + "-date", "invalid");
                                        var firstDate = selectedDates[0];
                                        me.widgets.calendar.cfg.setProperty("pagedate", (firstDate.getMonth() + 1) + "/" + firstDate.getFullYear());
                                        me.widgets.calendar.render();

                                        // NOTE: we don't need to check the time value in here as the _handlePickerChange
                                        //       function gets called as well as a result of rendering the picker above,
                                        //       that's also why we don't update the hidden field in here either.
                                    }
                                    if (me.options.changeFireAction) {
                                        Bubbling.fire(me.options.changeFireAction, {
                                            date: isoValue
                                        });
                                    }
                                }
                            }
                            else {
                                Dom.addClass(me.id + "-date", "invalid");
                                Dom.get(me.currentValueHtmlId).value = "";
                            }
                        }
                    }
                    else {
                        // when the date is completely cleared remove the hidden field and remove the invalid class
                        Dom.removeClass(me.id + "-date", "invalid");
                        Dom.get(me.currentValueHtmlId).value = "";

                        if (Alfresco.logger.isDebugEnabled())
                            Alfresco.logger.debug("Hidden field '" + me.currentValueHtmlId + "' has been reset");

                        // inform the forms runtime that the control value has been updated
                    }
                    if (me.options.mandatory || YAHOO.env.ua.ie) {
                        Bubbling.fire("mandatoryControlValueUpdated", me);
                    }
                },

                /**
                 * При открытии календаря посылаем событие, чтобы закрыть все другие календари,
                 * так как при открытых нескольких календарях поведение некорректно.
                 * Это метод, который отрабатывает при получении календарем события.
                 */
                hidePickerWhenAnotherIsOpening: function(bubblingName, args) {
                    var openingPicker = args[1].datepicker;

                    if (openingPicker.id != this.id) {
                         this._hidePicker();
                    }
                },

                /**
                 * Gets a custom message
                 *
                 * @method _msg
                 * @param messageId {string} The messageId to retrieve
                 * @return {string} The custom message
                 * @private
                 */
                _msg: function DatePicker__msg(messageId) {
                    var me = this;
                    return Alfresco.util.message.call(me, messageId, "LogicECM.DatePicker", Array.prototype.slice.call(arguments).slice(1));
                },

				onReadonlyControl: function (layer, args) {
					var input, fn;
					if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
						this.readonly = args[1].readonly;
						input = Dom.get(this.id + "-date");
						fn = args[1].readonly ? input.setAttribute : input.removeAttribute;
						fn.call(input, "readonly", "");
						if (args[1].readonly) {
							this.widgets.calendar.hide();
							Dom.addClass(this.id + '-icon', 'icon-readonly');
						}
					}
				},

	            onDisableControl: function (layer, args) {
		            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
			            this.widgets.calendar.hide();
			            this.tempDisabled = true;
			            Dom.get(this.id + "-date").disabled = true;
                        if (this.options.showTime) {
                            Dom.get(this.id + "-time").disabled = true;
                        }
			            Dom.get(this.id + "-date").disabled = true;
                        Dom.addClass(this.id + '-icon', 'icon-disabled');
		            }
	            },

	            onEnableControl: function (layer, args) {
		            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
			            this.tempDisabled = false;
			            if (!this.options.disabled) {
				            Dom.get(this.id + "-date").disabled = false;
                            if (this.options.showTime) {
                                Dom.get(this.id + "-time").disabled = false;
                            }
                            Dom.removeClass(this.id + '-icon', 'icon-disabled');
			            }
		            }
	            },

                onHideControl: function (layer, args) {
                    if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                        Dom.setStyle(this.id + "-parent", "display", "none");
                    }
                },

                onShowControl: function (layer, args) {
                    if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                        Dom.setStyle(this.id + "-parent", "display", "block");
                    }
                },

                onHandleFieldChange: function (layer, args) {
		            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
			            this._handleFieldChange();
		            }
	            }
            };
})();
