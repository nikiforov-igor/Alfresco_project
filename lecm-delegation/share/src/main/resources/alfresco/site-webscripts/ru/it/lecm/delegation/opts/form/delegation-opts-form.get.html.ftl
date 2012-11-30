[#ftl]
[#assign id = args.htmlid/]

[#assign hasDelegator = args.delegator??/]
[#assign delegator]
	[#if hasDelegator]
		${args.delegator}
	[#else/]
		""
	[/#if]
[/#assign]

[#if hasDelegator]
<div id="${id}-dialog" class="delegation-opts">
	<div class="hd">${msg("delegation.opts.form.title")}</div>
	<div class="bd">
		<form id="${id}-form">
			<div class="yui-g">
				<h2>${delegator}</h2>
			</div>
			<div class="bdft">
				<input type="submit" id="${id}-ok" value="${msg("delegation.opts.form.button.ok")}"/>
				<input type="submit" id="${id}-cancel" value="${msg("delegation.opts.form.button.cancel")}"/>
			</div>
		</form>
	</div>
</div>
[#else/]
<h2>There is no delegator specified, form won't be show</h2>
[/#if]