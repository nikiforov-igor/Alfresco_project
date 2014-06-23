<#list set.children as item>
	<#if item.kind != "set">
	<div class="attach-to-document">
		<@formLib.renderField field=form.fields[item.id]/>
	</div>
	</#if>
	<script>
		(function() {
			LogicECM.module.Base.Util.loadCSS([
				'css/lecm-documents/attach-to-document-set.css'
			]);
		})();
	</script>
</#list>