<#if item??>
	<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
    <#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>
	<#assign id = args.htmlid?html>
	<#assign fileExtIndex = item.fileName?last_index_of(".")>
	<#assign fileExt = (fileExtIndex > -1)?string(item.fileName?substring(fileExtIndex + 1)?lower_case, "generic")>
	<#assign displayName = (item.displayName!item.fileName)?html>

	<div class="document-header">
        <@view.viewForm formId="${id}-view-modifier-header-form"/>
		<div class="document-attachment-info">
			<h1 class="thin dark breadcrumb">
				<#if hasViewListPerm>
					<a class="title" href="${siteURL("document?nodeRef=" + documentNodeRef)}">${documentName}</a> :: <a class="title" href="${siteURL("document?nodeRef=" + documentNodeRef + "&view=attachments")}">${msg("title.attachments")}</a> ::
				<#else>
					<a class="title" href="${siteURL("document?nodeRef=" + documentNodeRef)}">${documentName}</a> :: ${msg("title.attachments")} ::
				</#if>
			</h1>

			<!-- Icon -->
			<img src="${url.context}/components/images/filetypes/${fileExt}-file-32.png"
			     onerror="this.src='${url.context}/res/components/images/filetypes/generic-file-32.png'"
			     title="${displayName}" class="attachment-thumbnail" width="32" />

			<!-- Title and Version -->
			<h1 class="thin dark">
				<#assign modifyUser = node.properties["lecm-document:modifier"]!"">
                <#assign modifyUserRef = node.properties["lecm-document:modifier-ref"]!"">
				<#assign modifyDate = node.properties["cm:modified"]>
				<#assign modifierLink = view.showViewLink(modifyUser, modifyUserRef, "logicecm.employee.view")>
				${displayName}<span class="document-version">${item.version}</span><span class="document-modified-info">${msg("label.modified-by-user-on-date", modifierLink, xmldate(modifyDate.iso8601)?string(msg("date-format.defaultFTL")))}</span>
			</h1>
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