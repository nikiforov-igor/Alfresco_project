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
	LogicECM = {};
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

	        importFromDialog: null,

	        importInfoDialog: null,

	        submitButton: null,

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady: function Toolbar_onReady() {
                // Import XML
                var importXmlButton = Alfresco.util.createYUIButton(this, "importXmlButton", this.showImportDialog,{});

	            this.submitButton = Alfresco.util.createYUIButton(this, "import-form-submit", this.onImportXML,{
		            disabled: true
	            });
                var importXmlButton = Alfresco.util.createYUIButton(this, "import-form-cancel", this.hideImportDialog,{});

                Event.on(this.id + "-import-form-import-file", "change", this.checkImportFile, null, this);

	            Event.on(this.id + "-import-error-form-show-more-link", "click", this.errorFormShowMore, null, this);

	            // Finally show the component body here to prevent UI artifacts on YUI button decoration
	            Dom.setStyle(this.id + "-body", "visibility", "visible");

	            this.importInfoDialog = Alfresco.util.createYUIPanel(this.id + "-import-info-form",
		            {
			            width: "50em"
		            });

	            this.importErrorDialog = Alfresco.util.createYUIPanel(this.id + "-import-error-form",
		            {
			            width: "60em"
		            });

	            this.importFromDialog = Alfresco.util.createYUIPanel(this.id + "-import-form",
		            {
			            width: "50em"
		            });
            },

	        showImportDialog: function() {
		        Dom.get(this.id + "-import-form-chbx-ignore").checked = false;
		        Dom.get(this.id + "-import-form-import-file").value = "";
                Dom.removeClass(this.importFromDialog.id, "hidden1");
		        this.importFromDialog.show();
	        },

	        hideImportDialog: function() {
		        this.importFromDialog.hide();
	        },

	        checkImportFile: function(event) {
		        this.submitButton.set("disabled", event.currentTarget.value == null || event.currentTarget.value.length == 0);
	        },

            /**
             * On "submit"-button click.
             */
            onImportXML: function() {
	            var me = this;
                Connect.setForm(this.id + '-import-xml-form', true);
                var url = Alfresco.constants.URL_CONTEXT + "proxy/alfresco/lecm/dictionary/post/import";
                var callback = {
	                upload: function(oResponse){
	                    var oResults = YAHOO.lang.JSON.parse(oResponse.responseText);
	                    if (oResults[0] != null && oResults[0].text != null) {
		                    Dom.get(me.id + "-import-info-form-content").innerHTML = oResults[0].text;
                            Dom.removeClass(me.importInfoDialog.id, "hidden1");
		                    me.importInfoDialog.show();
	                    } else if (oResults.exception != null) {
		  	                Dom.get(me.id + "-import-error-form-exception").innerHTML = oResults.exception.replace(/\n/g, '<br>').replace(/\r/g, '<br>');
		  	                Dom.get(me.id + "-import-error-form-stack-trace").innerHTML = me.getStackTraceString(oResults.callstack);
		                    Dom.setStyle(me.id + "-import-error-form-more", "display", "none");
                            Dom.removeClass(me.importErrorDialog.id, "hidden1");
		                    me.importErrorDialog.show();
	                    }

		                YAHOO.Bubbling.fire("datagridRefresh",
			                {
				                bubblingLabel: "dictionaries-all-datagrid"
			                });
                    }
                };
	            this.hideImportDialog();
	            Connect.asyncRequest(Alfresco.util.Ajax.POST, url, callback);
            },

	        getStackTraceString: function(callstack) {
		        var result = "";
		        if (callstack != null) {
			        for (var i = 0; i < callstack.length; i++) {
				        if (callstack[i].length > 0) {
				            result += callstack[i] + "<br/>";
				        }
			        }
		        }
		        return result;
	        },

	        errorFormShowMore: function() {
		        Dom.setStyle(this.id + "-import-error-form-more", "display", "block");
	        }
        }, true);
})();