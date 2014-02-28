package ru.it.lecm.base.utils;

/**
 *
 * @author snovikov
 */
public class BaseAlgorithmUtils implements IBaseAlgorithmUtils {

    private LecmBaseUtilsService lecmBaseUtilsService;

    public LecmBaseUtilsService getLecmBaseUtilsService() {
        return lecmBaseUtilsService;
    }

    public void setLecmBaseUtilsService(LecmBaseUtilsService LecmBaseUtilsService) {
        this.lecmBaseUtilsService = LecmBaseUtilsService;
    }

    @Override
    public <T, E> T getDocumentsOnStatusAmmount(AtomicWork<T> worker, E context) {
        if (lecmBaseUtilsService.checkProperties(null, null)) {
            return worker.execute(context);
        } else {
            return null;
        }    
    }

}
