 var docs = contracts.getContractsByFilters(args.dateFilter, args.userFilter);
 var strRefs = [];
 for (var index in docs) {
	 var nodeRef = "" + docs[index]; // возвращаем строку - чтобы потом не возникли проблемы с преобразованием
	 var node = search.findNode(nodeRef);
	 var activeMyTasks = statemachine.getTasks(node, "active", false, 0);

     strRefs.push({
	     nodeRef: nodeRef,
	     hasMyActiveTasks: activeMyTasks != null && activeMyTasks.myTasks != null && activeMyTasks.myTasks.size() > 0
     });
 }
 model.docs = strRefs;