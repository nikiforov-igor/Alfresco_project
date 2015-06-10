if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Documents = LogicECM.module.Documents || {};
LogicECM.module.Documents.Approval = LogicECM.module.Documents.Approval || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;
    var collapsedClass = "collapsed";
    var expandedClass = "expanded";

	LogicECM.module.Documents.Approval.Dashlet = function (htmlId) {
		LogicECM.module.Documents.Approval.Dashlet.superclass.constructor.call(this, "LogicECM.module.Documents.Approval.Dashlet", htmlId, ["button", "container"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.Documents.Approval.Dashlet, Alfresco.component.Base,
		{
			options: {
				approvalInfo: null
			},

			onReady: function () {
				Event.on(this.id + "-approval-status", "change", this.onChangeApprovalStatus, this, true);
				this.onChangeApprovalStatus();
			},

			onChangeApprovalStatus: function() {
				var status = Dom.get(this.id + "-approval-status").value;

				var resultsBlock = Dom.get(this.id + "_results");
				if (resultsBlock != null && this.options.approvalInfo != null && this.options.approvalInfo.stages != null) {
					resultsBlock.innerHTML = this.getResultsView(status, this.options.approvalInfo.stages);
				}
			},

			getResultsView: function(status, stages) {
				var resultsText = "";

				for (var i = 0; i < stages.length; i++) {
					var stage = stages[i];
					var decision = stage.decision.value;

					if ((status == "current" && decision == "NO_DECISION") ||
							(status == "finished" && decision != "NO_DECISION") ||
							status == "all") {
						resultsText += this.getStageView(status, stage);
					}
				}

				return resultsText;
			},

			getStageView: function (status, stage) {
				var resultsText = "";
                var btnEl = document.createElement('div');
                var id = "approval-stage-btn_" + stage.nodeRef + "_" + Alfresco.util.generateDomId();

                btnEl.id = id;
                Dom.addClass(btnEl, "approval-stage-btn " + collapsedClass);
                YAHOO.util.Event.addListener(id, "click", function() {
                    var btnEl = this;
                    if (Dom.hasClass(btnEl, collapsedClass)) {
                        Dom.replaceClass(btnEl, collapsedClass, expandedClass);
                    } else {
                        Dom.replaceClass(btnEl, expandedClass, collapsedClass);
                    }
                });

				resultsText += "<div class='approval-stage-container'>";
                resultsText += btnEl.outerHTML;
				resultsText += "<div class='approval-stage ";

                if (stage.type == "PARALLEL") {
                    resultsText += "stage-parallel";
                } else {
                    resultsText += "stage-sequential";
                }

                resultsText += "'>";
				resultsText += this.msg("label.approval.dashlet.stage.title") + ": ";
				resultsText += stage.title;
				resultsText += ", " + this.msg("label.approval.dashlet.stage.term") + " ";
				resultsText += stage.term;
				if (status != "current") {
					resultsText += ", " + this.msg("label.approval.dashlet.stage.status") + " ";
					resultsText += stage.state.displayValue;
				}
				resultsText += "</div>";

				resultsText += this.getItemsView(stage);
                resultsText += "</div>";

                return resultsText;
			},

			getItemsView: function(stage) {
				var resultsText = "";
				resultsText += '<div id="approval-stage-items-' + stage.nodeRef.replace(/:|\//g, '_') + '" class="stages-items">';
				for (var i = 0; i < stage.items.length; i++) {
					var item = stage.items[i];

					resultsText += "<div>";
					resultsText += item.employee;
					resultsText += ", " + this.msg("label.approval.dashlet.stage.term") + " ";
					resultsText += item.dueDate;
					resultsText += ", ";
					if (item.decision.value === 'NO_DECISION') {
						resultsText += item.state.displayValue;
					} else {
						resultsText += item.decision.displayValue;
					}
					resultsText += "</div>";
				}

				resultsText += "</div>";
				return resultsText;
			}
		});
})();