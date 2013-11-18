/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.FormsEditor = LogicECM.module.FormsEditor || {};

(function () {
	LogicECM.module.FormsEditor.AttributesTable = function (htmlId) {
		return LogicECM.module.FormsEditor.AttributesTable.superclass.constructor.call(this, "LogicECM.module.FormsEditor.AttributesTable", htmlId);
	};

	YAHOO.extend(LogicECM.module.FormsEditor.AttributesTable, Alfresco.component.Base);


	YAHOO.lang.augmentObject(LogicECM.module.FormsEditor.AttributesTable.prototype,
		{
			options: {
				bubblingLabel: null,
				itemNodeRef: null
			},

			onReady: function BaseToolbar_onReady() {
				this.showDatagrid();
			},

			showDatagrid: function() {
				if (this.options.itemNodeRef != null && this.options.bubblingLabel != null) {
					YAHOO.Bubbling.fire("activeGridChanged",
						{
							datagridMeta:{
								itemType: "lecm-forms-editor:attr",
								nodeRef: this.options.itemNodeRef,
								actionsConfig:{
									fullDelete:true
								}
							},
							bubblingLabel: this.options.bubblingLabel
						});
				}
			}
		}, true);
})();