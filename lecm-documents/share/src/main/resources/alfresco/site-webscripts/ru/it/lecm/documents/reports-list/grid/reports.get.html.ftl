<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="reports-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
        <#list reportsDescriptors as report>
            <div>
                <h3>
                    <a href="#" id="reports-list-report-link" class="theme-color-1" style="font-weight: bold;"
                       onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportType": "${report.code}"});'>${report.name}</a>
                </h3>
            </div>
            <br/>
        </#list>
            <br/>

            <div>
                <h3>
                    <a href="#" id="contracts-list-reports-link" class="theme-color-1" style="font-weight: bold;"
                       onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportType": "contracts-list"});'>Реестр
                        договоров</a>
                </h3>
                Отчеты по реестру договоров (изменить текст)
            </div>

            <br/>

            <div>
                <h3>
                    <a href="#" id="approval-discipline-reports-link" class="theme-color-1" style="font-weight: bold;"
                       onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportType": "approval-discipline"});'>Исполнительская
                        дисциплина по согласованиям за период</a>
                </h3>
                Отчеты по согласованиям (изменить текст)
            </div>

            <br/>

            <div>
                <h3>
                    <a href="#" id="docflow-timings-reports-link" class="theme-color-1" style="font-weight: bold;"
                       onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportType": "docflow-timings"});'>Сроки
                        прохождения маршрута за период</a>
                </h3>
                Сроки прохождения маршрута (изменить текст)
            </div>

            <br/>

            <div>
                <h3>
                    <a href="#" id="docflow-counters-reports-link" class="theme-color-1" style="font-weight: bold;"
                       onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportType": "docflow-counters"});'>Сводный
                        отчёт по договорам</a>
                </h3>
                Сводный отчёт по договорам (изменить текст)
            </div>

            <br/>
		</div>
	</div>
</div>
