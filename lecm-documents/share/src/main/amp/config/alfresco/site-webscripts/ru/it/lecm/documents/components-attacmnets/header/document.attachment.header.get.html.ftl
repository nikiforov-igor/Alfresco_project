<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-header.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/node-details/node-header.css" />
<@script type="text/javascript" src="${url.context}/res/components/node-details/node-header.js"></@script>

<#if item??>
	<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
	<#assign id = args.htmlid?html>
	<#assign fileExtIndex = item.fileName?last_index_of(".")>
	<#assign fileExt = (fileExtIndex > -1)?string(item.fileName?substring(fileExtIndex + 1)?lower_case, "generic")>
	<#assign displayName = (item.displayName!item.fileName)?html>

	<script type="text/javascript">
		(function(){
			var categories = [];
			<#if allAttachments?? && allAttachments.items??>
				<#list allAttachments.items as item>
					<#if item.category?? && item.attachments??>
						<#list item.attachments as attachment>
							categories['${attachment.nodeRef}'] = '${item.category.nodeRef}';
						</#list>
					</#if>
				</#list>
			</#if>

			function init() {
				var attachmentSelect = YAHOO.util.Dom.get("all-attachments-select");
				if (attachmentSelect != null) {
					YAHOO.util.Event.on(attachmentSelect, "change", function() {
						var nodeRef = this.options[attachmentSelect.selectedIndex].value;
						var url = Alfresco.util.siteURL('document-attachment?nodeRef=' + nodeRef);
						if (categories[nodeRef].length == 0) {
							window.open(url,'_blank');
						} else {
							window.open(url, '_self');
						}
					});
				}
			}

			YAHOO.util.Event.onDOMReady(init);
		})();
	</script>

	<div class="document-header">
        <!-- Message banner -->
            <#if item.workingCopy??>
                <#assign modifyLabel = "label.editing-started-on-date-by-user">
                <#if item.workingCopy.isWorkingCopy??>
                    <#assign lockUser = node.properties["cm:workingCopyOwner"]>
                <#else>
                    <#assign lockUser = node.properties["cm:lockOwner"]>
                </#if>
                <#if lockUser??>
                    <div class="status-banner theme-bg-color-2 theme-border-4">
                        <#assign lockedByLink = userProfileLink(lockUser.userName, lockUser.displayName, 'class="theme-color-1"') >
                        <#if (item.workingCopy.googleDocUrl!"")?length != 0 >
                            <#assign link><a href="${item.workingCopy.googleDocUrl}" target="_blank" class="theme-color-1">${msg("banner.google-docs.link")}</a></#assign>
                            <#if lockUser.userName == user.name>
                                <span class="google-docs-owner">${msg("banner.google-docs-owner", link)}</span>
                            <#else>
                                <span class="google-docs-locked">${msg("banner.google-docs-locked", lockedByLink, link)}</span>
                            </#if>
                        <#else>
                            <#if lockUser.userName == user.name>
                                <#assign status><#if node.isLocked>lock-owner<#else>editing</#if></#assign>
                                <span class="${status}">${msg("banner." + status)}</span>
                            <#else>
                                <span class="locked">${msg("banner.locked", lockedByLink)}</span>
                            </#if>
                        </#if>
                    </div>
                </#if>
            <#elseif (node.isLocked && (node.properties["cm:lockType"]!"") == "WRITE_LOCK")>
                <#assign lockUser = node.properties["cm:lockOwner"]>
                <#if lockUser??>
                    <div class="status-banner theme-bg-color-2 theme-border-4">
                        <#assign lockedByLink = userProfileLink(lockUser.userName, lockUser.displayName, 'class="theme-color-1"') >
                        <#if lockUser.userName == user.name>
                            <span class="lock-owner">${msg("banner.lock-owner")}</span>
                        <#else>
                            <span class="locked">${msg("banner.locked", lockedByLink)}</span>
                        </#if>
                    </div>
                </#if>
            </#if>
		<div class="document-attachment-info">
			<h1 class="thin dark breadcrumb">
				<#if documentNodeRef??>
				<#if hasViewListPerm>
					<a class="title" href="${siteURL("${documentPageName}" + "?nodeRef=" + documentNodeRef)}">${documentName}</a> :: <a class="title" href="${siteURL("${documentPageName}" + "?nodeRef=" + documentNodeRef + "&view=attachments")}">${msg("title.attachments")}</a> ::
				<#else>
					<a class="title" href="${siteURL("${documentPageName}" + "?nodeRef=" + documentNodeRef)}">${documentName}</a> :: ${msg("title.attachments")} ::
				</#if>
				</#if>
			</h1>

			<!-- Icon -->
			<img src="${url.context}/components/images/filetypes/${fileExt}-file-32.png"
			     onerror="this.src='${url.context}/res/components/images/filetypes/generic-file-32.png'"
			     title="${displayName}" class="attachment-thumbnail" width="32" />

			<!-- Title and Version -->
			<h1 class="thin dark">
				<select id="all-attachments-select">
					<#if allAttachments?? && allAttachments.items??>
						<#list allAttachments.items as item>
							<#if item.category??>
							    <optgroup label="${item.category.name!""}">
									<#if item.attachments??>
										<#list item.attachments as attachment>
											<option value="${attachment.nodeRef}" <#if attachment.nodeRef == nodeRef>selected="selected"</#if>>
												${attachment.name!""}
											</option>
										</#list>
									</#if>
							    </optgroup>
							</#if>
						</#list>
					<#else>
						<option value="${nodeRef}">${displayName}</option>
					</#if>
				</select>
			</h1>
            <div class="second-row">
                <#--<#assign modifyUser = node.properties["lecm-document:modifier"]!"">-->
                <#--<#assign modifyUserRef = node.properties["lecm-document:modifier-ref"]!"">-->
                <#--<#assign modifierLink = view.showViewLink(modifyUser, modifyUserRef, "logicecm.employee.view")>-->
                <#assign modifyDate = node.properties["cm:modified"]>
                <span>${msg("label.version")}</span>
                <span class="document-version">${item.version}</span>
                <span class="document-modified-info">${msg("label.modified-by-user-on-date", xmldate(modifyDate.iso8601)?string(msg("date-format.rfc822")))}</span>
            </div>
		</div>

		<div class="clear"></div>

	</div>
<#else>
	<div class="document-header">
		<div class="status-banner">
            ${msg(accessMsg)}
		</div>
	</div>
</#if>