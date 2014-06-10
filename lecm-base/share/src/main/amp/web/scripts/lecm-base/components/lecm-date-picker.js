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
     * DatePicker constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @param {String} currentValueHtmlId The HTML id of the parent element
     * @return {LogicECM.DatePicker} The new DatePicker instance
     * @constructor
     */
    LogicECM.DatePicker = function (htmlId, currentValueHtmlId) {
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
            this.options = YAHOO.lang.merge(this.options, obj);
            return this;
        },

        /**
         * Set messages for this component.
         *
         * @method setMessages
         * @param obj {object} Object literal specifying a set of messages
         * @return {LogicECM.DatePicker} returns 'this' for method chaining
         */
        setMessages: function DatePicker_setMessages(obj) {
            Alfresco.util.addMessages(obj, this.name);
            return this;
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
        draw: function () {
            var theDate = null;

            // calculate current date
            if (this.options.currentValue !== null && this.options.currentValue !== "") {
                theDate = Alfresco.util.fromISO8601(this.options.currentValue);
            }
            else {
                theDate = new Date();
            }

            var page = (theDate.getMonth() + 1) + "/" + theDate.getFullYear();
            var selected = (theDate.getMonth() + 1) + "/" + theDate.getDate() + "/" + theDate.getFullYear();
            var dateEntry = theDate.toString(this._msg("form.control.date-picker.entry.date.format"));
            var timeEntry = theDate.toString(this._msg("form.control.date-picker.entry.time.format"));

            // populate the input fields
            if (this.options.currentValue !== "") {
                // show the formatted date
                Dom.get(this.id + "-date").value = dateEntry;

                if (this.options.showTime) {
                    Dom.get(this.id + "-time").value = timeEntry;
                }
            }

            // construct the picker
            if (!this.options.disabled) {
                this.widgets.calendar = new YAHOO.widget.Calendar(this.id, this.id, { title: this._msg("form.control.date-picker.choose"), close: true, navigator: true });
                this.widgets.calendar.cfg.setProperty("pagedate", page);
                this.widgets.calendar.cfg.setProperty("selected", selected);

                if (this.options.minLimit && this.options.minLimit != "") {
                    var minDate = Alfresco.util.fromISO8601(this.options.minLimit);
                    if (minDate) {
                        this.widgets.calendar.cfg.setProperty("mindate", Alfresco.util.formatDate(minDate, "mm/dd/yyyy"));
                    }
                }

                if (this.options.maxLimit && this.options.maxLimit != "") {
                    var maxDate = Alfresco.util.fromISO8601(this.options.maxLimit);
                    if (maxDate) {
                        this.widgets.calendar.cfg.setProperty("maxdate", Alfresco.util.formatDate(maxDate, "mm/dd/yyyy"));
                    }
                }

                Alfresco.util.calI18nParams(this.widgets.calendar);

                // setup events
                this.widgets.calendar.selectEvent.subscribe(this._handlePickerChange, this, true);
                this.widgets.calendar.hideEvent.subscribe(function () {
                    // Focus icon after calendar is closed
                    Dom.get(this.id + "-icon").focus();
                }, this, true);

                // render the calendar control
                this.widgets.calendar.render();
            }

            Event.addListener(this.id + "-date", "keyup", this._handleFieldChange, this, true);
            Event.addListener(this.id + "-time", "keyup", this._handleFieldChange, this, true);

            var iconEl = Dom.get(this.id + "-icon");
            if (iconEl) {
                // setup keyboard enter events on the image instead of the link to get focus outline displayed
                Alfresco.util.useAsButton(iconEl, this._showPicker, null, this);
                Event.addListener(this.id + "-icon", "click", this._showPicker, this, true);
            }


            // register a validation handler for the date entry field so that the submit
            // button disables when an invalid date is entered
            YAHOO.Bubbling.fire("registerValidationHandler",
                {
                    fieldId: this.id + "-date",
                    handler: Alfresco.forms.validation.validDateTime,
                    when: "keyup"
                });

            // register a validation handler for the time entry field (if applicable)
            // so that the submit button disables when an invalid date is entered
            if (this.options.showTime) {
                YAHOO.Bubbling.fire("registerValidationHandler",
                    {
                        fieldId: this.id + "-time",
                        handler: Alfresco.forms.validation.validDateTime,
                        when: "keyup"
                    });
            }

            // If value was set in visible fields, make sure they are validated and put in the hidden field as well
            if (this.options.currentValue !== "") {
                this._handleFieldChange(null);
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
            var element = Dom.get(this.id);
            var parent = element.parentNode;
            var icon = Dom.get(this.id + "-icon");
            var d = 10;                                                         // величина наложения календаря на кнопку

            if (!Dom.hasClass(parent, "alfresco-share")) {                      // если календарь лежит не в body, нужно перенести
                var body = YAHOO.util.Selector.query('body')[0];
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
            this.widgets.calendar.show();
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
            var selected = args[0];
            var selDate = this.widgets.calendar.toDate(selected[0]);
            var dateEntry = selDate.toString(this._msg("form.control.date-picker.entry.date.format"));
            Dom.get(this.id + "-date").value = dateEntry;

            // update the time field if necessary
            if (this.options.showTime) {
                var time = Dom.get(this.id + "-time").value;
                if (time.length > 0) {
                    var dateTime = Dom.get(this.id + "-date").value + " " + time;
                    var dateTimePattern = this._msg("form.control.date-picker.entry.date.format") + " " + this._msg("form.control.date-picker.entry.time.format");
                    selDate = Date.parseExact(dateTime, dateTimePattern);
                }
            } else {
	            selDate.setHours(12);
	            selDate.setMinutes(0);
	            selDate.setSeconds(0);
            }

            // if we have a valid date, convert to ISO format and set value on hidden field
            if (selDate != null) {
                Dom.removeClass(this.id + "-date", "invalid");
                if (this.options.showTime) {
                    Dom.removeClass(this.id + "-time", "invalid");
                }
                var isoValue = Alfresco.util.toISO8601(selDate, {"milliseconds": true});
                Dom.get(this.currentValueHtmlId).value = isoValue;

                if (Alfresco.logger.isDebugEnabled())
                    Alfresco.logger.debug("Hidden field '" + this.currentValueHtmlId + "' updated to '" + isoValue + "'");

                // always inform the forms runtime that the control value has been updated
                YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
            }
            else {
                Dom.addClass(this.id + "-date", "invalid");

                if (this.options.showTime) {
                    Dom.addClass(this.id + "-time", "invalid");
                }
            }

            // Hide calendar if the calendar was open (Unfortunately there is no proper yui api method for this)
            if (Dom.getStyle(this.id, "display") != "none") {
                this.widgets.calendar.hide();
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
            var changedDate = Dom.get(this.id + "-date").value;
            if (changedDate.length > 0) {
                // Only set for actual value changes so tab or shift events doesn't remove the "text selection" of the input field
                if (event == undefined || (event.keyCode != KeyListener.KEY.TAB && event.keyCode != KeyListener.KEY.SHIFT)) {
                    // convert to format expected by YUI
                    var parsedDate = Date.parseExact(changedDate, this._msg("form.control.date-picker.entry.date.format"));
                    if (parsedDate != null) {
                        if (this.options.disabled) {
                            Dom.removeClass(this.id + "-date", "invalid");
                        } else {
                            this.widgets.calendar.select((parsedDate.getMonth() + 1) + "/" + parsedDate.getDate() + "/" + parsedDate.getFullYear());
                            var selectedDates = this.widgets.calendar.getSelectedDates();
                            if (selectedDates.length > 0) {
                                Dom.removeClass(this.id + "-date", "invalid");
                                var firstDate = selectedDates[0];
                                this.widgets.calendar.cfg.setProperty("pagedate", (firstDate.getMonth() + 1) + "/" + firstDate.getFullYear());
                                this.widgets.calendar.render();

                                // NOTE: we don't need to check the time value in here as the _handlePickerChange
                                //       function gets called as well as a result of rendering the picker above,
                                //       that's also why we don't update the hidden field in here either.
                            }
                        }
                    }
                    else {
                        Dom.addClass(this.id + "-date", "invalid");
                        if (YAHOO.env.ua.ie) {
                            YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                        }
                    }
                }
            }
            else {
                // when the date is completely cleared remove the hidden field and remove the invalid class
                Dom.removeClass(this.id + "-date", "invalid");
                Dom.get(this.currentValueHtmlId).value = "";

                if (Alfresco.logger.isDebugEnabled())
                    Alfresco.logger.debug("Hidden field '" + this.currentValueHtmlId + "' has been reset");

                // inform the forms runtime that the control value has been updated
                if (this.options.mandatory || YAHOO.env.ua.ie) {
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
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
            return Alfresco.util.message.call(this, messageId, "LogicECM.DatePicker", Array.prototype.slice.call(arguments).slice(1));
        }
    };
})();
