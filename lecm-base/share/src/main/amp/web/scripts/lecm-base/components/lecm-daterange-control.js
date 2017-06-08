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
 * DateRange component.
 *
 * @namespace LogicECM
 * @class LogicECM.DateRangeControl
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        KeyListener = YAHOO.util.KeyListener;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML;

    /**
     * DateRangeControl constructor.
     *
     * @param {String} htmlId The HTML id of the control element
     * @param {String} valueHtmlId The HTML id prefix of the value elements
     * @return {LogicECM.DateRangeControl} The new DateRangeControl instance
     * @constructor
     */
    LogicECM.DateRangeControl = function (htmlId, valueHtmlId) {
        LogicECM.DateRangeControl.superclass.constructor.call(this, "LogicECM.DateRangeControl", htmlId, ["button", "calendar"]);

        this.valueHtmlId = valueHtmlId;
        this.currentFromDate = "";
        this.currentToDate = "";

        YAHOO.Bubbling.on("reInitializeControl", this.onReInitializeControl, this);

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

    YAHOO.extend(LogicECM.DateRangeControl, Alfresco.component.Base,
        {
            /**
             * Current From date value
             *
             * @property currentFromDate
             * @type string
             */
            currentFromDate: null,

            /**
             * Current To date value
             *
             * @property currentToDate
             * @type string
             */
            currentToDate: null,

            options: {
                /**
                 * Ограничения по мин/макс значению
                 */
                minFromLimit: null,
                maxFromLimit: null,
                minToLimit: null,
                maxToLimit: null,
                toDateDefault: "NOW", //NEXT_MONTH, START_YEAR, LAST_MONTH, NOW, TOMORROW, EMPTY
                fromDateDefault: "LAST_MONTH",
                fillDates: false,
                mask: "dd.mm.yyyy",
                placeholder: Alfresco.util.message("lecm.form.control.date-picker.display.date.format"),
                fieldId: null,
                formId: false,
                defaultValue: null
            },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Component initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DateRange_onReady() {
                var toDate = this._getDateByKey("NOW");
                var fromDate = this._getDateByKey("NOW");

                if (this.options.defaultValue) {
                    Dom.get(this.valueHtmlId).value = this.options.defaultValue;
                }
                if (Dom.get(this.valueHtmlId).value) {
                    var fullDate = Dom.get(this.valueHtmlId).value.split("|");
                    if (fullDate[0]) {
                        // Use Date.parse to support non ISO8601 date defaults
	                    fromDate = Alfresco.util.fromISO8601(fullDate[0]);
                        Dom.get(this.id + "-date-from").value = fromDate.toString(this._msg("form.control.date-picker.entry.date.format"));
                        this.currentFromDate = fullDate[0];
                    } else {
                        Dom.get(this.id + "-date-from").value = "";
                        this.currentFromDate = "";
                    }
                    if (fullDate[1]) {
                        // Use Date.parse to support non ISO8601 date defaults
	                    toDate = Alfresco.util.fromISO8601(fullDate[1]);
	                    Dom.get(this.id + "-date-to").value = toDate.toString(this._msg("form.control.date-picker.entry.date.format"));
                        this.currentToDate = fullDate[1];
                    } else {
                        Dom.get(this.id + "-date-to").value = "";
                        this.currentToDate = "";
                    }
                } else {
                    if (this.options.fillDates) {
                        var fromDateFilled = this._getDateByKey(this.options.fromDateDefault);
                        var toDateFilled = this._getDateByKey(this.options.toDateDefault);

                        if (fromDateFilled) {
                            fromDate = fromDateFilled;
                            Dom.get(this.id + "-date-from").value = fromDate.toString(this._msg("form.control.date-picker.entry.date.format"));
                            this.currentFromDate = Alfresco.util.toISO8601(fromDate, {"milliseconds": false});
                        }
                        if (toDateFilled) {
                            toDate = toDateFilled;
                            Dom.get(this.id + "-date-to").value = toDate.toString(this._msg("form.control.date-picker.entry.date.format"));
                            this.currentToDate = Alfresco.util.toISO8601(toDate, {"milliseconds": false});
                        }
                    }
                }

                this._updateCurrentValue();

                // construct the pickers
                var page = (fromDate.getMonth() + 1) + "/" + fromDate.getFullYear();
                var selected = (fromDate.getMonth() + 1) + "/" + fromDate.getDate() + "/" + fromDate.getFullYear();

                this.widgets.calendarFrom = new YAHOO.widget.Calendar(this.id + "-from", this.id + "-from", {
                    title: this.msg("form.control.date-picker.choose"),
                    close: true,
                    navigator: {
                        strings : {
                            month: this.msg("lable.calendar-month-label"),
                            year: this.msg("lable.calendar-year-label"),
                            submit: this.msg("lable.calendar-ok-label"),
                            cancel: this.msg("lable.calendar-cancel-label"),
                            invalidYear: this.msg("lable.calendar-wrongyear-label")
                        }
                    }
                });

                this.widgets.calendarFrom.cfg.setProperty("pagedate", page);
                this.widgets.calendarFrom.cfg.setProperty("selected", selected);
                if (this.options.minFromLimit) {
                    var minDate = Alfresco.util.fromISO8601(this.options.minFromLimit);
                    if (minDate) {
                        this.widgets.calendarFrom.cfg.setProperty("mindate", Alfresco.util.formatDate(minDate, "mm/dd/yyyy"));
                    }
                }
                if (this.options.maxFromLimit) {
                    var maxDate = Alfresco.util.fromISO8601(this.options.maxFromLimit);
                    if (maxDate) {
                        this.widgets.calendarFrom.cfg.setProperty("maxdate", Alfresco.util.formatDate(maxDate, "mm/dd/yyyy"));
                    }
                }

                this.widgets.calendarFrom.hideEvent.subscribe(function () {
                    // Focus icon after calendar is closed
                    Dom.get(this.id + "-icon-from").focus();
                }, this, true);

                Alfresco.util.calI18nParams(this.widgets.calendarFrom);

                var iconEl = Dom.get(this.id + "-icon-from");
                if (iconEl) {
                    // setup keyboard enter events on the image instead of the link to get focus outline displayed
                    Alfresco.util.useAsButton(iconEl, this._showPicker, null, this);
                    Event.addListener(this.id + "-icon-from", "click", this._showPicker, this, true);
                }

                page = (toDate.getMonth() + 1) + "/" + toDate.getFullYear();
                selected = (toDate.getMonth() + 1) + "/" + toDate.getDate() + "/" + toDate.getFullYear();
                this.widgets.calendarTo = new YAHOO.widget.Calendar(this.id + "-to", this.id + "-to", {
                    title: this.msg("form.control.date-picker.choose"),
                    close: true,
                    navigator: {
                        strings : {
                            month: this.msg("lable.calendar-month-label"),
                            year: this.msg("lable.calendar-year-label"),
                            submit: this.msg("lable.calendar-ok-label"),
                            cancel: this.msg("lable.calendar-cancel-label"),
                            invalidYear: this.msg("lable.calendar-wrongyear-label")
                        }
                    }
                });
                this.widgets.calendarTo.cfg.setProperty("pagedate", page);
                this.widgets.calendarTo.cfg.setProperty("selected", selected);
                if (this.options.minToLimit) {
                    var minToDate = Alfresco.util.fromISO8601(this.options.minToLimit);
                    if (minToDate) {
                        this.widgets.calendarTo.cfg.setProperty("mindate", Alfresco.util.formatDate(minToDate, "mm/dd/yyyy"));
                    }
                }
                if (this.options.maxToLimit) {
                    var maxToDate = Alfresco.util.fromISO8601(this.options.maxToLimit);
                    if (maxToDate) {
                        this.widgets.calendarTo.cfg.setProperty("maxdate", Alfresco.util.formatDate(maxToDate, "mm/dd/yyyy"));
                    }
                }

                this.widgets.calendarTo.hideEvent.subscribe(function () {
                    // Focus icon after calendar is closed
                    Dom.get(this.id + "-icon-to").focus();
                }, this, true);

                Alfresco.util.calI18nParams(this.widgets.calendarTo);

                iconEl = Dom.get(this.id + "-icon-to");
                if (iconEl) {
                    // setup keyboard enter events on the image instead of the link to get focus outline displayed
                    Alfresco.util.useAsButton(iconEl, this._showPicker, null, this);
                    Event.addListener(this.id + "-icon-to", "click", this._showPicker, this, true);
                }

                if (this.options.mask) {
                    $("#" + this.id + "-date-from").inputmask(this.options.mask, {
                        placeholder: this.options.placeholder
                    });
                    $("#" + this.id + "-date-to").inputmask(this.options.mask, {
                        placeholder: this.options.placeholder
                    });
                }

                // Hide Calendar if we click anywhere in the document other than the calendar
                Event.on(document, "click", function (e) {
                    var el = Event.getTarget(e);
                    if (el) {
                        if (this.widgets.calendarFrom) {
                            var dialogFrom = this.widgets.calendarFrom.oDomContainer;
                            var inputFrom = Dom.get(this.id + "-date-from");
                            var iconFrom = Dom.get(this.id + "-icon-from");
                            if (el != dialogFrom && !Dom.isAncestor(dialogFrom, el) && el != inputFrom && el != iconFrom) {
                                if (Dom.getStyle(dialogFrom, "display") != "none") {
                                    this.widgets.calendarFrom.hide();
                                }
                            }
                        }
                        if (this.widgets.calendarTo) {
                            var dialogTo = this.widgets.calendarTo.oDomContainer;
                            var inputTo = Dom.get(this.id + "-date-to");
                            var iconTo = Dom.get(this.id + "-icon-to");
                            if (el != dialogTo && !Dom.isAncestor(dialogTo, el) && el != inputTo && el != iconTo) {
                                if (Dom.getStyle(dialogTo, "display") != "none") {
                                    this.widgets.calendarTo.hide();
                                }
                            }
                        }
                    }
                }, this, true);

                // setup events
                this.widgets.calendarFrom.selectEvent.subscribe(this._handlePickerChangeFrom, this, true);
                this.widgets.calendarFrom.selectEvent.subscribe(this.onChangeDates, this, true);
                Event.addListener(this.id + "-date-from", "keyup", this._handleFieldChangeFrom, this, true);
                Event.addListener(this.id + "-icon-from", "click", this._showPickerFrom, this, true);
                YAHOO.Bubbling.fire("registerValidationHandler",
                    {
                        fieldId: this.id + "-date-from",
                        handler: Alfresco.forms.validation.validDateTime,
                        when: "keyup"
                    });

                this.widgets.calendarTo.selectEvent.subscribe(this._handlePickerChangeTo, this, true);
                this.widgets.calendarTo.selectEvent.subscribe(this.onChangeDates, this, true);
                Event.addListener(this.id + "-date-to", "keyup", this._handleFieldChangeTo, this, true);
                Event.addListener(this.id + "-icon-to", "click", this._showPickerTo, this, true);
                YAHOO.Bubbling.fire("registerValidationHandler",
                    {
                        fieldId: this.id + "-date-to",
                        handler: Alfresco.forms.validation.validDateTime,
                        when: "keyup"
                    });

                /*При навешивании маски ввода, Event.addListener(...,"change",...) глушится
                где-то в недрах jQuery. Поэтому событие onChange навешиваем через jQuery*/
                $("#" + this.id + "-date-from").change(this.onChangeDates.bind(this));
                $("#" + this.id + "-date-to").change(this.onChangeDates.bind(this));

                // render the calendar controls
                this.widgets.calendarFrom.render();
                this.widgets.calendarTo.render();

                // initial validation
                this.validate();
                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            },

            _getDateByKey: function (key) {
                var date = new Date();
                date.setHours(12);
                date.setMinutes(0);
                date.setSeconds(0);

                switch (key) {
                    case 'START_YEAR':
                        date.setMonth(0);
                        date.setDate(1);
                        break;
                    case 'NEXT_MONTH' :
                        date.setMonth(date.getMonth() + 1);
                        break;
                    case 'LAST_MONTH' :
                        date.setMonth(date.getMonth() - 1);
                        break;
                    case 'TOMORROW':
                        date.setDate(date.getDate() + 1);
                        break;
                    case 'NOW':
                        break;
                    case 'EMPTY':
                        date = null; /*сброс даты*/
                        break;
                    default:
                        break;
                }
                return date;
            },

            /**
             * Handles the date picker icon being clicked.
             *
             * @method _showPickerFrom
             * @param event The event that occurred
             * @private
             */
            _showPickerFrom: function DateRange__showPickerFrom(event) {
                this.widgets.calendarFrom.show();
            },

            /**
             * Handles the date picker icon being clicked.
             *
             * @method _showPickerTo
             * @param event The event that occurred
             * @private
             */
            _showPickerTo: function DateRange__showPickerTo(event) {
                this.widgets.calendarTo.show();
            },

            /**
             * Setting a new value into textField without cursor position losses
             *
             * @method _setValueAndSavePosition
             * @param obj
             * @param val
             * @private
             */
            _setValueAndSavePosition: function DateRange__setValueAndSavePosition(obj, val) {
                var startPos = obj.selectionStart;
                var endPos = obj.selectionEnd;
                var ie8 = false;
                if (typeof startPos == 'undefined') {
                    ie8 = true;
                    var range = this._getInputSelection(obj);
                    startPos = range.start;
                    endPos = range.end;
                }
                obj.value = val;
                if (startPos == endPos) {
                    if (!ie8) {
                        obj.selectionStart = startPos;
                        obj.selectionEnd = endPos;
                    } else {
                        this._setCaretPosition(obj, startPos);
                    }
                }
                else {
                    // do nothing
                }
            },

            /**
             * Get a cursor (caret) positions (is used for ie8)
             *
             * @method _getInputSelection
             * @param el
             * @private
             */
            _getInputSelection: function DateRange__getInputSelection(el) {
                var start = 0, end = 0, normalizedValue, range,
                    textInputRange, len, endRange;

                range = document.selection.createRange();

                if (range && range.parentElement() == el) {
                    len = el.value.length;
                    normalizedValue = el.value.replace(/\r\n/g, "\n");

                    // Create a working TextRange that lives only in the input
                    textInputRange = el.createTextRange();
                    textInputRange.moveToBookmark(range.getBookmark());

                    // Check if the start and end of the selection are at the very end
                    // of the input, since moveStart/moveEnd doesn't return what we want
                    // in those cases
                    endRange = el.createTextRange();
                    endRange.collapse(false);

                    if (textInputRange.compareEndPoints("StartToEnd", endRange) > -1) {
                        start = end = len;
                    } else {
                        start = -textInputRange.moveStart("character", -len);
                        start += normalizedValue.slice(0, start).split("\n").length - 1;

                        if (textInputRange.compareEndPoints("EndToEnd", endRange) > -1) {
                            end = len;
                        } else {
                            end = -textInputRange.moveEnd("character", -len);
                            end += normalizedValue.slice(0, end).split("\n").length - 1;
                        }
                    }
                }

                var res = { start: start, end: end };
                return res
            },

            /**
             * Set cursor (caret) position (is used for ie8)
             *
             * @method _setCaretPosition
             * @param elem
             * @param caretPos
             * @private
             */
            _setCaretPosition: function DateRange__setCaretPosition(elem, caretPos) {
                if (elem != null) {
                    if (elem.createTextRange) {
                        var range = elem.createTextRange();
                        range.move('character', caretPos);
                        range.select();
                    }
                    else {
                        if (elem.selectionStart) {
                            elem.focus();
                            elem.setSelectionRange(caretPos, caretPos);
                        }
                        else
                            elem.focus();
                    }
                }
            },

            /**
             * Handles the from date being changed in the date picker YUI control.
             *
             * @method _handlePickerChangeFrom
             * @param type
             * @param args
             * @param obj
             * @private
             */
            _handlePickerChangeFrom: function DateRange__handlePickerChangeFrom(type, args, obj) {
                // update the date field
                var selected = args[0];
                var selDate = this.widgets.calendarFrom.toDate(selected[0]);
                // if we have a valid date, convert to ISO format and set value on hidden field
                if (selDate != null) {
                    selDate.setHours(12);
                    selDate.setMinutes(0);
                    selDate.setSeconds(0);
                    var dateEntry = selDate.toString(this.msg("lecm.form.control.date-picker.entry.date.format"));
                    var obj = Dom.get(this.id + "-date-from");
                    this._setValueAndSavePosition(obj, dateEntry);

                    this.currentFromDate = Alfresco.util.toISO8601(selDate, {"milliseconds": false});
                    this._updateCurrentValue();

                    Dom.removeClass(this.id + "-date-from", "invalid");
                }

                // Hide calendar if the calendar was open (Unfortunately there is no proper yui api method for this)
                if (Dom.getStyle(this.id + "-from", "display") != "none") {
                    this.widgets.calendarFrom.hide();
                }
            },

            /**
             * Handles the from date being changed in the date picker YUI control.
             *
             * @method _handlePickerChangeTo
             * @param type
             * @param args
             * @param obj
             * @private
             */
            _handlePickerChangeTo: function DateRange__handlePickerChangeTo(type, args, obj) {
                // update the date field
                var selected = args[0];
                var selDate = this.widgets.calendarTo.toDate(selected[0]);

                // if we have a valid date, convert to ISO format and set value on hidden field
                if (selDate != null) {
                    selDate.setHours(12);
                    selDate.setMinutes(0);
                    selDate.setSeconds(0);

                    var dateEntry = selDate.toString(this.msg("lecm.form.control.date-picker.entry.date.format"));
                    var obj = Dom.get(this.id + "-date-to");
                    this._setValueAndSavePosition(obj, dateEntry);

                    this.currentToDate = Alfresco.util.toISO8601(selDate, {"milliseconds": false});
                    this._updateCurrentValue();

                    Dom.removeClass(this.id + "-date-to", "invalid");
                }

                // Hide calendar if the calendar was open (Unfortunately there is no proper yui api method for this)
                if (Dom.getStyle(this.id + "-to", "display") != "none") {
                    this.widgets.calendarTo.hide();
                }
            },

            /**
             * Updates the currently stored date range value in the hidden form field.
             *
             * @method _updateCurrentValue
             * @private
             */
            _updateCurrentValue: function DateRange__updateCurrentValue() {
                var value = this.currentFromDate + "|" + this.currentToDate;
                value = value == "|" ? "" : value;
                Dom.get(this.valueHtmlId).value = value;
            },

            /**
             * Handles the date or time being changed in either input field.
             *
             * @method _handleFieldChangeFrom
             * @param event The event that occurred
             * @private
             */
            _handleFieldChangeFrom: function DateRange__handleFieldChangeFrom(event) {
                this._handleChange(event, "-date-from", this.widgets.calendarFrom);
            },

            /**
             * Handles the date or time being changed in either input field.
             *
             * @method _handleFieldChangeFrom
             * @param event The event that occurred
             * @private
             */
            _handleFieldChangeTo: function DateRange__handleFieldChangeTo(event) {
                this._handleChange(event, "-date-to", this.widgets.calendarTo);
            },

            /**
             * Обработчик ручного редактирования поля
             *
             * @method _handleChange
             * @param event
             * @param prefix
             * @param calendar
             * @private
             */
            _handleChange: function(event, prefix, calendar) {
                var changedDate = Dom.get(this.id + prefix).value;
                if (changedDate.length) {
                    // Only set for actual value changes so tab or shift events doesn't remove the "text selection" of the input field
                    if (!event || (event.keyCode != KeyListener.KEY.TAB && event.keyCode != KeyListener.KEY.SHIFT)) {
                        // convert to format expected by YUI
                        var parsedDate = Date.parseExact(changedDate, this.msg("lecm.form.control.date-picker.entry.date.format"));
                        if (parsedDate) {
                            calendar.select((parsedDate.getMonth() + 1) + "/" + parsedDate.getDate() + "/" + parsedDate.getFullYear());
                            var selectedDates = calendar.getSelectedDates();
                            if (selectedDates.length) {
                                var firstDate = selectedDates[0];
                                calendar.cfg.setProperty("pagedate", (firstDate.getMonth() + 1) + "/" + firstDate.getFullYear());
                                calendar.render();
                            }
                        }
                    }
                } else {
                    if (prefix == "-date-from") {
                        this.currentFromDate = "";
                    } else {
                        this.currentToDate = "";
                    }
                    this._updateCurrentValue();
                }
            },

            _msg: function (messageId) {
                var me = this;
                return Alfresco.util.message.call(me, messageId, "LogicECM.DatePicker", Array.prototype.slice.call(arguments).slice(1));
            },

            onChangeDates: function() {
                return this.validate();
            },

            _checkDateLimits: function (date, min, max) {
                if (date) {
                    var minLimitDate = null;
                    if (min) {
                        minLimitDate = Alfresco.util.fromISO8601(min);
                    }
                    var maxLimitDate = null;
                    if (max) {
                        maxLimitDate = Alfresco.util.fromISO8601(max);
                    }
                    return (!minLimitDate || minLimitDate <= date) && (!maxLimitDate || date <= maxLimitDate);
                }
                return true;
            },

            validate: function () {
                var valid = true;
                if (this.widgets.calendarFrom && this.widgets.calendarTo) {
                    var startPickerId = this.id + "-date-from";
                    var endPickerId = this.id + "-date-to";
                    var startDate = Date.parseExact(Dom.get(startPickerId).value, this.msg("lecm.form.control.date-picker.entry.date.format"));
                    var endDate = Date.parseExact(Dom.get(endPickerId).value, this.msg("lecm.form.control.date-picker.entry.date.format"));

                    var validFrom = this._checkDateLimits(startDate, this.options.minFromLimit, this.options.maxFromLimit);
                    var validTo = this._checkDateLimits(endDate, this.options.minToLimit, this.options.maxToLimit);
                    var validRange = true;

                    if (startDate && endDate) {
                        if (startDate > endDate) {
                            validRange = false;
                        }
                    }

                    if (!validRange) {
                        Dom.addClass(startPickerId, "invalid");
                        Dom.addClass(endPickerId, "invalid");
                        valid = false;
                    } else {
                        if (validFrom) {
                            Dom.removeClass(startPickerId, "invalid");
                        } else {
                            Dom.addClass(startPickerId, "invalid");
                        }

                        if (validTo) {
                            Dom.removeClass(endPickerId, "invalid");
                        } else {
                            Dom.addClass(endPickerId, "invalid");
                        }

                        valid = validFrom && validTo;
                    }

                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this.widgets.calendarFrom);
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this.widgets.calendarTo);
                }
                return valid;
            },

            onReInitializeControl: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    var options = args[1].options;
                    if (options != null) {
                        this.setOptions(options);
                    }
                    this.currentFromDate = "";
                    this.currentToDate = "";

                    for (var i in this.widgets) {
                        if (this.widgets.hasOwnProperty(i)) {
                            var w = this.widgets[i];

                            if (YAHOO.lang.isFunction(w.destroy)) {
                                w.destroy();
                            }
                        }
                    }
                    this.onReady();
                }
            }
        });
})();
