<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/documents-reports.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
</@>

<@markup id="html">
	<#assign el=args.htmlid/>
	
	<div class="widget-bordered-panel">
	    <div class="document-components-panel">
	        <h2 id="${el}-heading" class="not-active">
	            ${msg("heading")}
	        </h2>
	
	        <div id="${el}-formContainer">
                    <#if reportsDescriptors?? && (reportsDescriptors?size > 0)>
                        <ul id="document-reports-set" class="document-reports-set document-right-set">
                            <#assign i=0/>
                            <#escape x as x?js_string>
                                <#list reportsDescriptors as report>
                                    <li class="text-broken">
                                            <a href="#" class="theme-color-1"
                                               onClick='LogicECM.module.Documents.Reports.reportLinkClicked(this, {"reportCode": "${report.code}", "nodeRef":"${nodeRef}"});'><#if report.name != "">${report.name}<#else>(no name)</#if></a>
                                    </li>
                                </#list>
                            </#escape>
                        </ul>
                    </#if>
	        </div>
	
	        <script type="text/javascript">//]]>
	        	(function(){
		            var Dom = YAHOO.util.Dom,
		                Event = YAHOO.util.Event;
		
		            function init() {
			            var formContainerChildren = Dom.getChildren("${el}-formContainer");
			            if (formContainerChildren != null && formContainerChildren.length > 0) {
				            var forms = Dom.getChildren(formContainerChildren[0]);

				            if (forms.length > 0) {
					            var heading = Dom.get("${el}-heading");

					            Dom.removeClass(heading, "not-active");
					            Alfresco.util.createTwister(heading, "DocumentForms");
				            }
			            }
		            }
		
		            Event.onDOMReady(init);
	        	})();
	          //]]></script>
	    </div>
	</div>
</@>