<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.dependencies.inc">
    <script type="text/javascript">//<![CDATA[
        var response = ${response};
        if (typeof LogicECM == "undefined" || !LogicECM) {
            LogicECM = {};
        }

        LogicECM.module = LogicECM.module || {};

        LogicECM.module.DocumentsJournal = LogicECM.module.DocumentsJournal|| {};
        LogicECM.module.DocumentsJournal.SETTINGS = response;
        LogicECM.module.DocumentsJournal.CURRENT_USER = "${user.id}";
    //]]>
    </script>
</@>



<@bpage.basePage>
    <@region id="documents-grid" scope="template" />
</@bpage.basePage>