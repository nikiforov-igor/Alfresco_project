[#ftl]
[#assign id=args.htmlid?html/]

[#assign delegator]
	[#if page.url.args["delegator"]??]
		${page.url.args["delegator"]}
	[#else/]
		this is me!!!
	[/#if]
[/#assign]

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
