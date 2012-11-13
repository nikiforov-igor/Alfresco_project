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
 * Data Lists: Toolbar component.
 *
 * @namespace Alfresco
 * @class Alfresco.component.AllDictToolbar
 */
(function() {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Connect = YAHOO.util.Connect;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {Alfresco.component.AllDictToolbar} The new Toolbar instance
     * @constructor
     */
    Alfresco.component.AllDictToolbar = function(htmlId) {
        Alfresco.component.AllDictToolbar.superclass.constructor.call(this, "Alfresco.component.AllDictToolbar", htmlId, ["button", "container"]);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(Alfresco.component.AllDictToolbar, Alfresco.component.Base);

    /**
     * Augment prototype with Common Actions module
     */
    YAHOO.lang.augmentProto(Alfresco.component.AllDictToolbar, LogicECM.module.Base.DataActions);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(Alfresco.component.AllDictToolbar.prototype,
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
                 * Current siteId.
                 *
                 * @property siteId
                 * @type string
                 * @default ""
                 */
                siteId: ""
            },

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady: function AllDictToolbar_onReady() {
                var panel = new YAHOO.widget.Panel("import-xml-panel", {
                    visible: false,
                    fixedcenter: true,
                    constraintoviewport: true,
                    underlay: "none"
                });
                panel.render();
                Event.on("show-import-xml", "click", panel.show, null, panel);
                Event.on("import-xml-submit", "click", this.onSubmit);
            },
            /**
             * On "submit"-button click.
             */
            onSubmit: function() {
                Dom.setStyle('import-xml-panel', 'visibility', 'hidden');
                Connect.setForm('import-xml-form', true);
                var url = Alfresco.constants.URL_CONTEXT + "proxy/alfresco/lecm/dictionary/post/import";
                var fileUploadCallback = {
                    upload:function(o){
                        console.log('Upload successfully' + "\n" + 'Server Response: ' + o.responseText);
                        document.location.reload(true);
                    }
                };
                Connect.asyncRequest(Alfresco.util.Ajax.GET, url, fileUploadCallback);
            }
        }, true);
})();