var defaultSubject = errands.getDefaultSubject();
if (defaultSubject != null) {
	model.nodeRef = defaultSubject.map(function (subj) {return subj.nodeRef}).join(",");
}