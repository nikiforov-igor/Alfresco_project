(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.OrgStructure.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.OrgStructure.Toolbar = function (htmlId) {
        return LogicECM.module.OrgStructure.Toolbar.superclass.constructor.call(this, "LogicECM.module.OrgStructure.Toolbar", htmlId);
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.OrgStructure.Toolbar, LogicECM.module.Base.Toolbar);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.OrgStructure.Toolbar.prototype,
        {

            _initButtons: function () {
                this.toolbarButtons[this.options.newRowButtonType].push(
                    Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow,
                        {
                            disabled: this.options.newRowButtonType != 'defaultActive',
                            value: "create"
                        })
                );

                this.toolbarButtons[this.options.searchButtonsType].push(
                    Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick,
                        {
                            disabled: this.options.searchButtonsType != 'defaultActive'
                        })
                );

                this.toolbarButtons[this.options.searchButtonsType].push(
                    Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
                        {
                            disabled: this.options.searchButtonsType != 'defaultActive'
                        })
                );

                this.toolbarButtons["defaultActive"].push(
                    Alfresco.util.createYUIButton(this, "structure", this.onStructureClick)
                );
            },

            onStructureClick: function BaseToolbar_onStructureClick() {
                window.open(Alfresco.constants.PROXY_URI + "/lecm/orgstructure/diagram", "Структура организации", "top=0,left=0,height=768,width=1024");
            }
        }, true);
})();