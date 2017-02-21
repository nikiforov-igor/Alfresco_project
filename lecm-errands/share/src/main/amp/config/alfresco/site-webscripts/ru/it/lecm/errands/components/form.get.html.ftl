<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/lecm-errands-dashlet.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/errands-links.js"></@script>

<!-- Document Metadata Header -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-metadata.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-form.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-main-form.css" />


<#import "/ru/it/lecm/documents/controls/history/status-history.lib.ftl" as historyStatus/>
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
<#include "/ru/it/lecm/base-share/components/controls/lecm-dnd-uploader-container.ftl">
<#include "/org/alfresco/components/form/controls/common/editorparams.inc.ftl" />
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid?js_string>
<#assign controlId = id + "-cntrl">
<#assign containerId = id + "-container">
<#assign viewFormId = id+ "-view">
<#assign canEditExecutionReport = roles.isExecutor && hasAttrEditPerm && isEditableExecutionReport>

<#if node??>
<#assign props = node.properties/>

<script type="text/javascript">//<![CDATA[
	if (typeof LogicECM == "undefined" || !LogicECM) {
		LogicECM = {};
	}
	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Documents = LogicECM.module.Documents|| {};

    var setExecutionReport = null;
    (function () {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

		var viewHistory = new LogicECM.module.Document.ViewHistory("save-view-history").setOptions({
			nodeRef: "${nodeRef}"
		});
		viewHistory.save();

        function init() {
			<#if hasAttrEditPerm>
	            new LogicECM.module.Errands.Links("${id}").setOptions(
	                    {
	                        destination: "${nodeRef}",
	                        totalLinks: ${links.links?size},
	                        totalExecuteLinks:${executeLinks.links?size}
	                    }).setMessages(${messages});
			</#if>

            <#-- "time ago"  - start -->
            var date = "${props["cm:created"]["iso8601"]}";

            Dom.get("${id}-time-ago").innerHTML = Alfresco.util.relativeTime(new Date(date));
            <#-- "time ago"  - end -->

            <#-- Make twisters - start -->
            var blocks = Dom.getElementsByClassName("data-list-block", "div", "${id}_metadata");

            for (var i = 0; i < blocks.length; i++) {
                var block = blocks[i];
                var heading = Dom.getElementsByClassName("heading", "span", block)[0];
                var ul = Dom.getElementsByClassName("data-list", "ul", block)[0];

                Alfresco.util.createTwister(heading, "", {
                    panel: ul
                });
            }
            <#-- Make twisters - end -->

			<#if hasAddAttachmentPerm>
                drawDndForm("${nodeRef}", '${id}');
			</#if>
			<#if hasAttrEditPerm>
	            initSetExecutionReportForm('${id}');
			</#if>
	        initChildErrands('${id}');

            <#if limitDate?? && limitDate.iso8601??>
                var limitDate = "${limitDate.iso8601}";
                if (limitDate !== "") {
                    var localLimitDate = Alfresco.util.fromISO8601(limitDate);
                    Dom.get("${id}-limitation-date").innerHTML = localLimitDate.toString(dateDisplayControlMsg("form.control.date-picker.entry.date.format"));

                    var month = localLimitDate.getMonth() + 1;
                    var date = localLimitDate.getDate();
                    var hours = localLimitDate.getHours();
                    var minutes = localLimitDate.getMinutes();
                    Dom.get("errandLimitationDate").value = localLimitDate.getFullYear() + "-" + (month < 10 ? "0" : "") + month + "-" + (date < 10 ? "0" : "") + date + "T"
                            + (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
                }
            </#if>
        }

        function dateDisplayControlMsg(messageId) {
            return Alfresco.util.message.call(this, messageId, "LogicECM.DateDisplayControl", Array.prototype.slice.call(arguments).slice(1));
        }

		<#if hasAddAttachmentPerm>
	        function drawDndForm(nodeRef, htmlId) {
		        var formId = htmlId + "-attachemnts";
	            Alfresco.util.Ajax.request(
	                    {
	                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
	                        dataObj: {
	                            htmlid: formId,
	                            itemKind: "node",
	                            itemId: nodeRef,
	                            formId: "errands-dnd",
	                            mode: "edit",
		                        submitType:"json",
	                            showSubmitButton: true,
	                            showResetButton: false,
	                            showCancelButton: false,
								showCaption: false
	                        },
	                        successCallback: {
	                            fn: function (response) {
	                                var container = Dom.get('${id}-dnd');
	                                if (container != null) {
	                                    container.innerHTML = response.serverResponse.responseText;

		                                var form = new Alfresco.forms.Form(formId + '-form');
		                                form.setSubmitAsJSON(true);
		                                form.setAJAXSubmit(true,
				                                {
					                                successCallback:
					                                {
						                                fn: function(response) {
							                                window.location.reload(true);
						                                },
						                                scope: this
					                                }
				                                });
		                                form.init();

		                                Dom.setStyle(formId + "-form-buttons", "visibility", "hidden");
	                                }
	                            }
	                        },
	                        failureMessage: "message.failure",
	                        execScripts: true
	                    });
	        }
		</#if>

		<#if hasAttrEditPerm>
		    function initSetExecutionReportForm(htmlId) {
			    var execReportElement = YAHOO.util.Dom.get(htmlId + "-setExecutionReport-textarea");
			    if (execReportElement != null) {
				    Alfresco.util.createYUIButton(YAHOO.util.Dom.get(htmlId), "exec-report-set", function() {
                        if (setExecutionReport == null) {
                            return;
                        }

					    Alfresco.util.Ajax.jsonPost({
                            url: Alfresco.constants.PROXY_URI + "lecm/errands/api/setExecutionReport",
                            dataObj: {
                                nodeRef: "${nodeRef}",
                                executionReport: setExecutionReport.editor.getContent()
                            },
                            successMessage: "${msg("message.setExecutionReport.success")}",
                            failureMessage: "${msg("message.setExecutionReport.failure")}"
                        });
				    });

				    Alfresco.util.createYUIButton(YAHOO.util.Dom.get(htmlId), "exec-report-reset", function() {
                        if (setExecutionReport == null) {
                            return;
                        }

                        setExecutionReport.editor.clear();
				    });
			    }
		    }
		</#if>

	    function initChildErrands(htmlId) {
		    var errands = new LogicECM.module.Errands.dashlet.Errands(htmlId + "-exec-child-errands").setOptions(
				    {
					    itemType: "lecm-errands:document",
					    destination: LogicECM.module.Documents.ERRANDS_SETTINGS.nodeRef,
                        parentDoc:"${nodeRef}"
				    }).setMessages(${messages});

		    Alfresco.util.createYUIButton(YAHOO.util.Dom.get(htmlId), "exec-child-errands-add", function() {
			    errands.createChildErrand()
		    });
	    }

        Event.onDOMReady(init);
    })();
    //]]>
</script>

<#if canEditExecutionReport>
    <script type="text/javascript">//<![CDATA[
    (function() {

    	function init() {
            LogicECM.module.Base.Util.loadScripts([
				'scripts/lecm-base/components/lecm_tiny_mce.js',
                'scripts/lecm-base/components/lecm-rich-text.js'
			], createRichText);
		}
		function createRichText() {
       		setExecutionReport = new LogicECM.RichTextControl("${id}-setExecutionReport-textarea").setOptions(
            {
                editorParameters: {
                    height: 100,                    
                    inline_styles: false,                    
                    language: "${locale?substring(0, 2)?js_string}",
					menu: {},
					toolbar: "bold italic underline | bullist numlist | forecolor | undo redo removeformat"
                }
            });
        }
		YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>
</#if>

<div id="${id}_metadata" class="metadata-form errands-main-form">
    <div id="${id}-form-fields" class="form-fields">
        <div class="title">
            ${props["lecm-errands:title"]}
        </div>
        <div class="statuses">
            <#assign isImportantProp = props["lecm-errands:is-important"]/>
            <#assign isImportant = false/>
            <#if isImportantProp??>
                <#if isImportantProp?is_boolean>
                    <#assign isImportant=isImportantProp>
                <#elseif isImportantProp?is_string && isImportantProp == "true">
                    <#assign isImportant=true>
                </#if>
            </#if>
            <#if isImportant>
                <div class="control important-control">
                    ${msg("form.control.field-important")}
                </div>
            </#if>

            <div class="control status-control">
                <a onclick="LogicECM.module.DocumentStatusHistory.showDialog('form-errans-history-status', '${nodeRef}');" href="javascript:void(0);">${props["lecm-statemachine:status"]!""}</a>
            </div>
            <@historyStatus.showDialog formId="form-errans-history-status"/>
        </div>
        <div class="clear"></div>
        <div class="main-info">
            ${msg("message.eddand.fromEmployee")} <a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:'${props["lecm-errands:initiator-assoc-ref"]}', title: 'logicecm.employee.view'})">${props["lecm-errands:initiator-assoc-text-content"]}</a>
            &nbsp;
            <span id="${id}-time-ago"></span>
            &nbsp;${msg("message.eddand.toEmployee")} <a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:'${props["lecm-errands:executor-assoc-ref"]}', title: 'logicecm.employee.view'})">${props["lecm-errands:executor-assoc-text-content"]}</a>
            <br/>
            <#if additionalDoc?? && additionalDoc.name??>
                ${msg("message.eddand.onBasis")} <a href="${siteURL("document?nodeRef=" + additionalDoc.nodeRef)}">${additionalDoc.name}</a>
            </#if>
        </div>
        <#if props["lecm-errands:content"]?has_content>
            <div class="errand-content">${props["lecm-errands:content"]}</div>
        </#if>
        <#if hasViewContentListPerm>
	        <div id="${id}-attachments" class="data-list-block">
	            <span class="heading">${msg("message.eddand.attachments")}<span class="count"> (${(attachments![])?size})</span></span>
	            <ul class="data-list">
	                <#if attachments?? && attachments?size gt 0>
	                    <#list attachments as attachment>
	                        <li title="${attachment.name!""}">
	                            <img src="${url.context}/res/components/images/filetypes/${fileIcon(attachment.name, 16)}" class="file-icon"/>
		                        <#if hasViewAttachmentPerm>
		                            <a href="${url.context}/page/document-attachment?nodeRef=${attachment.nodeRef}">
		                                ${attachment.name!""}
		                            </a>
		                        <#else>
		                            ${attachment.name!""}
		                        </#if>
	                        </li>
	                    </#list>
	                </#if>
	            </ul>
	        </div>
        </#if>
        <div id="${id}-links" class="data-list-block">
            <span class="heading">${msg("label.links.head")}<span class="count" id="${id}-links-count"> (${links.links?size})</span></span>
            <#if roles.isInitiator && hasAttrEditPerm && isEditableLinks>
		        <span id="${id}-links-add" class="yui-button yui-push-button">
	                <span class="first-child">
	                    <button type="button">${msg("label.links.button")}</button>
	                </span>
	            </span>
            </#if>
            <ul class="data-list" id="${id}-links-list">
                <#if links.links?? && (links.links?size > 0)>
                    <#list links.links as link>
                        <li title="${link.name!""}">
                            <img src="${url.context}/res/components/images/filetypes/generic-file-16.png" class="file-icon"/>
                            <#if link.url?matches("://")>
                                <a href="${link.url}">${link.name!""}</a>
                            <#else>
                                <a href="http://${link.url}">${link.name!""}</a>
                            </#if>

                        </li>
                    </#list>
                </#if>
            </ul>
        </div>
        <div id="${id}-coexecs" class="data-list-block">
            <span class="heading">${msg("message.eddand.coexecutors")}<span class="count"> (${(coexecs![])?size})</span></span>
            <ul class="data-list persons-list">
                <#if coexecs?? && coexecs?size gt 0>
                    <#list coexecs as coexec>
                        <li>
                            <div class="avatar">
                                <img src="${url.context}/proxy/alfresco/lecm/profile/employee-photo?nodeRef=${coexec.employeeRef}" alt="Avatar" />
                            </div>
                            <div class="person">
                                <div><a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:'${coexec.employeeRef}', title:'logicecm.employee.view'})">${coexec.employeeName}</a></div>
                                <div class="position">${coexec.employeePosition}</div>
                                <div class="coexec-ref hidden1">${coexec.employeeRef}</div>
                            </div>
                        </li>
                    </#list>
                </#if>
            </ul>
        </div>
        <div class="line"></div>
        <#-- РАБОТА НАД ПОРУЧЕНИЕМ -->
        <div id="${id}-exec" class="block">
	        <#if props["lecm-statemachine:status"] == "В работе" && hasAddAttachmentPerm && hasStatemachine>
		        <div id="${id}-dnd" class="dnd-uploader"></div>
	        </#if>
            <div class="title">${msg("message.eddand.work")}</div>
			<#if hasViewContentListPerm>
	            <div id="${id}-exec-attachments" class="data-list-block">
	                <span class="heading">${msg("message.eddand.attachments")}<span class="count"> (${(attachmentsExec![])?size})</span></span>
	                <ul class="data-list">
	                    <#if attachmentsExec?? && attachmentsExec?size gt 0>
	                        <#list attachmentsExec as attachment>
	                            <li title="${attachment.name!""}">
	                                <img src="${url.context}/res/components/images/filetypes/${fileIcon(attachment.name, 16)}" class="file-icon"/>
		                            <#if hasViewAttachmentPerm>
			                            <a href="${url.context}/page/document-attachment?nodeRef=${attachment.nodeRef}">
			                                ${attachment.name!""}
			                            </a>
		                            <#else>
		                                ${attachment.name!""}
		                            </#if>
	                            </li>
	                        </#list>
	                    </#if>
	                </ul>
	            </div>
			</#if>
            <div id="${id}-exec-links" class="data-list-block">
                <span class="heading">${msg("label.links.head")}<span class="count" id="${id}-execute-links-count"> (${(executeLinks.links)?size})</span></span>
				<#if roles.isExecutor && hasAttrEditPerm && isEditableExecutionLinks>
		            <span id="${id}-exec-links-add" class="yui-button yui-push-button">
	                    <span class="first-child">
	                        <button type="button">${msg("label.links.button")}</button>
	                    </span>
	                </span>
				</#if>
                <ul class="data-list" id="${id}-execute-links-list">
                    <#if executeLinks.links?? && (executeLinks.links?size > 0)>
                        <#list executeLinks.links as link>
                            <li title="${link.name!""}">
                                <img src="${url.context}/res/components/images/filetypes/generic-file-16.png" class="file-icon"/>
                                <#if link.url?matches("://")>
                                    <a href="${link.url}">${link.name!""}</a>
                                <#else>
                                    <a href="http://${link.url}">${link.name!""}</a>
                                </#if>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
            <div id="${id}-exec-child-errands" class="data-list-block">
                <span class="heading">${msg("message.eddand.childErrands")}<span class="count"> (${(childErrands![])?size})</span></span>
                <#if roles.isExecutor && isEditableChildErrands && hasActionExecPerm && isErrandsStarter>
	                <span id="${id}-exec-child-errands-add" class="yui-button yui-push-button">
	                    <span class="first-child">
	                        <button type="button">${msg("message.eddand.addChildErrands")}</button>
	                    </span>
	                </span>
                </#if>
                <ul class="data-list persons-list">
                    <#if childErrands?? && childErrands?size gt 0>
                        <#list childErrands as childErrand>
                            <li>
                                <div class="avatar">
                                    <img src="${url.context}/proxy/alfresco/lecm/profile/employee-photo?nodeRef=${childErrand.executorNodeRef}" alt="Avatar" />
                                </div>
                                <div class="person">
                                    <div>
                                        <a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:'${childErrand.executorNodeRef}', title:'logicecm.employee.view'})">${childErrand.executorName}</a>
                                        ${msg("message.eddand.employee.get")}
                                        <span class="text-cropped"><a href="${siteURL("document?nodeRef=" + childErrand.nodeRef)}">${childErrand.name}</a></span>
                                    </div>
                                    <div class="descr">
                                        ${msg("message.eddand.limitationDate")} <span>${childErrand.limitationDate}</span>
                                    </div>
                                </div>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
            <div id="${id}-exec-report" class="exec-report">
                <span class="heading">${msg("message.errands.executionReport")}</span>
            </div>
            <#if canEditExecutionReport>
                <div class="control richtext editmode">
                    <div class="container">
                        <div class="value-div">
                            <textarea id="${id}-setExecutionReport-textarea" name="${id}-setExecutionReport-textarea" rows="2" columns="60" tabindex="0">${props["lecm-errands:execution-report"]!""}</textarea>
                        </div>
                    </div>
                </div>
            <#else>
                <div class="control richtext viewmode">
                    <div class="container">
                        <div class="value-div">
                            ${props["lecm-errands:execution-report"]!""}
                        </div>
                    </div>
                </div>
            </#if>
            <#if canEditExecutionReport>
                <div class="exec-block">
                    <span id="${id}-exec-report-transfer-coexecutors-reports" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button>${msg("message.errands.executionReport.transferCoexecutorsReports")}</button>
                        </span>
                    </span>
                    <span id="${id}-exec-report-reset" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button>${msg("message.errands.executionReport.clear")}</button>
                        </span>
                    </span>
                    <span id="${id}-exec-report-set" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button>${msg("message.errands.executionReport.save")}</button>
                        </span>
                    </span>
                </div>
            </#if>
            <div class="clear"></div>
        </div>
        <div class="line"></div>
        <#-- КОНТРОЛЬ ИСПОЛНЕНИЯ -->
        <div id="${id}-contr" class="block control-of-execution">
            <#if props["lecm-statemachine:status"] == "На утверждении контролером" && hasAddAttachmentPerm && hasStatemachine>
                <div id="${id}-dnd" class="dnd-uploader"></div>
            </#if>
            <div class="title">${msg("message.eddand.executionControl")}</div>
			<#if hasViewContentListPerm>
	            <div id="${id}-contr-attachments" class="data-list-block">
	                <span class="heading">${msg("message.eddand.attachments")}<span class="count"> (${(attachmentsControl![])?size})</span></span>
	                <ul class="data-list">
	                    <#if attachmentsControl?? && attachmentsControl?size gt 0>
	                        <#list attachmentsControl as attachment>
	                            <li title="${attachment.name!""}">
	                                <img src="${url.context}/res/components/images/filetypes/${fileIcon(attachment.name, 16)}" class="file-icon"/>
		                            <#if hasViewAttachmentPerm>
			                            <a href="${url.context}/page/document-attachment?nodeRef=${attachment.nodeRef}">
			                                ${attachment.name!""}
			                            </a>
		                            <#else>
		                                ${attachment.name!""}
		                            </#if>
	                            </li>
	                        </#list>
	                    </#if>
	                </ul>
	            </div>
			</#if>
        </div>
        <div class="line"></div>

        <#-- Отчеты соисполнителей -->
        <#assign fieldHtmlId = containerId + "-coexecutors-reports">
        <#assign controlId = fieldHtmlId + "-cntrl">
        <#assign containerId = fieldHtmlId + "-container-" + .now?iso_utc>

        <div id="${id}-coexecutors-reports" class="block">
            <div class="title">${msg("message.errand.coexecutorReports")}</div>
            <script type="text/javascript">//<![CDATA[
            (function() {
                function drawForm(){
                    var control = new LogicECM.errands.CoexecutorsReportsTS("${fieldHtmlId}").setMessages(${messages});
                    control.setOptions(
                            {
                                currentValue: "${node.properties["lecm-errands-ts:coexecutor-reports-assoc-ref"]!""}",
                                messages: ${messages},
                                bubblingLabel: "${containerId}-bubbling",
                                containerId: "${containerId}",
                                datagridFormId: "coexecutors-reports-datagrid",
                                attributeForShow: "",
                                mode: "view",
                                disabled: false,
                                isTableSortable: false,
                                externalCreateId: "",
                                refreshAfterCreate: false,
                                expandable: true,
                                expandDataSource: "components/form?formId=table-structure-expand",
                                documentNodeRef: "${node.nodeRef!""}",
                                showActions: false
                            });
                }
                function init() {
                    LogicECM.module.Base.Util.loadResources([
                                'scripts/lecm-base/components/advsearch.js',
                                'scripts/lecm-base/components/lecm-datagrid.js',
                                'scripts/documents/tables/lecm-document-table.js',
                                'scripts/lecm-errands/coexecutors-reports-table-control.js'
                            ],
                            [
                                'css/lecm-errands/coexecutors-reports-table-control.css'
                            ], drawForm);
                }
                YAHOO.util.Event.onDOMReady(init);
            })();
            //]]></script>

            <div class="form-field with-grid coexecutors-report" id="${controlId}">
                <div class="reports-filter-block">
                    <input type="checkbox" id="${controlId}-change-filter">
                    <label id="${controlId}-change-filter-label" for="${controlId}-change-filter"></label>
                </div>
                <@grid.datagrid containerId false/>
                <div id="${controlId}-container">
                    <input type="hidden" id="${fieldHtmlId}" name="${fieldHtmlId}" value="${node.properties["lecm-errands-ts:coexecutor-reports-assoc-ref"]!""}"/>
                </div>
            </div>
        </div>
        <div class="line"></div>


    </div>
</div>
</#if>
