<div class="fio-and-photo">
	<#list set.children as item>
	    <#if item_index == 0>
	        <div class="yui-g"><div class="yui-u first">
	    <#elseif item_index == 3>
	        </div> <#-- "yui-u first" end -->
	        <div class="yui-u">
	    </#if>
	    <@formLib.renderField field=form.fields[item.id] />
	</#list>
	</div> <#-- second "yui-u" end -->
	</div> <#-- "yui-g" end -->

	<script>
		(function() {
			LogicECM.module.Base.Util.loadCSS([
				'css/lecm-base/components/fio-and-photo.css'
			]);
		})();
	</script>
</div>