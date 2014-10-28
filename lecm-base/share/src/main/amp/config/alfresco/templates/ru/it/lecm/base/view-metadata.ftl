<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/ru/it/lecm/base-share/components/view.lib.ftl"/>
<@templateHeader />

<@templateBody>
<@viewForm />
<div id="alf-hd">
	<@region id="share-header" scope="global" />
    <#-- <@region id="title" scope="template" /> -->
    <@region id="navigation" scope="template" />
</div>
<div id="bd">
    <div class="share-form view-metadata">
        <@region id="edit-metadata" scope="template" />
    </div>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
	<@region id="footer" scope="global" />
</div>
</@>
