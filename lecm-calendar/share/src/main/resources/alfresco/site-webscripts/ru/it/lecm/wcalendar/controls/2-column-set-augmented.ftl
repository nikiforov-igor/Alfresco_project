<style>
	.lecm-2-column-set-augmented label {
		width: auto !important;
	}

	.lecm-2-column-set-augmented .yui-g .yui-u {
		width: 40%;
		float: left;
	}
</style>

<div class="lecm-2-column-set-augmented">
	<#list set.children as item>
	   <#if item.kind != "set">
		  <#if (item_index % 2) == 0>
		  <div class="yui-g"><div class="yui-u first">
		  <#else>
		  <div class="yui-u">
		  </#if>
		  <@formLib.renderField field=form.fields[item.id] />
		  </div>
		  <#if ((item_index % 2) != 0) || !item_has_next></div></#if>
	   </#if>
	</#list>
</div>
<div id="error-message-container-set"></div>