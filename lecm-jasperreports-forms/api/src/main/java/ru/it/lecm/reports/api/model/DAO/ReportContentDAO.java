package ru.it.lecm.reports.api.model.DAO;

import java.io.InputStream;

import org.alfresco.service.cmr.repository.ContentReader;

import ru.it.lecm.reports.api.model.ReportDescriptor;
import ru.it.lecm.reports.model.impl.ReportType;
import ru.it.lecm.reports.model.impl.SubReportDescriptorImpl;

/**
 * Хранилище файлов для службы построения отчётов.
 * Уникальный ключ для файла: Тип отчёта, Название отчёта, Название файла. 
 * 
 * @author rabdullin
 */
/**
 * Служба хранения файлов, шаблонов и др контента, связанного с развёрнутыми отчётами.
 * Атом хранения - файл или шаблон связанный с каким-либо отчётом.
 * Базовый объект типа "cm:content" имеет "cm:name" как ключ хранения.
 * Иерахия хранения:
 *   1. папка службы ()
 *      2. папка "Тип отчёта" (reportType)
 *         3. папка "Отчёт" (reportMnemo)
 *            4. [Файл/Контент] Название + данные
 * [4] название уникально для своего отчёта
 *
 * @author rabdullin
 *
 */
public interface ReportContentDAO {

	/**
	 * @return true, если хранилище только для чтения
	 */
	boolean isReadonly();

	/**
	 * @param value true, если хранилище только для чтения
	 */
	void setReadonly(boolean value);

	/**
	 * проверить существование объекта
	 */
	boolean exists(IdRContent id);

	/**
	 * Удалить контент, если он существует, иначе ничего не происходит.
	 * <br/> (!) При readonly = true поднимается исключение.
	 * <br/> (!) Если файл id.fileName указан как "*", то удаляется весь каталог id.reportMnemo;
	 */
	void delete(IdRContent id); // throws java.io.IOException;

	/**
	 * Загрузить данные контента. Если объекта не существует - вернуть NULL.
	 * @param id ид для контента/файла
	 * @return
	 *  // NOTE: исключение FileNotFoundException НЕ выбрасывается  
	 */
	ContentReader loadContent(IdRContent id);

	/**
	 * Сохранить объект. Если одноимённый уже существует - переписать.
	 */
	void storeContent(IdRContent id, InputStream stm);

	/**
	 * Просканировать все автомарные объекты (файлы) в хранилище.
	 * @param enumerator интерфейс обратного вызова, если null, то будет 
	 * выполнен просто подсчёт имеющихся файлов
	 * @return количество остканированных объектов 
	 */
	int scanContent( ContentEnumerator enumerator);

	/**
	 * Вернуть id рутового объекта (для репозитория это NodeRef для файлов - файловый путь root)
	 */
	String getRoot();

	/** IdContent: id для определения положения файлов в иерархии хранения */
	public static class IdRContent {

		/**
		 * Используется для fileName, когда надо обозначить независимость от имени файла,
		 * например, чтобы обозначить целиком весь каталог /reportMnemo 
		 */
		final public static String FILENAME_WILDCARD_ANYFILE = "*";

		private ReportType reportType;
		private String reportMnemo;
		private String fileName;

		/**
		 * Создание id по трём его составляющим
		 * @param fileName имя файла, если пустое или null, то вместо него используется маска "*"
		 */
		public static IdRContent createId( ReportType reportType, String reportMnemo, String fileName) {
			if (fileName == null || fileName.trim().length() == 0)
				fileName = FILENAME_WILDCARD_ANYFILE;
			return new IdRContent(reportType, reportMnemo, fileName.trim());
		}

		/**
		 * Создать id указанного файла отчёта
		 * @param desc описатель, если null, воз-ся Null
		 * @param fileName имя файла, если пустое или null, то вместо него используется маска "*"
		 */
		public static IdRContent createId( ReportDescriptor desc, String fileName) {
            if (desc == null) {
                return null;
            }
            if (!(desc instanceof SubReportDescriptorImpl)) {
                return createId(desc.getReportType(), desc.getMnem(), fileName);
            } else{
                // для подотчетов, если тип не задан - берем из родительского отчета
                ReportType reportType = desc.getReportType();
                if (reportType == null) {
                    reportType = ((SubReportDescriptorImpl) desc).getOwnerReport().getReportType();
                }
                return createId(reportType, desc.getMnem(), fileName);
            }
		}

		public IdRContent(ReportType reportType, String reportMnemo, String fileName)
		{
			super();
			this.reportType = reportType;
			this.reportMnemo = reportMnemo;
			this.fileName = fileName;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("IdContent [ type '").append(reportType).append("'");
			builder.append(", reportMnemo '").append(reportMnemo).append("'");
			builder.append(", fileName '").append(fileName).append("'");
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((reportType == null) ? 0 : reportType.hashCode());
			result = prime * result
					+ ((reportMnemo == null) ? 0 : reportMnemo.hashCode());
			result = prime * result
					+ ((fileName == null) ? 0 : fileName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;

			final IdRContent other = (IdRContent) obj;
			if (reportType == null) {
				if (other.reportType != null)
					return false;
			} else if (!reportType.equals(other.reportType))
				return false;

			if (reportMnemo == null) {
				if (other.reportMnemo != null)
					return false;
			} else if (!reportMnemo.equals(other.reportMnemo))
				return false;

			if (fileName == null) {
				if (other.fileName != null)
					return false;
			} else if (!fileName.equals(other.fileName))
				return false;

			return true;
		}

		public ReportType getReportType() {
			return reportType;
		}

		public void setReportType(ReportType reportType) {
			this.reportType = reportType;
		}

		public String getReportMnemo() {
			return reportMnemo;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}

	/**
	 * Интерфейс обратного вызова для перечислителей объектов хранилища
	 */
	public interface ContentEnumerator {
		void lookAtItem(IdRContent id);
	}
}
