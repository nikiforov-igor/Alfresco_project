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
 * LogicECM Orgstructure module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.OrgStructure.OrgStructure
 */
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

/**
 * OrgStructure module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.OrgStructure
 */
(function () {

    LogicECM.module.OrgStructure.Menu = function (htmlId) {
        return LogicECM.module.OrgStructure.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.OrgStructure.Menu",
            htmlId,
            ["button"]);
    };

    YAHOO.extend(LogicECM.module.OrgStructure.Menu, Alfresco.component.Base, {
        onReady: function () {
            var onButtonClick1 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "org-employees";
            };
            this.widgets.employeesButton = Alfresco.util.createYUIButton(this, "employeesBtn", onButtonClick1, {});

            var onButtonClick2 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "org-staff-list";
            };
            this.widgets.staffButton = Alfresco.util.createYUIButton(this, "staffBtn", onButtonClick2, {});

            var onButtonClick3 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "org-structure";
            };
            this.widgets.orgstructureButton = Alfresco.util.createYUIButton(this, "orgstructureBtn", onButtonClick3, {});

            var onButtonClick4 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "org-work-groups";
            };
            this.widgets.workGroupButton = Alfresco.util.createYUIButton(this, "workGroupBtn", onButtonClick4, {});

            var onButtonClick5 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "org-positions";
            };
            this.widgets.positionsButton = Alfresco.util.createYUIButton(this, "positionsBtn", onButtonClick5, {});

            var onButtonClick6 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "org-roles";
            };
            this.widgets.rolesButton = Alfresco.util.createYUIButton(this, "rolesBtn", onButtonClick6, {});

            var onButtonClick7 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "org-profile";
            };
            this.widgets.organizationButton = Alfresco.util.createYUIButton(this, "organizationBtn", onButtonClick7, {});

            var onButtonClick8 = function (e) {
                window.location.href = window.location.protocol + "//" + window.location.host +
                    Alfresco.constants.URL_PAGECONTEXT + "org-business-roles";
            };
            this.widgets.businessRolesButton = Alfresco.util.createYUIButton(this, "businessRolesBtn", onButtonClick8, {});
        }
    });
})();
