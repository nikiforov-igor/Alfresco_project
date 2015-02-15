<script type="text/javascript">//<![CDATA[
	LogicECM = LogicECM || {};
	LogicECM.Nomenclature = LogicECM.Nomenclature || {};
	LogicECM.Nomenclature.isArchivist = ${isArchivist?string};
	LogicECM.Nomenclature.isCentralized = ${isCentralized?string};
//]]></script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePageSimple showToolbar=false>
	<#if isArchivist>
		<div class="yui-t1 nomenclature" id="lecm-dictionary">
			<@region id="toolbar" scope="template" />
			<@region id="main-form" scope="template" />
		</div>
	<#else>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>
