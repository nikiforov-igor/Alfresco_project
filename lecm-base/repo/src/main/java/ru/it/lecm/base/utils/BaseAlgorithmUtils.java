package ru.it.lecm.base.utils;

import org.alfresco.service.cmr.repository.NodeRef;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author snovikov
 */
public class BaseAlgorithmUtils extends BaseBean implements IBaseAlgorithmUtils {

    private LecmBaseUtilsService lecmBaseUtilsService;

    public LecmBaseUtilsService getLecmBaseUtilsService() {
        return lecmBaseUtilsService;
    }

    public void setLecmBaseUtilsService(LecmBaseUtilsService lecmBaseUtilsService) {
        this.lecmBaseUtilsService = lecmBaseUtilsService;
    }

    @Override
    public <T, E> T getDocumentsOnStatusAmmount(AtomicWork<T> worker, E context) {
        if (lecmBaseUtilsService.checkProperties(null, null)) {
            return worker.execute(context);
        } else {
            return null;
        }    
    }

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

}
