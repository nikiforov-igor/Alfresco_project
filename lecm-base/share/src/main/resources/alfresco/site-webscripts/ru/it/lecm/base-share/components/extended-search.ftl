<div id="searchBlock" style="display: none;">
	<h2 id="${id}-heading" class="thin dark">
	${msg("search-block")}
	</h2>
	<div id="${id}-searchContainer" class="search">
		<div class="yui-gc form-row">
		<#-- search button -->
			<div class="yui-u align-right">
                    <span id="${id}-search-button-1" class="yui-button yui-push-button search-icon">
                        <span class="first-child">
                        <button type="button">${msg('button.search')}</button>
                        </span>
                    </span>
                    <span id="${id}-clear-button" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button">${msg('button.clear')}</button>
                        </span>
                    </span>
			</div>
		</div>

	<#-- keywords entry box - DIV structure mirrors a generated Form to collect the correct styles -->
		<div class="forms-container keywords-box">
			<div class="share-form">
				<div class="form-container">
					<div class="form-fields">
						<div class="set">
							<div>${msg("label.keywords")}:</div>
							<input type="text" class="terms" name="${id}-search-text" id="${id}-search-text"
							       value="${(page.url.args["st"]!"")?html}" maxlength="1024"/>
						</div>
					</div>
				</div>
			</div>
		</div>
	<#-- container for forms retrieved via ajax -->
		<div id="${id}-forms" class="forms-container form-fields"></div>

		<div class="yui-gc form-row">
			<div class="yui-u first"></div>
		<#-- search button -->
			<div class="yui-u align-right">
                    <span id="${id}-search-button-2" class="yui-button yui-push-button search-icon">
                        <span class="first-child">
                            <button type="button">${msg('button.search')}</button>
                        </span>
                    </span>
			</div>
		</div>
	</div>
	<script type="text/javascript">//<![CDATA[
	Alfresco.util.createTwister("${id}-heading", "OrgstructureSearch");
	//]]></script>
</div>