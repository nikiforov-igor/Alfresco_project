function main()
{
	var nodeRef = json.get('nodeRef');
	var value = json.get('value');

	//if (nodeRef != null) {
	meetings.setApproovementRequired(nodeRef, value);
	//}
	
}

main();
