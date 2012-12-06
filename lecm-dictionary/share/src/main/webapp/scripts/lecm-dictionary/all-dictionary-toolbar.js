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
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Dictionary.Dictionary
 */
LogicECM.module.AllDictionary = LogicECM.module.AllDictionary || {};

/**
 * Data Lists: Toolbar component.
 *
 * @namespace Alfresco
 * @class LogicECM.module.AllDictionary.Toolbar
 */
(function() {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        UA = YAHOO.util.UserAction,
        Connect = YAHOO.util.Connect;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {Alfresco.component.AllDictToolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.AllDictionary.Toolbar = function(htmlId) {
	    LogicECM.module.AllDictionary.Toolbar.superclass.constructor.call(this, "LogicECM.module.AllDictionary.Toolbar", htmlId, ["button", "container"]);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.AllDictionary.Toolbar, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.AllDictionary.Toolbar.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {},

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady: function Toolbar_onReady() {
                // Import XML
                var importXmlButton = Alfresco.util.createYUIButton(this, "importXmlButton", function(){},{});
                var inputId = this.id + "-import-xml-input";

                Event.on(inputId, "mouseenter", function() {
                    UA.mouseover(importXmlButton);
                });
                Event.on(inputId, "mouseleave", function() {
                    UA.mouseout(importXmlButton);
                });
                Event.on(inputId, "change", this.onImportXML, null, this);

	            // Finally show the component body here to prevent UI artifacts on YUI button decoration
	            Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            /**
             * On "submit"-button click.
             */
            onImportXML: function() {
                Connect.setForm(this.id + '-import-xml-form', true);
                var url = Alfresco.constants.URL_CONTEXT + "proxy/alfresco/lecm/dictionary/post/import";
                var fileUploadCallback = {
                    upload:function(o){
                        console.log('Server Response: ' + o.responseText);
                        document.location.reload(true);
                    }
                };
                Connect.asyncRequest(Alfresco.util.Ajax.GET, url, fileUploadCallback);
            }
        }, true);
})();