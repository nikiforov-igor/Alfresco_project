/* global Alfresco */

/**
 * @module logic_ecm/im/MessengerPopup
 * @extends module:alfresco/menus/AlfMenuBarPopup
 * @param declare
 * @param lang
 * @param array
 * @param dom
 * @param domClass
 * @param domStyle
 * @param domConstruct
 * @param on
 * @param xhr
 * @param json
 * @param AlfMenuBarPopup
 * @author LogicECM
 */
define(['dojo/_base/declare',
        'dojo/_base/lang',
        'dojo/_base/array',
        'dojo/dom',
        'dojo/dom-class',
        'dojo/dom-style',
        'dojo/dom-construct',
        'dojo/on',
        'dojo/request/xhr',
        'dojo/json',
        'alfresco/menus/AlfMenuBarItem'
    ],
    function (declare, lang, array, dom, domClass, domStyle, domConstruct, on, xhr, json, AlfMenuBarItem) {

        return declare([AlfMenuBarItem], {
            cssRequirements: [{cssFile: './css/counter-styles.css'}],

            location: {href: "#"},

            buttonDomElement: null,
            originalBackgroundImage: null,

            highLighterIntervalID: null,

            counterBublingHandler: function (context) {
                return function (layer, args) {
                    var count = args[1].count;
                    var elem = dom.byId('msgCounter');

                    elem.innerHTML = (count > 99) ? '∞' : count;
                    domStyle.set('msgCounter', 'display', count > 0 ? 'inline-block' : 'none');
                    domClass.toggle('msgCounter', 'blink', count > 0);
                };
            },


            postCreate: function () {
                this.inherited(arguments);
                if (this.domNode) {
                    this.onClick = lang.hitch(this, 'showPopup');
                }

                Alfresco.logger.info("A new LogicECM.module.LecmIM.Messenger has been created");

                domConstruct.place('<span id="msgCounter" class="counter notifications-counter"></span>', this.domNode);
                YAHOO.Bubbling.on("ru.it.lecm.im.update-messages-count", this.counterBublingHandler(this));
            },

            // Создание обработчика нажатия на кнопку
            showPopup: function () {
                if (window.iJab) {
                    window.iJab.toggleIsVisible();
                } else {
                    alert("Messenger not found!");
                }
            }
        });
    }
);
