package ru.it.lecm.documents.beans;

import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.VersionService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.base.beans.BaseBean;
import ru.it.lecm.base.beans.SubstitudeBean;
import ru.it.lecm.dictionary.beans.DictionaryBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

/**
 * User: PMelnikov
 * Date: 01.07.15
 * Time: 16:03
 */

public class DocumentStampServiceImpl extends BaseBean implements DocumentStampService {

    private static final transient Logger logger = LoggerFactory.getLogger(DocumentStampServiceImpl.class);

    private DictionaryBean dictionaryService;
    private ContentService contentService;
    private VersionService versionService;
    private SubstitudeBean substitudeService;

    public void setDictionaryService(DictionaryBean dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }

    public void setSubstitudeService(SubstitudeBean substitudeService) {
        this.substitudeService = substitudeService;
    }

    @Override
    public NodeRef getServiceRootFolder() {
        return null;
    }

    @Override
    public HashMap<String, Object> getStamp(NodeRef document, String code) {
        NodeRef stamp = dictionaryService.getDictionaryValueByParam("Штампы", PROP_CODE, code);
        if (stamp == null) {
            return null;
        }

        HashMap<String, Object> result = new HashMap<>();
        int stampWidth = (Integer) nodeService.getProperty(stamp, PROP_WIDTH);
        int stampHeight = (Integer) nodeService.getProperty(stamp, PROP_HEIGHT);
        float stampWidthPoints = Utilities.millimetersToPoints(stampWidth);
        float stampHeightPoints = Utilities.millimetersToPoints(stampHeight);
        float pageWidth = 0;
        float pageHeight = 0;

        PdfReader pdfReader = null;
        ContentReader reader = contentService.getReader(document, ContentModel.PROP_CONTENT);
        try (InputStream contentIS = reader.getContentInputStream()){
            pdfReader = new PdfReader(contentIS);
            Rectangle rect = pdfReader.getPageSizeWithRotation(1);
            pageWidth = rect.getWidth();
            pageHeight = rect.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pdfReader != null) {
                pdfReader.close();
            }
        }

        String prevStamps = (String) nodeService.getProperty(document, PROP_PREV_STAMPS);

        if (reader.getSize() > 0) {
            result.put(STRUCT_PROP_STAMP, stamp.toString());
        }
        result.put(STRUCT_PROP_PAGE_WIDTH, pageWidth);
        result.put(STRUCT_PROP_PAGE_HEIGHT, pageHeight);
        result.put(STRUCT_PROP_STAMP_WIDTH, stampWidthPoints);
        result.put(STRUCT_PROP_STAMP_HEIGHT, stampHeightPoints);
        result.put(STRUCT_PROP_DOCUMENT, document.toString());

        result.put(STRUCT_PROP_PREV_STAMPS, prevStamps);

        return result;
    }

    @Override
    public void drawStamp(NodeRef document, NodeRef attach, NodeRef stamp, int x, int y, int width, int height, int page) {
        drawStamp(document, attach, stamp, x, y, width, height, page, null);
    }

    public void drawStamp(NodeRef document, NodeRef attach, NodeRef stamp, int x, int y, int width, int height, int page, List<String> additionalStrings) {
        ContentReader documentReader = contentService.getReader(attach, ContentModel.PROP_CONTENT);
        ContentReader stampReader = contentService.getReader(stamp, ContentModel.PROP_CONTENT);
        ContentWriter documentWriter = contentService.getWriter(attach, ContentModel.PROP_CONTENT, true);

        versionService.ensureVersioningEnabled(attach, null);

        PdfStamper pdfStamper = null;
        try (InputStream docIs = documentReader.getContentInputStream(); InputStream stampIs = stampReader.getContentInputStream(); OutputStream docOs = documentWriter.getContentOutputStream()) {
            PdfReader pdfReader = new PdfReader(docIs);
            pdfStamper = new PdfStamper(pdfReader, docOs);
            Rectangle pageRect = pdfReader.getPageSizeWithRotation(page);

            int mmStampWidth = (Integer) nodeService.getProperty(stamp, PROP_WIDTH);
            int mmStampHeight = (Integer) nodeService.getProperty(stamp, PROP_HEIGHT);
            float stampWidth = Utilities.millimetersToPoints(mmStampWidth);
            float stampHeight = Utilities.millimetersToPoints(mmStampHeight);
            float pageWidth = pageRect.getWidth();
            float pageHeight = pageRect.getHeight();
            float scale = pageWidth / width;
            float xPoint = x * scale;
            float yPoint = pageHeight - stampHeight - (y * scale);
            PdfContentByte content = pdfStamper.getOverContent(page);
            //image
            if (stampReader.getSize() > 0) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int c;
                byte[] buf = new byte[1024 * 8];
                while ((c = stampIs.read(buf)) != -1) {
                    baos.write(buf, 0, c);
                }

                //BufferedImage tmp_image = ImageIO.read(stampIs);
                //BufferedImage image = new BufferedImage(tmp_image.getWidth(), tmp_image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                //image.createGraphics().drawRenderedImage(tmp_image, null);
                Image pdfImage = Image.getInstance(baos.toByteArray());
                pdfImage.setAbsolutePosition(xPoint, yPoint);
                pdfImage.scaleToFit(stampWidth, stampHeight);
                content.addImage(pdfImage);//, stampWidth, 0, 0, stampHeight, xPoint, yPoint);
            }

            //text
            String substitudeString = (String) nodeService.getProperty(stamp, DocumentStampService.PROP_SUBSTITUDE_STRING);
            if (StringUtils.isNotEmpty(substitudeString)) {
                /*if (!FontFactory.isRegistered("Times New Roman")) {
                    FontFactory.register("fonts/Times_New_Roman.ttf");
                }*/
                BaseFont bf = BaseFont.createFont("fonts/Times_New_Roman.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

                int fontSize = (Integer) nodeService.getProperty(stamp, DocumentStampService.PROP_FONT_SIZE);
                float leading = 1.0f * fontSize;

                String hAlign = (String) nodeService.getProperty(stamp, DocumentStampService.PROP_HORIZONTAL_ALIGN);
                String vAlign = (String) nodeService.getProperty(stamp, DocumentStampService.PROP_VERTICAL_ALIGN);

                String text = null;
                if (additionalStrings != null && additionalStrings.size() > 0) {
                    try {
                        MessageFormat formatter = new MessageFormat(substitudeString);
                        text = formatter.format(additionalStrings.toArray());
                    } catch (Exception ex) {
                        logger.error("Cannot apply formatter. Redirect to substitute", ex);
                    }
                }
                if (text == null) {
                    text = substitudeService.formatNodeTitle(document, substitudeString);
                }

                String[] lines = text.split("<br>");

                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    float textWidth = bf.getWidth(line) / 1000 * fontSize;

                    float textX = xPoint;
                    if (ALIGN_RIGHT.equals(hAlign)) {
                        //right
                        textX = xPoint + stampWidth - textWidth;
                    } else if (ALIGN_CENTER.equals(hAlign)) {
                        //center
                        textX = xPoint + (stampWidth - textWidth) / 2;
                    }

                    float textY = yPoint;
                    if (ALIGN_TOP.equals(vAlign)) {
                        //top
                        textY = yPoint + stampHeight - ((i + 1) * leading);
                    } else if (ALIGN_BOTTOM.equals(vAlign)) {
                        //bottom
                        textY = yPoint + (leading * (lines.length - i - 1));
                    } else if (ALIGN_MIDDLE.equals(vAlign)) {
                        //middle
                        textY = yPoint + (stampHeight - (leading * lines.length)) / 2 + (leading * (lines.length - i - 1));
                    }

                    content.beginText();
                    content.setFontAndSize(bf, fontSize);
                    content.moveText(textX, textY);
                    content.showText(line);
                    content.endText();
                }
            }
            String prevStamps = (String) nodeService.getProperty(attach, PROP_PREV_STAMPS);
            JSONArray prevStampsArray;
            if (StringUtils.isNotEmpty(prevStamps)) {
                prevStampsArray = new JSONArray(prevStamps);
            } else {
                prevStampsArray = new JSONArray();
            }
            JSONObject stampJson = new JSONObject();
            stampJson.put("x", x * scale);
            stampJson.put("y", y * scale);
            stampJson.put("p", page);
            stampJson.put("width", stampWidth);
            stampJson.put("height", stampHeight);
            stampJson.put("docVersion", versionService.getCurrentVersion(attach).getVersionLabel());

            prevStampsArray.put(stampJson);
            nodeService.setProperty(attach, PROP_PREV_STAMPS, prevStampsArray.toString());

            pdfStamper.close();

            versionService.createVersion(attach, null);
        } catch (Exception e) {
            logger.error("Cannot create stamp", e);
        } finally {
            if (pdfStamper != null) {
                try {
                    pdfStamper.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void clearPreviousStampInfo(NodeRef attach) {
        if (nodeService.hasAspect(attach, DocumentStampService.ASPECT_PREV_STAMPS)) {
            nodeService.setProperty(attach, PROP_PREV_STAMPS, "");
        }
    }
}
