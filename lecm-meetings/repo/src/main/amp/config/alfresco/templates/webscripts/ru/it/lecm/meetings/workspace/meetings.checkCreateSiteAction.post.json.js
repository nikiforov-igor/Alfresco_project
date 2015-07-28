function main() {
	var isInitOrSec = false;
	var isApproved = false;
	
	var meeting = null;
	if (json.has("dataTableRef")){
		var dataTableRef = json.get("dataTableRef");
		if (dataTableRef){
			var dataTable = search.findNode(dataTableRef);
			meeting = documentTables.getDocumentByTableData(dataTable);
		}	
	}
	
	if (json.has("itemRef")){
		var itemRef = json.get("itemRef");
		if (itemRef){
			var item = search.findNode(itemRef);
			meeting = documentTables.getDocumentByTableDataRow(item);
		}
	}
	
	var initiators = meeting.associations["lecm-events:initiator-assoc"];
	var secretaries= meeting.associations["lecm-meetings:secretary-assoc"];
	var employee = orgstructure.getCurrentEmployee();
	if (initiators){
		if (employee.id.equals(initiators[0].id)){
			isInitOrSec = true;			
		}	
	}
	if (secretaries && !isInitOrSec){
		if (employee.id.equals(secretaries[0].id)){
			isInitOrSec = true;			
		}	
	}

	var approveAgenda = meeting.properties["lecm-meetings:approve-agenda"];
	if (!approveAgenda){
		var currentIter = routesService.getDocumentCurrentIteration(meeting);
		if (currentIter){
			var approvalResult = currentIter.properties["lecmApproveAspects:approvalDecision"];	
			if (approvalResult){
				if ("APPROVED".equals(approvalResult) || "APPROVED_WITH_REMARK".equals(approvalResult) || "APPROVED_FORCED".equals(approvalResult)){
					isApproved = true;
				}
			}
		}
	} else{
		isApproved = true;
	}			
		
	model.isInitOrSec =  isInitOrSec;
	model.isApproved =  isApproved;
}

main();