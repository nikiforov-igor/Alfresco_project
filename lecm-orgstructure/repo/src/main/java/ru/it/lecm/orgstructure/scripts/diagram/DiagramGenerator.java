package ru.it.lecm.orgstructure.scripts.diagram;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

/**
 * User: pmelnikov
 * Date: 28.05.13
 * Time: 13:54
 */
public class DiagramGenerator{

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
                if (getModel().isVertex(state.getCell())
                        && canvas instanceof mxImageCanvas
                        && ((mxImageCanvas) canvas).getGraphicsCanvas() instanceof OrgstructureSwingCanvas) {
                    ((OrgstructureSwingCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas())
                            .drawVertex(state, unit);
                }
                // Redirection of drawing vertices in SwingCanvas
                else if (getModel().isVertex(state.getCell())
                        && canvas instanceof OrgstructureSwingCanvas) {
                    ((OrgstructureSwingCanvas) canvas).drawVertex(state, unit);
                } else {
                    super.drawState(canvas, state, drawLabel);
                }
            }
        };

        Object parent = graph.getDefaultParent();
        graph.setAutoSizeCells(true);

        graph.getModel().beginUpdate();
        try {

            NodeRef organization = service.getOrganizationRootRef();
            String orgName = nodeService.getProperty(organization, ContentModel.PROP_NAME).toString();
            Object orgObject = graph.insertVertex(parent, null, new OrgstructureUnit(orgName), 10, 10, 10, 10);

            HashSet<QName> qnames = new HashSet<QName>();
            qnames.add(OrgstructureBean.TYPE_ORGANIZATION_UNIT);
            List<ChildAssociationRef> children = nodeService.getChildAssocs(service.getStructureDirectory(), qnames);
            for(ChildAssociationRef child : children) {
                createStructure(graph, orgObject, child.getChildRef());
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
            layout.execute(parent);
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph) {
            private static final long serialVersionUID = 4683716829748931448L;

            public mxInteractiveCanvas createCanvas() {
                return new OrgstructureSwingCanvas(this);
            }

        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null, new OrgstructureSwingCanvas(graphComponent));
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(baos.toByteArray());

    }

    private void createStructure(mxGraph graph, Object parent, NodeRef structure) {
        String title = nodeService.getProperty(structure, ContentModel.PROP_NAME).toString();
        OrgstructureUnit unit = new OrgstructureUnit(title);

        List<NodeRef> positions = service.getUnitStaffLists(structure);
        for (NodeRef position : positions) {
            NodeRef employee = service.getEmployeeByPosition(position);
            String positionName = nodeService.getProperty(service.getPositionByStaff(position), ContentModel.PROP_NAME).toString();
            if (employee != null) {
                String name = nodeService.getProperty(employee, ContentModel.PROP_NAME).toString();

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

        HashSet<QName> qnames = new HashSet<QName>();
        qnames.add(OrgstructureBean.TYPE_ORGANIZATION_UNIT);
        List<ChildAssociationRef> children = nodeService.getChildAssocs(structure, qnames);
        if (children.size() > 0) {
            for (ChildAssociationRef child : children) {
                createStructure(graph, unitObject, child.getChildRef());
            }
        }
/*
        OrgstructureUnit unit1 = new OrgstructureUnit("Департамент истребления насекомых");
        unit1.setBoss("Жабин И.А., директор");
        unit1.addEmployee("Иванов И.И., осенизатор");
        unit1.addEmployee("Петров П.П., химик");
        unit1.addEmployee("Васечкин И.И., стрелок");
        unit1.addEmployee("Никифоров В.В, ассистент");

        Object v2 = graph.insertVertex(parent, null, unit1, 240, 150,
                80, 30);
        Object v3 = graph.insertVertex(parent, null, new OrgstructureUnit("Департамент истребления насекомых"), 240, 150,
                80, 30);
        Object v4 = graph.insertVertex(parent, null, new OrgstructureUnit("Департамент истребления насекомых"), 240, 150,
                80, 30);
        Object v5 = graph.insertVertex(parent, null, new OrgstructureUnit("О4"), 240, 150,
                80, 30);
        graph.insertEdge(parent, null, "", v1, v3);
        graph.insertEdge(parent, null, "", v1, v4);
        graph.insertEdge(parent, null, "", v4, v5);
*/

    }

    public class OrgstructureSwingCanvas extends mxInteractiveCanvas {
        protected CellRendererPane rendererPane = new CellRendererPane();

        protected JLabel vertexRenderer = new JLabel();

        protected mxGraphComponent graphComponent;

        public OrgstructureSwingCanvas(mxGraphComponent graphComponent) {
            this.graphComponent = graphComponent;

            vertexRenderer.setBorder(BorderFactory
                    .createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            vertexRenderer.setHorizontalAlignment(JLabel.LEFT);
            vertexRenderer.setBackground(graphComponent.getBackground()
                    .brighter());
            vertexRenderer.setOpaque(true);

            vertexRenderer.setFont(OrgstructureUnit.FONT);
        }

        public void drawVertex(mxCellState state, OrgstructureUnit unit) {
            if (unit == null) return;
            vertexRenderer.setText(unit.getHtml());

            // TODO: Configure other properties...

            rendererPane.paintComponent(g, vertexRenderer, graphComponent,
                    (int) state.getX() + translate.x, (int) state.getY()
                    + translate.y, (int) state.getWidth(),
                    (int) state.getHeight(), true);
        }

    }

}
