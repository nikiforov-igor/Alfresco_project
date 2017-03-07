<style>
	.categoriesContainer,
	.emptyContainer {
		border: 1px solid lightgray;
		padding: 0.3em 1em 0.3em 1em;
		margin: 0em 1em 1em 1em;
	}

	.categoryDescription > h2 {
		font-weight: bold;
		font-size: 110%;
	}

	.attachmentsContainer {
		padding-left: 2em;
	}

	.attachmentDescription {
		margin: 0.3em 0em 0em 0em;
		list-style-type: disc !important;
	}
</style>
<#if categories??>
<div class='categoriesContainer'>
	<#list categories as category>
	<div class='categoryContainer'>
		<div class='categoryDescription'>
			<h2>${category.displayName}</h2>
		</div>
 		<#if category.attachments??>
		<div class='attachmentsContainer'>
			<ul>
			<#list category.attachments as attachment>
				<li class='attachmentDescription'>
					<span>
						<a href='${shareContext}/page/document-attachment?nodeRef=${attachment.nodeRef}'>${attachment.displayName}</a>
					</span>
					<#-- <span style='float: right; clear: both;'>2 МБ</span> -->
				</li>
			</#list>
			<ul>
		</div>
		</#if>
	</div>
	<#if category_has_next><hr style='height: 1px !important'></#if>
	</#list>
</div>
<#else>
<div class='emptyContainer'>
	<h1>${msg("no.categories")}</h1>
</div>
</#if>
