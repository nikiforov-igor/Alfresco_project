
<#assign el=args.htmlid/>

<div class="widget-bordered-panel">
    <div class="document-forms-panel">
        <h2 id="${el}-heading" class="grey">
            ${msg("heading")}
        </h2>

        <div id="${el}-formContainer">
            <div class="form-field">
            <#escape x as x?js_string>
                <#list reportsDescriptors as report>
                    <div>
                        <h3>
                            <a href="#" id="reports-list-report-link" class="theme-color-1" style="font-weight: bold;"
                               onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportCode": "${report.code}", "nodeRef":"${nodeRef}"});'><#if report.name != "">${report.name}<#else>(no name)</#if></a>
                        </h3>
                    </div>
                    <br/>
                </#list>
            </#escape>
            </div>
        </div>

        <script type="text/javascript">
            var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event;

            function init() {
                var forms = Dom.getChildren(Dom.getChildren("${el}-formContainer")[0]);

                if (forms.length > 0) {
                    var heading = Dom.get("${el}-heading");

                    Dom.removeClass(heading, "grey");
                    Dom.addClass(heading, "dark");
                    Alfresco.util.createTwister(heading, "DocumentForms");
                }
            }

            Event.onDOMReady(init);

        </script>
    </div>
</div>
