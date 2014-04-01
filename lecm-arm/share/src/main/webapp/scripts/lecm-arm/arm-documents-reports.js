if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
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
				var types = args[1].types;
				if (types !== null) {
					var container = Dom.get(this.id + "-data");

					if (container != null) {
						container.innerHTML = "";
						var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/registeredReports?forCollection=true&docType=" + types;
						var callback = {
							success: function (oResponse) {
                                container.innerHTML = "";
                                var oResults = eval("(" + oResponse.responseText + ")");
                                if (oResults != null && oResults.list != null) {
	                                var resultLength = oResults.list.length;

	                                Dom.setStyle(this.id + "-data", "display", resultLength > 0 ? "" : "none");
	                                Dom.setStyle(this.id + "-empty", "display", resultLength == 0 ? "" : "none");

									for (var i = 0; i < oResults.list.length; i++) {
										container.innerHTML += this.getReportTr(oResults.list[i], i, oResults.list.length);
									}
								}
							},
							failureMessage:"message.failure",
							scope: this
						};
						YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
					}
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

				var result = "<tr class=\"" + trClasses + "\">";
				result += "<td>";
				result += "<div class=\"yui-dt-liner\">";

				result += '<a href="#" onClick=\'LogicECM.module.Documents.Reports.reportLinkClicked(null, {"reportCode": "' + report.code + '"});\'>' + report.name + '</a>';

				result += "</div>";
				result += "</td>";
				result += "</tr>";
				return result;
			}
		}, true);
})();