<script type="text/javascript">//<![CDATA[
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.Templates = LogicECM.module.Templates || {};

    LogicECM.module.Templates.TEMPLATES_FOLDER = "${nodeRef}";
//]]>
</script>
<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#assign hasPermission = hasRole/>
<@bpage.basePageSimple showToolbar=false>
    <#if hasPermission>
        <@region id="templates-toolbar" scope="template" />
        <@region id="templates-grid" scope="template"/>
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePageSimple>