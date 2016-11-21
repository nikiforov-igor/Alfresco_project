<#assign id = args.htmlid>

<div class="dashlet document bordered approval">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentMetadataComponent.onExpandTab('approvalTab')" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
		</span>
        <span class="lecm-dashlet-actions-right">
	        <select id="${id}-approval-status" class="approval-status-select">
                <option value="current" selected="selected">${msg("label.approval.dashlet.status.current")}</option>
                <option value="finished">${msg("label.approval.dashlet.status.finished")}</option>
                <option value="all">${msg("label.approval.dashlet.status.all")}</option>
	        </select>
	    </span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results"></div>
</div>

<script type="text/javascript">//<![CDATA[
	(function(){
        function init() {
            LogicECM.module.Base.Util.loadResources([
                        'scripts/dashlets/lecm-approval-dashlet.js'
                    ],
                    [
                        'css/lecm-documents/lecm-approval-dashlet.css'
                    ], createControl);
        }
        function createControl() {
            var control = new LogicECM.module.Documents.Approval.Dashlet("${id}").setMessages(${messages});
            control.setOptions({
	            approvalInfo: ${approval}
            });
        }

        YAHOO.util.Event.onDOMReady(init);
	})();
//]]></script>