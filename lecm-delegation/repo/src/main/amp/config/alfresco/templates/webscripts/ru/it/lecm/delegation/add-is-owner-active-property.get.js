(function () {
    var delegationOptsList = delegation.getDelegationOptsContainer().getChildren();
    var delegationOpts, owner;
    for (var i in delegationOptsList) {
        delegationOpts = delegationOptsList[i];
        logger.log('Processing item #' + i + ' with ref: ' + delegationOpts.nodeRef);
        owner = delegationOpts.assocs['lecm-d8n:delegation-opts-owner-assoc'][0];
        delegationOpts.properties['lecm-d8n:is-owner-employee-exists'] = owner.properties['lecm-dic:active'];
        delegationOpts.save();
    }

    model.data = jsonUtils.toJSONString({'processed_elements_count': delegationOptsList.length});
})();
