(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

    LogicECM.DateDisplayControl = function(htmlId)
   {
      // Mandatory properties
      this.name = "LogicECM.DateDisplayControl";
      this.id = htmlId;

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button"], this.onComponentsLoaded, this);

      return this;
   };

    LogicECM.DateDisplayControl.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
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
          * String date format
          *
          * @property formatDateStr
          * @type string
          * @default null
          */
		 formatDateStr: null
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DateDisplayControl} returns 'this' for method chaining
       */
      setOptions: function DateDisplayControl_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {LogicECM.DateDisplayControl} returns 'this' for method chaining
       */
      setMessages: function DateDisplayControl_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DateDisplayControl_onComponentsLoaded()
      {
         Event.onContentReady(this.id + "-date", this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DateDisplayControl_onReady()
      {
         // calculate current date
         if (this.options.currentValue == null || this.options.currentValue == "") {
             return;
         }

		 var theDate = Alfresco.util.fromISO8601(this.options.currentValue);

		 if (this.options.formatDateStr){
			var dateEntry = Alfresco.util.formatDate(theDate,this._msg(this.options.formatDateStr));
		 }else{
			var dateEntry = theDate.toString(this._msg("form.control.date-picker.entry.date.format"));
		 }
         var timeEntry = theDate.toString(this._msg("form.control.date-picker.entry.time.format"));

         // populate the input fields
          if (this.options.currentValue !== "") {
              Dom.get(this.id + "-date").innerHTML = dateEntry + (this.options.showTime ? " " + timeEntry : "");
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
      _msg: function DateDisplayControl__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "LogicECM.DateDisplayControl", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
