var representative = contractorsRootObject.getMainRepresentative(args["contractor"]);
if (representative) {
    model.result = representative.nodeRef.toString();
}
