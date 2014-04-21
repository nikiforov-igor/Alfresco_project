if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}


(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	var $html = Alfresco.util.encodeHTML,
		$siteURL = Alfresco.util.siteURL;

	LogicECM.TaskDistribution = function (htmlId) {
		LogicECM.TaskDistribution.superclass.constructor.call(this, "LogicECM.TaskDistribution", htmlId, ["button", "menu", "container", "datasource", "datatable", "paginator", "json", "history"]);

		return this;
	};

	YAHOO.extend(LogicECM.TaskDistribution, Alfresco.component.Base);

	YAHOO.lang.augmentProto(LogicECM.TaskDistribution, Alfresco.action.WorkflowActions);

	YAHOO.lang.augmentObject(LogicECM.TaskDistribution.prototype,
		{
			options: {
				maxItems: 50
			},

			onReady: function () {
				var url = Alfresco.constants.PROXY_URI + "api/task-instances?authority=" + encodeURIComponent(Alfresco.constants.USERNAME) +
					"&properties=" + ["bpm_priority", "bpm_status", "bpm_dueDate", "bpm_description", "bpm_package"].join(",");
				this.widgets.pagingDataTable = new Alfresco.util.DataTable(
					{
						dataTable: {
							container: this.id + "-tasks",
							columnDefinitions: [
								{ key: "id", sortable: false, formatter: this.bind(this.renderCellIcons), width: 40 },
								{ key: "title", sortable: false, formatter: this.bind(this.renderCellTaskInfo) },
								{ key: "name", sortable: false, formatter: this.bind(this.renderCellActions), width: 200 }
							],
							config: {
								MSG_EMPTY: this.msg("message.noTasks")
							}
						},
						dataSource: {
							url: url,
							defaultFilter: {
								filterId: "assignee",
								filterData: "me"
							}
						},
						paginator: {
							config: {
								containers: [this.id + "-paginator"],
								rowsPerPage: this.options.maxItems
							}
						}
					});
			},

			renderCellIcons: function TL_renderCellIcons(elCell, oRecord, oColumn, oData) {
				var priority = oRecord.getData("properties")["bpm_priority"],
					priorityMap = { "1": "high", "2": "medium", "3": "low" },
					priorityKey = priorityMap[priority + ""],
					pooledTask = oRecord.getData("isPooled");
				var desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/priority-' + priorityKey + '-16.png" title="' + this.msg("label.priority", this.msg("priority." + priorityKey)) + '"/>';
				if (pooledTask) {
					desc += '<br/><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/pooled-task-16.png" title="' + this.msg("label.pooledTask") + '"/>';
				}
				elCell.innerHTML = desc;
			},

			renderCellTaskInfo: function TL_renderCellTaskInfo(elCell, oRecord, oColumn, oData) {
				var taskId = oRecord.getData("id"),
					message = $html(oRecord.getData("properties")["bpm_description"]),
					dueDateStr = oRecord.getData("properties")["bpm_dueDate"],
					dueDate = dueDateStr ? Alfresco.util.fromISO8601(dueDateStr) : null,
					type = $html(oRecord.getData("title")),
					status = $html(oRecord.getData("properties")["bpm_status"]),
					assignee = oRecord.getData("owner");

				// if there is a property label available for the status use that instead
				var data = oRecord.getData();
				if (data.propertyLabels && Alfresco.util.isValueSet(data.propertyLabels["bpm_status"], false)) {
					status = data.propertyLabels["bpm_status"];
				}

				// if message is the same as the task type show the <no message> label
				if (message == type) {
					message = this.msg("workflow.no_message");
				}

				var info = '<h3><a href="' + $siteURL('task-edit?taskId=' + taskId + '&referrer=tasks&myTasksLinkBack=true') + '" class="theme-color-1" title="' + this.msg("link.editTask") + '">' + message + '</a></h3>';
				info += '<div class="due"><label>' + this.msg("label.due") + ':</label><span>' + (dueDate ? Alfresco.util.formatDate(dueDate, "longDate") : this.msg("label.none")) + '</span></div>';
				info += '<div class="status"><label>' + this.msg("label.status") + ':</label><span>' + status + '</span></div>';
				info += '<div class="type"><label>' + this.msg("label.type", type) + ':</label><span>' + type + '</span></div>';
				if (!assignee || !assignee.userName) {
					info += '<div class="unassigned"><span class="theme-bg-color-5 theme-color-5 unassigned-task">' + this.msg("label.unassignedTask") + '</span></div>';
				}
				elCell.innerHTML = info;
			},

			renderCellActions: function TL_renderCellActions(elCell, oRecord, oColumn, oData) {
				this.createAction(elCell, this.msg("link.editTask"), "task-edit-link", $siteURL('task-edit?taskId=' + oRecord.getData('id') + '&referrer=tasks&myTasksLinkBack=true'));
				var me = this;
				Alfresco.util.Ajax.request({
					method: "GET",
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/workflow/GetDocumentDataByTaskId?taskID=" + oRecord.getData('id'),
					requestContentType: "application/json",
					responseContentType: "application/json",
					successCallback: {
						fn: function (response) {
							var result = response.json;
							if (result != null) {
								me.createAction(elCell, me.msg("link.documentOpen"), "task-view-link", $siteURL('document?nodeRef=' + encodeURIComponent(result.nodeRef)));
							}
						},
						scope: this
					},
					failureCallback: {
						fn: function (response) {
						},
						scope: this
					}
				});
			}

		}, true);
})();
