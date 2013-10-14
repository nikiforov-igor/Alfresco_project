package ru.it.lecm.reports.model.DAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.URLDecoder;

import ru.it.lecm.reports.api.model.ReportType;
import ru.it.lecm.reports.api.model.DAO.ReportContentDAO;
import ru.it.lecm.reports.beans.ReportsManagerImpl;
import ru.it.lecm.reports.model.impl.ReportTypeImpl;
import ru.it.lecm.reports.utils.Utils;

public class FileReportContentDAOBean implements ReportContentDAO {

	/*
     * Обозначение макро-ссылок на отдельные элементы id
	 * Используется три автоматических значения для макро-подстановки:
	 *   1) на тип отчета
	 *   2) на код (мнемонику) отчёта
	 *   3) на имя файла.
	 * Каждое значение может быть подставлено в трёх вариантах: 
	 *   1) либо как есть, 
	 *   2) либо значение в нижнем регистре, 
	 *   3) либо в верхнем. 
	 */

	/* Как есть ==============================================================*/
    /**
     * Макрос для подстановки "Тип отчёта" как есть (без изменения регистра)
     */
    private static final String MACRO_RTYPE = "@reporType";

    /**
     * Макрос для подстановки "Мнемоники/кода отчёта" как есть
     */
    private static final String MACRO_RMNEM = "@reportMnemo";

    /**
     * Макрос для подстановки "Названия файла" как есть
     */
    private static final String MACRO_FNAME = "@fileName";

	/* Нижний регистр ========================================================*/
    /**
     * Макрос для подстановки "Тип отчёта" в НИЖНЕМ регистре
     */
    private static final String MACRO_RTYPE_LO = "@reporTypeLo";

    /**
     * Макрос для подстановки "Мнемоники/кода отчёта" в НИЖНЕМ регистре
     */
    private static final String MACRO_RMNEM_LO = "@reportMnemoLo";

    /**
     * Макрос для подстановки "Названия файла" в НИЖНЕМ регистре
     */
    private static final String MACRO_FNAME_LO = "@fileNameLo";

	/* Верхний регистр =======================================================*/
    /**
     * Макрос для подстановки "Тип отчёта" в ВЕРХНЕМ регистре
     */
    private static final String MACRO_RTYPE_UP = "@reporTypeUp";

    /**
     * Макрос для подстановки "Мнемоники/кода отчёта" в ВЕРХНЕМ регистре
     */
    private static final String MACRO_RMNEM_UP = "@reportMnemoUp";

    /**
     * Макрос для подстановки "Названия файла" в ВЕРХНЕМ регистре
     */
    private static final String MACRO_FNAME_UP = "@fileNameUp";

    /**
     * формат по-умолчанию для формирования пути к файлу, относительно root-каталога
     */
    private static final String DEFAULT_STORE_STRUC_FMT
            = MACRO_RTYPE + "/" + MACRO_RMNEM + "/" + MACRO_FNAME;

	/* Умолчания и значения ==================================================*/
    /**
     * используемое значения, когда исходное NULL
     */
    private static final String DEFAULT_NULL_FMT = MACRO_FNAME; // для формата - использовать только название файла
    private static final String EMPTY_REPORT_TYPE_DIR = ReportsManagerImpl.DEFAULT_REPORT_TYPE; // для пустого типа отчёта
    private static final String EMPTY_REPORT_MNEM_DIR = "default"; // для пустой мнемоники (кода) отчёта

    private static final transient Logger logger = LoggerFactory.getLogger(FileReportContentDAOBean.class);

    /**
     * флаг запрета записи: true = запрещено, false = разрешено
     */
    private boolean readonly = false;

    /**
     * корневая папка (относительно ./classes)
     */
    private String rootDir = "/";

    /**
     * формат для получения полного пути файла по его Id
     */
    private String storeStructurePathFmt = DEFAULT_STORE_STRUC_FMT;

	public void init() {
		logger.info( String.format( "initialized as:\n\t %s\t %s\n\t %s\t '%s' -> '%s'\n\t %s\t %s"
				, "readonly", readonly
				, "rootDir", rootDir, getRoot()
				, "structure format", storeStructurePathFmt
		));
	}

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public String getRoot() {
        return makeAbsRootFilePath().getAbsolutePath();
    }

    /**
     * корневая папка (относительно ./classes)
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * корневая папка (относительно ./classes)
     */
    public void setRootDir(String rootDir) {
        this.rootDir = Utils.nonblank(rootDir, "/");

        // добавление в начале строки обязательного разделителя каталогов
        if (!this.rootDir.startsWith("/")) {
            this.rootDir = "/" + this.rootDir;
        }
    }

