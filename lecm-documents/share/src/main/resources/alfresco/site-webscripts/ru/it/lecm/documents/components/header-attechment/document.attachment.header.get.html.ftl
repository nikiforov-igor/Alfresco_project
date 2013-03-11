<#if item??>
	<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
	<#assign id = args.htmlid?html>
	<#assign fileExtIndex = item.fileName?last_index_of(".")>
	<#assign fileExt = (fileExtIndex > -1)?string(item.fileName?substring(fileExtIndex + 1)?lower_case, "generic")>
	<#assign displayName = (item.displayName!item.fileName)?html>

	<div class="document-header">
		<div class="document-info">
			<!-- Icon -->
			<img src="${url.context}/components/images/filetypes/${fileExt}-file-32.png"
			     onerror="this.src='${url.context}/res/components/images/filetypes/generic-file-32.png'"
			     title="${displayName}" class="attachment-thumbnail" width="32" />

			<!-- Title and Version -->
			<h1 class="thin dark">
				<#assign modifyUser = node.properties["cm:modifier"]>
				<#assign modifyDate = node.properties["cm:modified"]>
				<#assign modifierLink = userProfileLink(modifyUser.userName, modifyUser.displayName, 'class="theme-color-1"') >
				${displayName}<span class="document-version">${item.version}</span><span class="document-modified-info">${msg("label.modified-by-user-on-date", modifierLink, xmldate(modifyDate.iso8601)?string(msg("date-format.defaultFTL")))}</span>
			</h1>
		</div>

		<div class="document-action">
		</div>

		<div class="clear"></div>

	</div>
<#else>
	<div class="document-header">
		<div class="status-banner">
			${msg("banner.not-found")}
		</div>
	</div>
</#if>