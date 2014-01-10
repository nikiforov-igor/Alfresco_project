package ru.it.lecm.base.beans;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryListener;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.springframework.extensions.surf.util.I18NUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Размещает модели данных в репозитории, где они впоследствии подхватываются альфреской
 * <p/>
 * Created by AZinovin on 12.12.13.
 */
public class ModelToRepositoryLoader implements DictionaryListener {

    private boolean useDefaultModels = false;
    private List<String> models = new ArrayList<String>();
    private List<String> messages = new ArrayList<String>();

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
        AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                // регистрируем модели
                for (final String bootstrapModel : models) {
                    lecmModelsService.loadModelFromLocation(bootstrapModel, firstRun && useDefaultModels);
                }
                return null;
            }
        });
        firstRun = false;
    }

    public void afterDictionaryDestroy() {

    }

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
