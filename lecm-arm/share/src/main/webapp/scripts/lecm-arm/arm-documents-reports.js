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
					var container = Dom.get(this.id);

					if (container != null) {
						container.innerHTML = "";

						var sUrl = Alfresco.constants.PROXY_URI + "/lecm/reports/rptmanager/registeredReports?forCollection=true&docType=" + types;
						var callback = {
							success: function (oResponse) {
                                container.innerHTML = "";
                                var oResults = eval("(" + oResponse.responseText + ")");
                                if (oResults != null && oResults.list != null) {
									for (var i = 0; i < oResults.list.length; i++) {
										var report = oResults.list[i];

										var reportContent = "";
										reportContent += "<div><h3>";
										reportContent += '<a href="#" class="theme-color-1" style="font-weight: bold;" onClick=\'LogicECM.module.Documents.Reports.reportLinkClicked(null, {"reportCode": "' + report.code + '"});\'>' + report.name + '</a>';
										reportContent += "</h3>";
										if (report.description != null) {
											reportContent += report.description;
										}
										reportContent += "</div><br/>";

										container.innerHTML += reportContent;
									}
								}
							},
							failureMessage:"message.failure",
							scope: this
						};
						YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
					}
				}
			}
		}, true);
})();