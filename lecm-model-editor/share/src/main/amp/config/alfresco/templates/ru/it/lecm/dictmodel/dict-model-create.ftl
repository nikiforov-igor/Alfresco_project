<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.templateHeader>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/model-editor/model-editor.css" group="model-editor"/>
	<@script type="text/javascript" src="${url.context}/res/components/model-editor/utils.js" group="model-editor"/>
</@>

<@bpage.basePage showToolbar=isAdmin showMenu=isAdmin>
	<#if isAdmin>
		<div class="share-form">
			<@region id="create-content-mgr" scope="template" />
			<@region id="create-content" scope="template" />
		</div>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePage>
