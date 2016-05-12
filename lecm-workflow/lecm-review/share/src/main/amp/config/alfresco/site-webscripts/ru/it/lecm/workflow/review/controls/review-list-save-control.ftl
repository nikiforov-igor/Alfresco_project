<#assign formId = args.htmlid + '-form'>
<#assign controlId = fieldHtmlId>
<#assign buttonId = fieldHtmlId + '-btn-save'>

<div id='${controlId}' class='control review-list-save hidden'>
	<div class='label-div'></div>
	<div class='container'>
		<div class='buttons-div'>
			<span id='${buttonId}' class='yui-button'>
				<span class='first-child'>
					<input type='button'>
				</span>
			</span>
		</div>
		<div class='value-div'>
		</div>
	</div>
</div>
<div class='clear'></div>
<script type='text/javascript'>//<![CDATA[
	(function () {

		function initReviewListSaveControl() {
			new LogicECM.module.Review.ReviewList.SaveControl('${controlId}', {
				reviewListDictionary: 'Списки ознакомления',
				buttonSaveLabelId: '&nbsp;Сохранить список&nbsp;'
			}, ${messages});
		}

		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-review/review-list-save-control.js'
		],[
//			'css/lecm-review/review-list-save-control.css'
		], initReviewListSaveControl);
	})();
//]]></script>
