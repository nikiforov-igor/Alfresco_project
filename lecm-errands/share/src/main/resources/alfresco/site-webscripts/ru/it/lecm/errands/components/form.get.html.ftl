<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>
<#import "/ru/it/lecm/documents/controls/history/status-history.lib.ftl" as historyStatus/>
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
<#include "/ru/it/lecm/base-share/components/controls/lecm-dnd-uploader-container.ftl">

<#assign id = args.htmlid?js_string>
<#assign controlId = id + "-cntrl">
<#assign containerId = id + "-container">
<#assign viewFormId = id+ "-view">
<#assign canEditExecutionReport = roles.isExecutor && hasAttrEditPerm && isEditableExecutionReport>

<#if node??>
<#assign props = node.properties/>

<script type="text/javascript">
    //<![CDATA[
    var errands;
    (function () {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

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
	                            showCancelButton: false
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

					    Alfresco.util.Ajax.jsonRequest(
                            {
                                method: "POST",
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
		    errands = new LogicECM.module.Errands.dashlet.Errands(htmlId + "-exec-child-errands").setOptions(
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
        var setExecutionReport = new LogicECM.RichTextControl("${id}-setExecutionReport-textarea").setOptions(
            {
                editorParameters: {
                    height: 100,
                    width: 700,
                    inline_styles: false,
                    convert_fonts_to_spans: false,
                    theme: 'advanced',
                    theme_advanced_toolbar_location: "top",
                    theme_advanced_toolbar_align: "left",
                    theme_advanced_statusbar_location: "bottom",
                    theme_advanced_path: false,
                    language: "${locale?substring(0, 2)?js_string}",
                    theme_advanced_resizing: true,
                    theme_advanced_buttons1: "bold,italic,underline,separator,bullist,numlist,separator,forecolor,separator,undo,redo,removeformat",
                    theme_advanced_buttons2: null,
                    theme_advanced_buttons3: null
                }
            });
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
                <div class="form-field field-important">
                    <div class="read-only-important">
                        ${msg("form.control.field-important")}
                    </div>
                </div>
            </#if>

            <div class="form-field field-status">
                <div class="read-only-status">
                    <a onclick="showViewStatusDialog();" href="javascript:void(0);">${props["lecm-statemachine:status"]!""}</a>
                </div>
            </div>
            <@historyStatus.showDialog formId="form-errans-history-status" nodeRef="${nodeRef}" />
        </div>
        <div class="clear"></div>
        <div class="main-info">
            ${msg("message.eddand.fromEmployee")} <a href="javascript:void(0);" onclick="viewAttributes('${props["lecm-errands:initiator-assoc-ref"]}', null, 'logicecm.employee.view')">${props["lecm-errands:initiator-assoc-text-content"]}</a>
            &nbsp;
            <span id="${id}-time-ago"></span>
            &nbsp;${msg("message.eddand.toEmployee")} <a href="javascript:void(0);" onclick="viewAttributes('${props["lecm-errands:executor-assoc-ref"]}', null, 'logicecm.employee.view')">${props["lecm-errands:executor-assoc-text-content"]}</a>
            <br/>
            <#if additionalDoc?? && additionalDoc.name??>
                ${msg("message.eddand.onBasis")} <a href="${siteURL("document?nodeRef=" + additionalDoc.nodeRef)}">${additionalDoc.name}</a>
            </#if>
        </div>
        <#assign content = props["lecm-errands:content"]/>
        <#if content?? && content != "">
            <div class="errand-content">${content}</div>
        </#if>
        <div class="times">
            <#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign>

            <#assign justInTimeProp = props["lecm-errands:just-in-time"],
                justInTime=false>
            <#if justInTimeProp??>
                <#if justInTimeProp?is_boolean>
                    <#assign justInTime=justInTimeProp>
                <#elseif justInTimeProp?is_string && justInTimeProp == "true">
                    <#assign justInTime=true>
                </#if>
            </#if>

            ${msg("message.eddand.limitationDate")} <span id="${id}-limitation-date"></span>
            <input type="hidden" id="errandLimitationDate" value=""/>
            <#if justInTime>
                &nbsp;${msg("message.eddand.justInTime")}
            </#if>
        </div>
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
                                <div>${view.showViewLink(coexec.employeeName, coexec.employeeRef, "logicecm.employee.view")}</div>
                                <div class="position">${coexec.employeePosition}</div>
                                <div class="coexec-ref" style="display: none">${coexec.employeeRef}</div>
                            </div>
                        </li>
                    </#list>
                </#if>
            </ul>
        </div>
        <div class="line"></div>
        <#-- РАБОТА НАД ПОРУЧЕНИЕМ -->
        <div id="${id}-exec" class="block">
	        <#if hasAddAttachmentPerm && hasStatemachine>
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
                <#if roles.isExecutor && isEditableChildErrands && hasActionExecPerm>
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
                                        ${view.showViewLink(childErrand.executorName, childErrand.executorNodeRef, "logicecm.employee.view")}
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
            <div class="form-field" style="padding-left: 50px; padding-right: 10px;">
                <#if canEditExecutionReport>
                    <textarea id="${id}-setExecutionReport-textarea" rows="" cols="">${props["lecm-errands:execution-report"]!""}</textarea>
                <#else>
                    <div style="overflow: auto; border: 1px solid #CCC; border-radius: 3px; padding: 2px; width: 500px; height: 100px; background-color: rgb(235, 235, 228);">${props["lecm-errands:execution-report"]!""}</div>
                </#if>
            </div>
            <#if canEditExecutionReport>
                <div>
                    <span id="${id}-exec-report-set" class="yui-button yui-push-button" style="width: 84px;">
                        <span class="first-child">
                            <button>${msg("message.errands.executionReport.save")}</button>
                        </span>
                    </span>
                    <br/>
                    <span id="${id}-exec-report-reset" class="yui-button yui-push-button" style="margin-top: 10px; width: 84px;">
                        <span class="first-child">
                            <button>${msg("message.errands.executionReport.clear")}</button>
                        </span>
                    </span>
                </div>
            </#if>
            <div style="clear: both;"></div>
        </div>
        <div class="line"></div>
        <#-- КОНТРОЛЬ ИСПОЛНЕНИЯ -->
        <div id="${id}-contr" class="block">
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
    </div>
</div>
</#if>
