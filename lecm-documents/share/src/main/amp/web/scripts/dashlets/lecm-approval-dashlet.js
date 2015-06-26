if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Documents = LogicECM.module.Documents || {};
LogicECM.module.Documents.Approval = LogicECM.module.Documents.Approval || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;
    var collapsedClass = "alfresco-twister-closed";
    var expandedClass = "alfresco-twister-open";

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

					if ((status == "current" && !this.stageIsFinished(stage)) ||
							(status == "finished" && this.stageIsFinished(stage)) ||
							status == "all") {
						resultsText += this.getStageView(status, stage);
					}
				}

				return resultsText;
			},

			stageIsFinished: function (stage) {
				var decision = stage.decision.value;
				var state = stage.state.value;

				return decision != "NO_DECISION" || state == "CANCELLED";
			},

			getStageView: function (status, stage) {
				var resultsText = "<div class='approval-stage-container'>";
				var stageEl = document.createElement('div');
                var id = "approval-stage_" + stage.nodeRef + "_" + Alfresco.util.generateDomId();

                stageEl.id = id;
                Dom.addClass(stageEl, "approval-stage alfresco-twister " + collapsedClass);
                YAHOO.util.Event.addListener(id, "click", function() {
                    var btnEl = this;
                    if (Dom.hasClass(btnEl, collapsedClass)) {
                        Dom.replaceClass(btnEl, collapsedClass, expandedClass);
                    } else {
                        Dom.replaceClass(btnEl, expandedClass, collapsedClass);
                    }
                });

				var text = this.msg("label.approval.dashlet.stage.title") + ": " + stage.title;
				text += ", " + this.msg("label.approval.dashlet.stage.term") + " " + stage.term;
				if (status != "current") {
					text += ", " + this.msg("label.approval.dashlet.stage.status") + " " + stage.state.displayValue;
				}

				var stageTypeEl = document.createElement('div');
				if (stage.type == "PARALLEL") {
					Dom.addClass(stageTypeEl, "stage-parallel");
					stageTypeEl.title = this.msg("label.approval.dashlet.stage.parallel");
				} else {
					Dom.addClass(stageTypeEl, "stage-sequential");
					stageTypeEl.title = this.msg("label.approval.dashlet.stage.sequential");
				}

				stageEl.innerHTML = text + stageTypeEl.outerHTML;

                resultsText += stageEl.outerHTML;
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
					resultsText += ", " + this.msg("label.approval.dashlet.item.term") + " ";
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