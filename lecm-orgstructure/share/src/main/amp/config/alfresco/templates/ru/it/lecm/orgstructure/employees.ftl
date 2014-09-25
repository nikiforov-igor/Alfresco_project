<script type="text/javascript">//<![CDATA[
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

    var response = ${response};
    LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS = response;
//]]>
</script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePageSimple>
    <@region id="employees-grid" scope="template" />
</@bpage.basePageSimple>
