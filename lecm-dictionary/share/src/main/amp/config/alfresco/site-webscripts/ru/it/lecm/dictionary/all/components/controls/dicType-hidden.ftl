<#include "/ru/it/lecm/base-share/components/controls/hidden.ftl">

<#assign dialogId = args.htmlid>

<script type='application/javascript'>
(function() {
	LogicECM.module.Base.Util.reInitializeControl('${dialogId}', 'lecm-dic:attributeForShow', {
		webscript: 'lecm/dictionary/attributes?dataType=${fieldValue}'
	});
})();
</script>
