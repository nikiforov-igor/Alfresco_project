<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>

<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/reports-grid.css" />
</@>

<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="reports-grid">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
        <#list reportsDescriptors as report>
            <div>
                <h3>
                    <a href="#" class="theme-color-1"
                       onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportCode": "${report.code}"});'><#if report.name != "">${report.name}<#else>(no name)</#if></a>
                </h3>
                <#if report.description?? && report.description != "">
                    ${report.description}
                </#if>
            </div>
            <br/>
        </#list>
		</div>
	</div>
</div>
