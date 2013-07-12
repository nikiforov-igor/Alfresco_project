<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="reports-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
        <#list reportsDescriptors as report>
            <div>
                <h3>
                    <a href="#" id="reports-list-report-link" class="theme-color-1" style="font-weight: bold;"
                       onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportCode": "${report.code}"});'>${report.name}</a>
                </h3>
            </div>
            <br/>
        </#list>
		</div>
	</div>
</div>