    /**
     * форматная строка для формирования полного пути к файлу (относительно rootDir)
     * Можно использовать поля из класса IdContent. Например:
     * "/@reporType/@reportMnemo/@fileName"
     * (см также makeAbsFile и DEFAULT_STORE_STRUC_FMT)
     */
    public String getStoreStructurePathFmt() {
        return storeStructurePathFmt;
    }

    public void setStoreStructurePathFmt(String storeStructurePathFmt) {
        this.storeStructurePathFmt = Utils.coalesce(storeStructurePathFmt, DEFAULT_NULL_FMT);
    }

    /**
     * Гарантировать наличие родительских каталогов указанного файла
     *
     * @param f
     */
    static void ensureParents(File f) {
        if (f == null) {
            return;
        }
        final File parent = f.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }

    /**
     * Returns the resource URL for a specified resource name.
     *
     * @param resource the resource name
     * @return the URL of the resource having the specified name, or <code>null</code> if none found
     * @see ClassLoader#getResource(String)
     */
    public static URL getResource(String resource) {
        URL resultLocation = null;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            resultLocation = classLoader.getResource(resource);
        }

        if (resultLocation == null) {
            classLoader = FileReportContentDAOBean.class.getClassLoader();
            if (classLoader != null) {
                resultLocation = classLoader.getResource(resource);
            }

            if (resultLocation == null) {
                resultLocation = IdRContent.class.getResource("/" + resource);
            }
        }

