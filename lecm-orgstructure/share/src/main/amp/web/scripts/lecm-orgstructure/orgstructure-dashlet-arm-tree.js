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
 * @namespace LogicECM.module.OrgStructure
 * @class LogicECM.module.OrgStructure.DashletArmTree
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;
    var Event = YAHOO.util.Event;

    LogicECM.module.OrgStructure.DashletArmTree = function (htmlId) {
        LogicECM.module.OrgStructure.DashletArmTree.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.OrgStructure.DashletArmTree, LogicECM.module.OrgStructure.ArmTree);

    YAHOO.lang.augmentObject(LogicECM.module.OrgStructure.ArmTree.prototype,
        {
        }, true);
})();