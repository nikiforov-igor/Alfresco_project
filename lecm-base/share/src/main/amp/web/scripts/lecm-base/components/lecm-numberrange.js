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
 * NumberRange component.
 *
 * @namespace LogicECM
 * @class LogicECM.NumberRange
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * NumberRange constructor.
    *
    * @param {String} htmlId The HTML id of the control element
    * @param {String} valueHtmlId The HTML id prefix of the value elements
    * @return {LogicECM.NumberRange} The new NumberRange instance
    * @constructor
    */
   LogicECM.NumberRange = function (htmlId, valueHtmlId) {
       LogicECM.NumberRange.superclass.constructor.call(this, "LogicECM.NumberRange", htmlId);

       this.valueHtmlId = valueHtmlId;
       this.currentMinNumber = "";
       this.currentMaxNumber = "";

       YAHOO.Bubbling.on("reInitializeControl", this.onReInitializeControl, this);
       return this;
   };

   YAHOO.extend(LogicECM.NumberRange, Alfresco.component.Base,
   {
      /**
       * Current minimum number value
       *
       * @property currentMinNumber
       * @type string
       */
      currentMinNumber: "",

      /**
       * Current maximum number value
       *
       * @property currentMaxNumber
       * @type string
       */
      currentMaxNumber: "",

       options: {
           onlyPositive: false,
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
       onReady: function NumberRange_onReady() {
           // Add listener for input fields to keep the generated range value up-to-date
           Event.addListener(this.id + "-min", "keyup", this._handleFieldChange, this, true);
           YAHOO.Bubbling.fire("registerValidationHandler",
               {
                   fieldId: this.id + "-min",
                   handler: this._changeNumber,
                   when: "keyup"
               });

           Event.addListener(this.id + "-max", "keyup", this._handleFieldChange, this, true);
           YAHOO.Bubbling.fire("registerValidationHandler",
               {
                   fieldId: this.id + "-max",
                   handler: this._changeNumber,
                   when: "keyup"
               });
           this.init();
           LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
       },

       init: function () {
           if (this.options.defaultValue) {
               Dom.get(this.valueHtmlId).value = this.options.defaultValue;
           }
           if (Dom.get(this.valueHtmlId).value) {
               var fullNumbers = Dom.get(this.valueHtmlId).value.split("|");
               if (fullNumbers[0]) {
                   Dom.get(this.id + "-min").value = fullNumbers[0];
                   this.currentMinNumber = fullNumbers[0];
               } else {
                   Dom.get(this.id + "-min").value = "";
                   this.currentMinNumber = "";
               }
               if (fullNumbers[1]) {
                   Dom.get(this.id + "-max").value = fullNumbers[1];
                   this.currentMaxNumber = fullNumbers[1];
               } else {
                   Dom.get(this.id + "-max").value = "";
                   this.currentMaxNumber = "";
               }
           }
       },

      /**
       * Updates the currently stored range value in the hidden form field.
       *
       * @method _updateCurrentValue
       * @private
       */
      _updateCurrentValue: function NumberRange__updateCurrentValue() {
          var value = this.currentMinNumber + "|" + this.currentMaxNumber;
          value = value == "|" ? "" : value;
          Dom.get(this.valueHtmlId).value = value;
      },

      /**
       * Handles the value being changed in either input field.
       *
       * @method _handleFieldChange
       * @param event The event that occurred
       * @private
       */
      _handleFieldChange: function NumberRange__handleFieldChange(event) {
          var strMinValue = YAHOO.lang.trim(Dom.get(this.id + "-min").value),
              strMaxValue = YAHOO.lang.trim(Dom.get(this.id + "-max").value);

          /*MIN*/
          this.currentMinNumber = '';

          if (this._isNumber(strMinValue, this.options.onlyPositive) || strMinValue.length == 0) {
              Dom.removeClass(this.id + "-min", "invalid");
              this.currentMinNumber = strMinValue;
          } else {
              if (strMinValue.length > 0) {
                  Dom.addClass(this.id + "-min", "invalid");
              }
          }

          /*MAX*/
          this.currentMaxNumber = '';

          if (this._isNumber(strMaxValue, this.options.onlyPositive) || strMaxValue.length == 0) {
              Dom.removeClass(this.id + "-max", "invalid");
              this.currentMaxNumber = strMaxValue;
          } else {
              if (strMaxValue.length > 0) {
                  Dom.addClass(this.id + "-max", "invalid");
              }
          }

          if (this.currentMinNumber != '' && this.currentMaxNumber != '') {
              if (parseFloat(this.currentMinNumber) > parseFloat(this.currentMaxNumber)) {
                  Dom.addClass(this.id + "-min", "invalid");
                  Dom.addClass(this.id + "-max", "invalid");
              } else {
                  Dom.removeClass(this.id + "-min", "invalid");
                  Dom.removeClass(this.id + "-max", "invalid");
              }
          }

          this._updateCurrentValue();
          YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
      },

       _changeNumber: function(field) {
           return !YAHOO.util.Dom.hasClass(field, "invalid");
       },

       _isNumber: function (value, onlyPositiveNumber) {
           var numberExp = /[-]?\d+(\.\d+|,\d+)?/ig;

           var valid = !isNaN(parseFloat(value)) && isFinite(value);
           if (valid) {
               var test = value.match(numberExp);
               valid = (test != null && test[0] == value);
           }
           if (valid && onlyPositiveNumber) {
               valid = !(value < 0);
           }
           return valid;
       },

       onReInitializeControl: function (layer, args) {
           if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
               var options = args[1].options;
               if (options != null) {
                   this.setOptions(options);
               }
               this.currentMinNumber = "";
               this.currentMaxNumber = "";

               if (options.resetValue) {
                   Dom.get(this.valueHtmlId).value = "|";
               }
               this.init();
           }
       }
   });
})();