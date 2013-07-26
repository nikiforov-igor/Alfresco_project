<#assign id = args.htmlid?js_string>

<#--<#if data??>-->

<#assign myErrandsActiveSelected = "">
<#if myErrandsState == "active">
    <#assign myErrandsActiveSelected = "selected">
</#if>

<#assign myErrandsCompletedSelected = "">
<#if myErrandsState == "completed">
    <#assign myErrandsCompletedSelected = "selected">
</#if>

<script type="text/javascript">
    LogicECM.module.Errands.SETTINGS =
        <#if errandsDashletSettings?? >
        ${errandsDashletSettings}
        <#else>
        {}
        </#if>;

    var errands = new LogicECM.module.Errands.dashlet.Errands("${id}").setOptions(
    {
        itemType:"lecm-errands:document",
        destination: LogicECM.module.Errands.SETTINGS.nodeRef
    }).setMessages(${messages});
</script>

<div class="list-category">
    <div class="list-category-title">${msg("errandslist.label.my.errands")}</div>
    <div class="tasks-list-filter">
        <select id="${id}-my-errands-states" style="margin-left: 13px;">
            <option selected value="all">${msg("errandslist.option.all")}</option>
            <option ${myErrandsActiveSelected} value="active">${msg("errandslist.option.active")}</option>
            <option ${myErrandsCompletedSelected} value="completed">${msg("errandslist.option.completed")}</option>
        </select>
    </div>

    <#list errandsData.myErrands as errand>
        <#if errand.isImportant == "true">
            <#assign priorityClass = "WORKFLOWTASKPRIORITY_HIGH">
            <#assign priorityMessage = "Важное">
        <#else>
            <#assign priorityClass = "WORKFLOWTASKPRIORITY_LOW">
            <#assign priorityMessage = "">
        </#if>

        <#assign typeClass = "WORKFLOWTASKTYPE_NA">
        <#assign typeMessageKey = "WORKFLOWTASKTYPE_NA">

        <#if errand.isExpired == "true">
            <#assign typeClass = "WORKFLOWTASKTYPE_OVERDUE">
            <#assign typeMessageKey = "WORKFLOWTASKTYPE_OVERDUE">
        <#--<#elseif (todayDate?date("dd/MM/yyyy") < errand.dueDate?date("dd/MM/yyyy")) && (errand.dueDate?date("dd/MM/yyyy") < soonDate?date("dd/MM/yyyy"))>-->
                <#--<#assign typeClass = "WORKFLOWTASKTYPE_SOON">-->
            <#--<#assign typeMessageKey = "WORKFLOWTASKTYPE_SOON">-->
        </#if>

        <#if errand.dueDate == "">
            <#assign dueDate = " - ">
        <#else>
            <#assign dueDate = errand.dueDate>
        </#if>

        <div class="workflow-task-item">
            <div class="workflow-task-list-picture ${priorityClass}" title="${priorityMessage}">&nbsp;</div>
            <div style="float: left;">
                <div>
                    <div class="workflow-task-title workflow-task-list-left-column" style="font-size: 16px;">
                        <a href="${url.context}/page/document?nodeRef=${errand.nodeRef}">${errand.title}:</a>
                    </div>
                    <span class="workflow-task-status ${typeClass}">${msg(typeMessageKey)}</span>
                </div>
                <div style="clear: both;"></div>
                <div class="workflow-task-description">${errand.description}</div>
                <div>
                    <div class="workflow-task-list-left-column">
                        <span class="workflow-task-list-label">${msg("errandslist.label.duedate")}:&nbsp;</span>${dueDate}
                    </div>
                    <span class="workflow-task-list-label">${msg("errandslist.label.status")}: </span>${errand.statusMessage}
                </div>
            </div>
            <div style="clear: both;"></div>
        </div>
    </#list>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
    YAHOO.util.Event.onDOMReady(function (){
        YAHOO.util.Event.on("${id}-my-errands-statuses", "change", onMyErrandsStatusesSelectChange, this, true);
    });

    function onMyErrandsStatusesSelectChange() {
        var statesSelect = Dom.get("${id}-my-errands-states");

        var selectedValue = "";
        if (statesSelect != null && statesSelect.value != null) {
            selectedValue = statesSelect.value;
        }

        documentTasksComponent.setMyErrandsState(selectedValue);
        documentTasksComponent.loadMyErrands();
    }

})();
//]]></script>

<#--</#if>-->