if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};

(function()
{
	var Dom = YAHOO.util.Dom,
		Util = LogicECM.module.Base.Util;

	LogicECM.module.Calendar.MembersView = function (fieldHtmlId) {
		LogicECM.module.Calendar.MembersView.superclass.constructor.call(this, "LogicECM.module.Calendar.MembersView", fieldHtmlId, [ "container", "datasource"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.MembersView, Alfresco.component.Base,
		{
			options: {
				currentValue: null,
				disabled: null,
				mode: null,
				eventNodeRef: null
			},

			onReady: function(){
				this.loadTableData();
			},

			loadTableData: function() {
				if (this.options.eventNodeRef != null && this.options.eventNodeRef.length > 0) {
					var sUrl = sUrl = Alfresco.constants.PROXY_URI + "/lecm/events/getMembers?eventNodeRef=" + encodeURIComponent(this.options.eventNodeRef);
					Alfresco.util.Ajax.jsonGet(
						{
							url: sUrl,
							successCallback: {
								fn: function (response) {
									if (response.json != null && response.json.members != null) {
										var members = response.json.members;

										var el = Dom.get(this.id + "-cntrl-currentValueDisplay");
										for (var i = 0; i < members.length; i++) {
											el.innerHTML += Util.getCroppedItem(Util.getControlEmployeeView(members[i].nodeRef, members[i].name));
										}
									}
								},
								scope: this
							},
							failureMessage: "message.failure"
						});
				}
			}
		});
})();