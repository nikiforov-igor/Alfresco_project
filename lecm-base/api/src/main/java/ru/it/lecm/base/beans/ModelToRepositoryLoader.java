package ru.it.lecm.base.beans;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryListener;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.springframework.extensions.surf.util.I18NUtil;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.transaction.TransactionService;

/**
 * Размещает модели данных в репозитории, где они впоследствии подхватываются альфреской
 * <p/>
 * Created by AZinovin on 12.12.13.
 */
public class ModelToRepositoryLoader implements DictionaryListener {

    private boolean useDefaultModels = false;
    private List<String> models = new ArrayList<String>();
    private List<String> messages = new ArrayList<String>();
	private TransactionService transactionService;

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

    private RepositoryLocation repositoryModelsLocation;

    private DictionaryDAO dictionaryDAO = null;

    private LecmModelsService lecmModelsService;

    private boolean firstRun = true;

    public void setUseDefaultModels(String useDefaultModels) {
        this.useDefaultModels = Boolean.valueOf(useDefaultModels);
    }

    public void setLecmModelsService(LecmModelsService lecmModelsService) {
        this.lecmModelsService = lecmModelsService;
    }

    public void setRepositoryModelsLocation(RepositoryLocation repositoryModelsLocation) {
        this.repositoryModelsLocation = repositoryModelsLocation;
    }

    public void setDictionaryDAO(DictionaryDAO dictionaryDAO) {
        this.dictionaryDAO = dictionaryDAO;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public void setLabels(List<String> labels) {
        this.messages = labels;
    }

    /**
     * Размещение моделей в репозиторий
     */
    public void init() {

        onDictionaryInit();
        initStaticMessages();

        register();
    }

    /**
     * Register with the Dictionary
     */
    public void register() {
        dictionaryDAO.register(this);
    }

    @Override
    public void onDictionaryInit() {
        if (repositoryModelsLocation == null) {
            return;
        }
//        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
//            @Override
//            public Object doWork() throws Exception {
//                                //Вызывается только в init-методе
//				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>(){
//
//					@Override
//					public Object execute() throws Throwable {
						// регистрируем модели
						for (final String bootstrapModel : models) {
							lecmModelsService.loadModelFromLocation(bootstrapModel, firstRun && useDefaultModels);
						}
//						return null;
//					}
//
//				}, false);
//            }
//        });
        //firstRun = false;
    }

    @Override
    public void afterDictionaryDestroy() {

    }

    @Override
    public void afterDictionaryInit() {

    }

    /**
     * Register the static resource bundles
     */
    private void initStaticMessages()
    {
        // register messages
        for (String resourceBundle : messages)
        {
            I18NUtil.registerResourceBundle(resourceBundle);
        }
    }
}
