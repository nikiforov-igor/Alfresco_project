 var docs = contracts.getContractsByFilters(args.dateFilter, args.userFilter);
 var strRefs = [];
 for (var index in docs) {
     strRefs.push("" + docs[index]); // возвращаем строку - чтобы потом не возникли проблемы с преобразованием
 }
 model.docs = strRefs;