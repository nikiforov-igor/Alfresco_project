if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.ARM.DocumentsReports = function (htmlId) {
		LogicECM.module.ARM.DocumentsReports.superclass.constructor.call(this, "LogicECM.module.ARM.DocumentsReports", htmlId);

		YAHOO.Bubbling.on("updateArmReports", this.onUpdateArmReports, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.DocumentsReports, Alfresco.component.Base,
		{
			onUpdateArmReports: function(layer, args) {
				var types = args[1].types ? args[1].types : "";
				var reportCodes = args[1].reportCodes ? args[1].reportCodes : "";
				var container = Dom.get(this.id + "-data");
				if (container) {
					container.innerHTML = "";
					var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/registeredReports?forCollection=true&docType=" + types + "&reportCodes=" + reportCodes;
					Alfresco.util.Ajax.jsonGet({
					url: sUrl,
					successCallback: {
						fn: function (response) {
                                container.innerHTML = "";
                                if (response.json && response.json.list) {
	                                var resultLength = response.json.list.length;
	                                Dom.setStyle(this.id + "-data", "display", resultLength > 0 ? "" : "none");
	                                Dom.setStyle(this.id + "-empty", "display", resultLength == 0 ? "" : "none");
									for (var i = 0; i < response.json.list.length; i++) {
										container.appendChild(this.getReportTr(response.json.list[i], i, response.json.list.length));
									}
								}
							},
							scope: this
						},
						failureMessage: this.msg("message.failure")
					});
				}
			},

			getReportTr: function(report, index, allCount) {
				var trClasses = "yui-dt-rec";
				if (index == 0) {
					trClasses += " yui-dt-first";
				}
				if (index == allCount - 1) {
					trClasses += " yui-dt-last";
				}
				if (index % 2 == 0) {
					trClasses += " yui-dt-even";
				} else {
					trClasses += " yui-dt-odd";
				}

				var tr = document.createElement("tr");
				tr.className = trClasses;
				var td = document.createElement("td");
				var div = document.createElement("div");
				div.className = "yui-dt-liner";

				div.innerHTML = '<a href="#" onClick=\'LogicECM.module.Documents.Reports.reportLinkClicked(null, {"reportCode": "' + report.code + '"});\'>' + report.name + '</a>';

				tr.appendChild(td);
                td.appendChild(div);
				return tr;
			}
		}, true);
})();