package ru.it.lecm.orgstructure.scripts.diagram;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.OutputStream;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 28.05.13
 * Time: 13:54
 */
public class DiagramGenerator{

    private OrgstructureBean service;
    private NodeService nodeService;

    public void generate(OutputStream baos, OrgstructureBean service, NodeService nodeService, NodeRef rootRef) {
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
            if (rootRef == null) {
                rootRef = service.getOrganization();
            }
            createStructure(graph, null, rootRef);
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
    }

    private void createStructure(mxGraph graph, Object parent, NodeRef structure) {
        String orgName = (String) nodeService.getProperty(structure, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME);
        if (StringUtils.isEmpty(orgName)) {
            orgName = (String) nodeService.getProperty(structure, OrgstructureBean.PROP_ORG_ELEMENT_SHORT_NAME);
            if (StringUtils.isEmpty(orgName)) {
                orgName = (String) nodeService.getProperty(structure, ContentModel.PROP_NAME);
            }
        }
        OrgstructureUnit unit = new OrgstructureUnit(orgName);

        List<NodeRef> positions = service.getUnitStaffLists(structure);
        for (NodeRef position : positions) {
            NodeRef employee = service.getEmployeeByPosition(position);
            NodeRef positionByStaff = service.getPositionByStaff(position);
            String positionName = (String) nodeService.getProperty(positionByStaff, ContentModel.PROP_TITLE);
            if (StringUtils.isEmpty(positionName)) {
                positionName = (String) nodeService.getProperty(positionByStaff, ContentModel.PROP_NAME);
            }
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
        if (parent != null) {
            graph.insertEdge(graph.getDefaultParent(), null, "", parent, unitObject);
        }

        List<NodeRef> children = service.getSubUnits(structure, true);
        if (children.size() > 0) {
            for (NodeRef child : children) {
                createStructure(graph, unitObject, child);
            }
        }
    }

}
