<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.templateHeader>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-header.css" />

    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/lecm-document-regnum-uniqueness-validator.js"/>

<#-- подключить все скрипты необходимые для диалоговых форм -->
    <#include "/org/alfresco/components/form/form.dependencies.inc">
</@>


<div id="no_menu_page" class="sticky-wrapper">
    <@bpage.basePage showHeader=true showTitle=true showToolbar=false showMenu=false>
        <@region id="content" scope="template"/>
    </@bpage.basePage>
</div>
