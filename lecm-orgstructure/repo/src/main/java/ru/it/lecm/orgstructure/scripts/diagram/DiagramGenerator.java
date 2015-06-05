package ru.it.lecm.orgstructure.scripts.diagram;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * User: pmelnikov
 * Date: 28.05.13
 * Time: 13:54
 */
public class DiagramGenerator{
	private static final transient Logger logger = LoggerFactory.getLogger(DiagramGenerator.class);

    private OrgstructureBean service;
    private NodeService nodeService;

    public InputStream generate(OrgstructureBean service, NodeService nodeService) {
        this.service = service;
        this.nodeService = nodeService;

        mxGraph graph = new mxGraph() {
            public void drawState(mxICanvas canvas, mxCellState state,
                                  boolean drawLabel) {
                OrgstructureUnit unit = null;

                if (state.getCell() != null && ((mxCell) state.getCell()).getValue() instanceof OrgstructureUnit) {
                    unit = (OrgstructureUnit) ((mxCell) state.getCell()).getValue();
                }

                // Indirection for wrapped swing canvas inside image canvas (used for creating
                // the preview image when cells are dragged)
                if (unit != null && getModel().isVertex(state.getCell())
                        && canvas instanceof mxSvgCanvas) {
                    mxSvgCanvas svgCanvas = (mxSvgCanvas) canvas;
                    unit.draw(state, svgCanvas);

                } else {
                    super.drawState(canvas, state, drawLabel);
                }
            }
        };

        Object parent = graph.getDefaultParent();
        graph.setAutoSizeCells(true);

        graph.getModel().beginUpdate();
        try {

            NodeRef organization = service.getOrganization();
            Object orgName = nodeService.getProperty(organization, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME);
            if (orgName == null) {
                orgName = nodeService.getProperty(organization, OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME);
                if (orgName == null) {
                    orgName = nodeService.getProperty(organization, ContentModel.PROP_NAME);
                }
            }
            Object orgObject = graph.insertVertex(parent, null, new OrgstructureUnit(orgName.toString()), 10, 10, 10, 10);

            List<NodeRef> children = service.getSubUnits(service.getStructureDirectory(), true);
            for(NodeRef child : children) {
                createStructure(graph, orgObject, child);
            }

        } finally {
            mxCompactTreeLayout layout = new mxCompactTreeLayout(graph) {

                @Override
                public void execute(Object parent) {
                    mxIGraphModel model = graph.getModel();
                    int childCount = model.getChildCount(parent);
                    for (int i = 0; i < childCount; i++) {
                        Object cell = model.getChildAt(parent, i);

                        if (model.isVertex(cell) && graph.isCellVisible(cell)) {
                            mxCell cellObj = (mxCell) cell;
                            OrgstructureUnit unit = (OrgstructureUnit) cellObj.getValue();
                            cellObj.getGeometry().setWidth(unit.getWidth());
                            cellObj.getGeometry().setHeight(unit.getHeight());
                        }
                    }
                    super.execute(parent);
                }
            };
            layout.setHorizontal(false);
            layout.setLevelDistance(120);
            layout.execute(parent);
            graph.getModel().endUpdate();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 0.05, Color.WHITE, false, null, new OrgstructureSwingCanvas());
        Document document = mxCellRenderer.createSvgDocument(graph, null, 1, Color.WHITE, null);
        try {
            DOMSource domSource = new DOMSource(document);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult sr = new StreamResult(baos);
            transformer.transform(domSource, sr);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(baos.toByteArray());

/*
        BufferedImage result = new BufferedImage(image.getWidth() + 10, image.getHeight() + 10, BufferedImage.TYPE_INT_RGB);
        Graphics g = result.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, result.getWidth(), result.getHeight());
        g.drawImage(image, 5, 5, null);
        try {
            ImageIO.write(result, "png", baos);
        } catch (IOException e) {
	        logger.error(e.getMessage(), e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
*/

    }

    private void createStructure(mxGraph graph, Object parent, NodeRef structure) {
        Object orgName = nodeService.getProperty(structure, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME);
        if (orgName == null) {
            orgName = nodeService.getProperty(structure, OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME);
            if (orgName == null) {
                orgName = nodeService.getProperty(structure, ContentModel.PROP_NAME);
            }
        }
        OrgstructureUnit unit = new OrgstructureUnit(orgName.toString());

        List<NodeRef> positions = service.getUnitStaffLists(structure);
        for (NodeRef position : positions) {
            NodeRef employee = service.getEmployeeByPosition(position);
            String positionName = nodeService.getProperty(service.getPositionByStaff(position), ContentModel.PROP_NAME).toString();
            if (employee != null) {
                Object name = nodeService.getProperty(employee, OrgstructureBean.PROP_EMPLOYEE_SHORT_NAME);

                if (name == null) {
                    name = nodeService.getProperty(employee, ContentModel.PROP_NAME);
                }

                boolean isBoss = (Boolean) nodeService.getProperty(position, OrgstructureBean.PROP_STAFF_LIST_IS_BOSS);
                if (isBoss) {
                    unit.setBoss(name + ", " + positionName);
                } else {
                    unit.addEmployee(name + ", " + positionName);
                }
            }

        }

        Object unitObject = graph.insertVertex(graph.getDefaultParent(), null, unit, 0, 0, 0, 0);
        graph.insertEdge(graph.getDefaultParent(), null, "", parent, unitObject);

        List<NodeRef> children = service.getSubUnits(structure, true);
        if (children.size() > 0) {
            for (NodeRef child : children) {
                createStructure(graph, unitObject, child);
            }
        }
    }

}
