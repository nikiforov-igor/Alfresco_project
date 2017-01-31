var errand = search.findNode(args['nodeRef']);

if (errand) {
    model.report = errands.getAcceptedExecutorReport(errand);
}