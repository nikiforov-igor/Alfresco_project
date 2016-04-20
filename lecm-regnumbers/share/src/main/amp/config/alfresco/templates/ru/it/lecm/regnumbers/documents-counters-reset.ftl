<script type="text/javascript">
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.Counters = LogicECM.module.Counters || {};
    LogicECM.module.Counters.isEngineer = ${isEngineer?string};

    LogicECM.module.Counters.Const = LogicECM.module.Counters.Const || {};

    LogicECM.module.Counters.Const.COUNTERS_CONTAINER = LogicECM.module.Counters.Const.COUNTERS_CONTAINER || ${countersContainer};
    LogicECM.module.Counters.Const.COUNTERS_DATAGRID_LABEL = LogicECM.module.Counters.Const.COUNTERS_DATAGRID_LABEL || "countersDatagrid";

    //]]></script>
</script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#assign hasPermission = isEngineer/>
<@bpage.basePageSimple>
    <#if hasPermission>
        <@region id="counters-grid" scope="template"/>
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePageSimple>