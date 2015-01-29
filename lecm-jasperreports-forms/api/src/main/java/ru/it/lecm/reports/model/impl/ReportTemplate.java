package ru.it.lecm.reports.model.impl;

import org.apache.commons.io.IOUtils;
import ru.it.lecm.reports.api.model.L18able;
import ru.it.lecm.reports.api.model.Mnemonicable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class ReportTemplate extends MnemonicNamedItem implements Mnemonicable, L18able, Serializable {
    private static final long serialVersionUID = 1L;


    private String fileName;
    // NOTE: тут вполне может быть проксик для реальной загрузки данных только во время первовго требования порции данных ("load-on-demand")
    private byte[] data;

    private ReportType reportType;

    public ReportTemplate() {
        super();
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getData() {
        return (this.data != null) ? new ByteArrayInputStream(this.data) : null;
    }

    public void setData(InputStream stm) {
        if (stm == null) {
            this.data = null;
        } else {
            try {
                this.data = IOUtils.toByteArray(stm);
            } catch (IOException e) {
                throw new RuntimeException("Fail to get data bytes from input stream", e);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReportTemplate [ ");
        builder.append(String.format("fileName= '%s'", fileName));
        builder.append(", data ");
        builder.append(data == null ? "null" : data.length + " bytes");
        builder.append(", ").append(super.toString());
        builder.append("]");
        return builder.toString();
    }

    public ReportType getReportType() {
        return this.reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
}
