<script type="text/javascript">//<![CDATA[
    if (typeof LogicECM == "undefined" || !LogicECM) {
        var LogicECM = {};
    }
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

    var response = ${response};
    LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS = response;
    LogicECM.module.OrgStructure.IS_ENGINEER = ${isOrgEngineer?string};
//]]>
</script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePageSimple>
    <@region id="employees-grid" scope="template" />
</@bpage.basePageSimple>
