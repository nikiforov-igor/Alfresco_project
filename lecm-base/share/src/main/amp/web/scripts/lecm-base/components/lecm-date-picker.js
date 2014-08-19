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
        KeyListener = YAHOO.util.KeyListener;

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
        var me = this;
        // Mandatory properties
        me.name = "LogicECM.DatePicker";
        me.id = htmlId;
        me.currentValueHtmlId = currentValueHtmlId;

        /* Register this component */
        Alfresco.util.ComponentManager.register(me);

        /* Load YUI Components */
        Alfresco.util.YUILoaderHelper.require(["button", "calendar"], me.onComponentsLoaded, me);

        // Initialise prototype properties
        me.widgets = {};

        return me;
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
                    maxLimit: null
                },
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
                    var me = this;
                    var theDate = null;

                    // calculate current date
                    if (me.options.currentValue !== null && me.options.currentValue !== "") {
                        theDate = Alfresco.util.fromISO8601(me.options.currentValue);
                    }
                    else {
                        if (me.options.defaultScript && me.options.destination) {
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
                    var dateEntry = theDate.toString(me._msg("form.control.date-picker.entry.date.format"));
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
                        me.widgets.calendar = new YAHOO.widget.Calendar(me.id, me.id, {title: me._msg("form.control.date-picker.choose"), close: true, navigator: true});
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
                        me.widgets.calendar.hideEvent.subscribe(function() {
                            // Focus icon after calendar is closed
                            Dom.get(me.id + "-icon").focus();
                        }, me, true);

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

                    Event.addListener(me.id + "-date", "keyup", me._handleFieldChange, me, true);
                    Event.addListener(me.id + "-time", "keyup", me._handleFieldChange, me, true);

                    var iconEl = Dom.get(me.id + "-icon");
                    if (iconEl) {
                        // setup keyboard enter events on the image instead of the link to get focus outline displayed
                        Alfresco.util.useAsButton(iconEl, me._showPicker, null, me);
                        Event.addListener(me.id + "-icon", "click", me._showPicker, me, true);
                    }

                    // register a validation handler for the date entry field so that the submit
                    // button disables when an invalid date is entered
                    YAHOO.Bubbling.fire("registerValidationHandler",
                            {
                                fieldId: me.id + "-date",
                                handler: Alfresco.forms.validation.validDateTime,
                                when: "keyup"
                            });

                    // register a validation handler for the time entry field (if applicable)
                    // so that the submit button disables when an invalid date is entered
                    if (me.options.showTime) {
                        YAHOO.Bubbling.fire("registerValidationHandler",
                                {
                                    fieldId: me.id + "-time",
                                    handler: Alfresco.forms.validation.validDateTime,
                                    when: "keyup"
                                });
                    }

                    // If value was set in visible fields, make sure they are validated and put in the hidden field as well
                    if (me.options.currentValue !== "") {
                        me._handleFieldChange(null);
                    }
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
                    var me = this;
                    var element = Dom.get(me.id);
                    var parent = element.parentNode;
                    var icon = Dom.get(me.id + "-icon");
                    var d = 10;                                                         // величина наложения календаря на кнопку

                    if (!Dom.hasClass(parent, "alfresco-share")) {                      // если календарь лежит не в body, нужно перенести
                        var body = Selector.query('body')[0];
                        body.appendChild(element);
                    }

                    Dom.setX(element, Dom.getX(icon) - element.offsetWidth + d);       // смещаем влево от кнопки на ширину календаря

                    var y = Dom.getY(icon) + d;                                        // смещаем немного вниз относительно кнопки
                    var height = element.offsetHeight;

                    if (y + height > Dom.getViewportHeight()) {                        // если календарь не помещается до низа окна
                        y -= height;                                                   // откроем его вверх
                    }
                    Dom.setY(element, y);

                    // show the popup calendar widget
                    me.widgets.calendar.show();
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
                    var dateEntry = selDate.toString(me._msg("form.control.date-picker.entry.date.format"));
                    Dom.get(me.id + "-date").value = dateEntry;

                    // update the time field if necessary
                    if (me.options.showTime) {
                        var time = Dom.get(me.id + "-time").value;
                        if (time.length > 0) {
                            var dateTime = Dom.get(me.id + "-date").value + " " + time;
                            var dateTimePattern = me._msg("form.control.date-picker.entry.date.format") + " " + me._msg("form.control.date-picker.entry.time.format");
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
                        YAHOO.Bubbling.fire("mandatoryControlValueUpdated", me);
                    }
                    else {
                        Dom.addClass(me.id + "-date", "invalid");

                        if (me.options.showTime) {
                            Dom.addClass(me.id + "-time", "invalid");
                        }
                    }

                    // Hide calendar if the calendar was open (Unfortunately there is no proper yui api method for me)
                    if (Dom.getStyle(me.id, "display") != "none") {
                        me.widgets.calendar.hide();
                    }
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
                            var parsedDate = Date.parseExact(changedDate, me._msg("form.control.date-picker.entry.date.format"));
                            if (parsedDate != null) {
                                if (me.options.disabled) {
                                    Dom.removeClass(me.id + "-date", "invalid");
                                } else {
                                    me.widgets.calendar.select((parsedDate.getMonth() + 1) + "/" + parsedDate.getDate() + "/" + parsedDate.getFullYear());
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
                                }
                            }
                            else {
                                Dom.addClass(me.id + "-date", "invalid");
                                if (YAHOO.env.ua.ie) {
                                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", me);
                                }
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
                        if (me.options.mandatory || YAHOO.env.ua.ie) {
                            YAHOO.Bubbling.fire("mandatoryControlValueUpdated", me);
                        }
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
                }
            };
})();
