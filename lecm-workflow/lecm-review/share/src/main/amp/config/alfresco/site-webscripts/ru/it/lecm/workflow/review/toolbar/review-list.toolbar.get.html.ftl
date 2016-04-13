<#import '/ru/it/lecm/base-share/components/base-components.ftl' as comp/>

<#assign bubblingLabel = 'review-list'/>
<#assign toolbarId = args.htmlid + '-review-list-toolbar'/>

<div id='${toolbarId}'>
	<@comp.baseToolbar toolbarId true true true>
		<div class='create-row'>
			<span id='${toolbarId}-newRowButton' class='yui-button yui-push-button'>
				<span class='first-child'>
					<button type='button'>${msg('logicecm.review-list.add-element')}</button>
				</span>
			</span>
		</div>

        <script type='text/javascript'>
            (function () {
                function initOrganizationsToolbar() {
                    new LogicECM.module.Review.ReviewList.Toolbar('${toolbarId}', {
                        bubblingLabel: '${bubblingLabel}'
                    }, ${messages});
                }

                LogicECM.module.Base.Util.loadResources([
                    'scripts/lecm-base/components/lecm-toolbar.js',
                    'scripts/lecm-review/review-list-toolbar.js'
                ], [
                    'components/data-lists/toolbar.css'
                ], initOrganizationsToolbar);
            })();
        </script>
	</@>
</div>


