<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader />

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<div class="share-form">
		<@region id="edit-metadata-mgr" scope="template" />
		<@region id="edit-metadata" scope="template" />
	</div>
</@bpage.basePage>