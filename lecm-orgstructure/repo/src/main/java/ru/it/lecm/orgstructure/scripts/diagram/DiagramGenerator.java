package ru.it.lecm.orgstructure.scripts.diagram;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import ru.it.lecm.orgstructure.beans.OrgstructureBean;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

            NodeRef organization = service.getOrganization();
            Object orgName = nodeService.getProperty(organization, OrgstructureBean.PROP_ORG_ELEMENT_FULL_NAME);
            if (orgName == null) {
                orgName = nodeService.getProperty(organization, ContentModel.PROP_NAME);
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
            layout.execute(parent);
            graph.getModel().endUpdate();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null, new OrgstructureSwingCanvas());
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

        List<NodeRef> children = service.getSubUnits(structure, true);
        if (children.size() > 0) {
            for (NodeRef child : children) {
                createStructure(graph, unitObject, child);
            }
        }
    }

    public class OrgstructureSwingCanvas extends mxInteractiveCanvas {
        protected CellRendererPane rendererPane = new CellRendererPane();

        protected JLabel vertexRenderer = new JLabel();

        protected JScrollPane graphComponent = new JScrollPane();

        public OrgstructureSwingCanvas() {
            vertexRenderer.setBorder(BorderFactory
                    .createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            vertexRenderer.setHorizontalAlignment(JLabel.LEFT);
            vertexRenderer.setVerticalAlignment(JLabel.TOP);
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
