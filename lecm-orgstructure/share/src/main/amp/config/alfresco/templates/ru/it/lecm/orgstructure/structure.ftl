<@markup id="js">
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-tree.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-utils.js"></@script>
</@>
<@markup id="css">
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-orgstructure/orgstructure-tree.css" />
</@>

<script type="text/javascript">//<![CDATA[
    LogicECM.module = LogicECM.module || {};
    LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

    var response = ${response};
    LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS = response;
//]]>
</script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple>
    <@region id="tree" scope="template" />
</@bpage.basePageSimple>
