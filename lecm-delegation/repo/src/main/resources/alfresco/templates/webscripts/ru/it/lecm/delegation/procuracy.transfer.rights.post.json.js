logger.log (jsonUtils.toJSONString (json));
var procuracyRef = json.get("procuracyRef");
var delegationOptsRef = json.get("delegationOptsRef");
var result = delegation.transferRights(procuracyRef, delegationOptsRef);
model.procuracyRef  = procuracyRef;
model.delegationOptsRef  = delegationOptsRef;
model.status = result ? 'flag "can transfer rights" actualized' : 'flag "can transfer rights" not actualized because of invalid parameters';
