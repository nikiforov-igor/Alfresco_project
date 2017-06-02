<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.dependencies.inc">
    <script type="text/javascript">//<![CDATA[
    if (typeof LogicECM == "undefined" || !LogicECM) {
        LogicECM = {};
    }

    LogicECM.module = LogicECM.module || {};
//]]></script>
</@>

<div id="no_menu_page" class="sticky-wrapper">
<@bpage.basePage showHeader=true showTitle=false showToolbar=false showMenu=false>
   <@region id="content" scope="template"/>
</@bpage.basePage>
</div>