        return resultLocation;
    }


    /**
     * Получить название базового каталога в файловой системе (".")
     *
     * @return абсолютный файл корня классов
     */
    private File getSysRootDir() {
        final URL base = getResource("/"); // получение главного каталога
        return new File(URLDecoder.decode(base.getFile()));// NPE скажет о проблемах, т.к. базовый каталог обязан существовать
    }

    public File makeAbsRootFilePath() {
        return new File(getSysRootDir().getAbsolutePath() + rootDir + "/");
    }

    /**
     * По указанному id файла вернуть его полный абсолютный путь.
     *
     * @param id
     * @return
     */
    public File makeAbsFilePath(IdRContent id) {
        return (id != null) ? makeAbsFilePath(id.getReportType(), id.getReportMnemo(), id.getFileName()) : null;
    }

    public File makeAbsFilePath(final ReportType rtype, final String mnem, final String fileName) {
        final String stype = Utils.nonblank((rtype == null) ? ReportsManagerImpl.DEFAULT_REPORT_TYPE : rtype.getMnem(), EMPTY_REPORT_TYPE_DIR);
        final String smnem = Utils.nonblank(mnem, EMPTY_REPORT_MNEM_DIR);
        final String sname = Utils.nonblank(fileName, "");

		/* NOTE: преобразования должны проводиться так, чтобы максросы,
         * отрабатываемые раньше, не включали текстуально себя в макросы, которые
		 * будут работать после, иначе подстановка выполниться некорректно (раньше 
		 * и с другими подставляеммыми значениями).
		 */
        final String relPath = storeStructurePathFmt
                // для нижнего регистра ...
                .replaceAll(MACRO_RTYPE_LO, stype.toLowerCase())
                .replaceAll(MACRO_RMNEM_LO, smnem.toLowerCase())
                .replaceAll(MACRO_FNAME_LO, sname.toLowerCase())

                        // для верхнего регистра ...
                .replaceAll(MACRO_RTYPE_UP, stype.toUpperCase())
                .replaceAll(MACRO_RMNEM_UP, smnem.toUpperCase())
                .replaceAll(MACRO_FNAME_UP, sname.toUpperCase())

                        // для обычных значений ...
                .replaceAll(MACRO_RTYPE, stype)
                .replaceAll(MACRO_RMNEM, smnem)
                .replaceAll(MACRO_FNAME, sname);

        // все пути относительно class-path: getSysRootDir() и далее rootDir ...
        return new File(makeAbsRootFilePath().getAbsolutePath() + "/" + relPath);
    }

    /**
     * Если запрет записи - поднять исключение
     */
    private void checkWriteable(IdRContent id, String operTag) {
        if (isReadonly()) {
            throw new RuntimeException(String.format("Cannot %s by id={%s} due to Readonly-mode", operTag, id));
        }
    }

    @Override
    public boolean exists(IdRContent id) {
        final File f = makeAbsFilePath(id);
        return (f != null) && f.exists();
    }

    @Override
    public ContentReader loadContent(IdRContent id) {
        final File f = makeAbsFilePath(id);
        if (f == null || !f.exists())
            // NOTE: (?) throws FileNotFoundException
            return null;
        try {
            final URL fileUrl = f.toURI().toURL();
			final ContentReader result = new org.alfresco.repo.content.filestore.FileContentReader(f, fileUrl.toString());
			return result;
        } catch (MalformedURLException ex) {
            final String msg = String.format("Read error:\n\t file '%s'\n\t by id {%s}\n\t Error %s"
                    , f.getAbsolutePath(), id.toString(), ex.getMessage());
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    @Override
    public void delete(IdRContent id) {
        if (id == null) {
            return;
        }

        checkWriteable(id, "delete");

        if ("*".equals(id.getFileName())) {
            // удаление полного каталога отчёта ...
            final File fDirReport = makeAbsFilePath(id.getReportType(), id.getReportMnemo(), "");
            try {
                FileUtils.deleteDirectory(fDirReport);
				logger.info( String.format( "Delete directory by id=[%s]:\n\t directory deleted: '%s'", id, fDirReport.getAbsolutePath()));
            } catch (IOException ex) {
                final String msg = String.format("Fail to delete template file directory\n\t by id='%s'\n\t %s", id, ex.getMessage());
                logger.error(msg, ex);
                throw new RuntimeException(msg, ex);
            }
            return;
        }

        final File f = makeAbsFilePath(id);
        if (f == null) {
            return;
        }

        if (!f.exists()) {
            logger.warn(String.format("Ignored file deletion by id=[%s]:\n\t file not exists: '%s'", id, f.getAbsolutePath()));
            return;
        }

        final boolean ok = f.delete();
        if (ok) {
            logger.info(String.format("Delete file by id=[%s]:\n\t file deleted: '%s'", id, f.getAbsolutePath()));
        } else {
            logger.info(String.format("Delete file by id=[%s]:\n\t (!) file NOT deleted: '%s'", id, f.getAbsolutePath()));
        }
    }

    @Override
    public void storeContent(IdRContent id, InputStream instm) {
        checkWriteable(id, "store");
        // (?) сохранить прежний
        final File f = makeAbsFilePath(id);
        ensureParents(f);

        FileOutputStream outstm = null;
        try {
            outstm = new FileOutputStream(f);
            IOUtils.copy(instm, outstm);
        } catch (IOException ex) {
            final String msg = String.format("Read error:\n\t file '%s'\n\t by id {%s}\n\t Error %s"
                    , f.getAbsolutePath(), id.toString(), ex.getMessage());
            logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        } finally {
            IOUtils.closeQuietly(outstm);
        }
    }

    @Override
    public int scanContent(final ContentEnumerator enumerator) {
        // проходим по всем типа, отчётам и файлам ...

		/*
		 * Иерахия хранения:
		 *   1. RootDir
		 *      2. [lev==1] папки конкретного "Типа отчёта" (reportType)
		 *            [lev==2] 3. папка "Отчёт" (reportMnemo)
		 *               [lev==3] 4. [Файл/Контент] Название + данные 
		 *               здесь название файла должно быть уникально для своего отчёта
		 */
        int result = 0;
        final File rootDir = makeAbsRootFilePath();
        final File[] ftypes = rootDir.listFiles();
        if (ftypes != null) {
			/* просмотр "Типов отчётов" ... */
            for (final File flType : ftypes) {
                if (!flType.isDirectory() || flType.getAbsolutePath().startsWith(".")) {
                    // пропуск не каталогов и "."/".."
                    continue;
                }

                final ReportType rtype = new ReportTypeImpl(flType.getName());
                final File[] freports = flType.listFiles();
                if (freports == null) {
                    continue;
                }

				/* просмотр Отчётов ... */
                for (final File flReport : freports) {
                    if (!flReport.isDirectory() || flReport.getAbsolutePath().startsWith(".")) {
                        // пропуск не каталогов и "."/".."
                        continue;
                    }

                    final String rmnem = flReport.getName();
                    final File[] frepoFiles = flReport.listFiles();
                    if (frepoFiles == null) {
                        continue;
                    }

					/* просмотр Файлов ... */
                    for (final File file : frepoFiles) {
                        if (file.isDirectory()) {
                            // пропуск каталогов
                            continue;
                        }

                        result++;

						/* Обратный вызов для каждого файла ... */
                        if (enumerator != null) {
                            final String rFileName = file.getName();
                            final IdRContent id = IdRContent.createId(rtype, rmnem, rFileName);
                            enumerator.lookAtItem(id);
                        }
                    } // for k
                } // for j
            } // for i
        }
        return result;
    }
}
