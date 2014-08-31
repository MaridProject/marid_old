/*
 * Copyright (C) 2014 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.bd.schema;

import images.Images;
import org.marid.bd.BlockComponent;
import org.marid.bd.shapes.LinkShape;
import org.marid.bd.shapes.LinkShapeEvent;
import org.marid.ide.components.BlockMenuProvider;
import org.marid.l10n.L10nSupport;
import org.marid.swing.AbstractFrame;
import org.marid.swing.SwingUtil;
import org.marid.swing.menu.MenuActionList;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import static java.awt.Color.RED;
import static java.awt.SystemColor.infoText;
import static java.lang.String.format;
import static javax.swing.BorderFactory.*;

/**
 * @author Dmitry Ovchinnikov
 */
public class SchemaFrame extends AbstractFrame implements SchemaFrameConfiguration, L10nSupport {

    protected final SchemaEditor schemaEditor = new SchemaEditor(this);
    protected final JLayer<SchemaEditor> layer = new JLayer<>(schemaEditor, new SchemaEditorLayerUI());
    protected final JMenu blocksMenu = new JMenu(s("Blocks"));

    public SchemaFrame(BlockMenuProvider blockMenuProvider) {
        super("Schema");
        enableEvents(AWTEvent.COMPONENT_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
        centerPanel.add(layer);
        getContentPane().setBackground(getBackground());
        getJMenuBar().add(blocksMenu);
        blockMenuProvider.fillMenu(blocksMenu);
        pack();
    }

    protected void fireEvent(AWTEvent event) {
        layer.getUI().eventDispatched(event, layer);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        switch (e.getID()) {
            case WindowEvent.WINDOW_OPENED:
                schemaEditor.start();
                break;
            case WindowEvent.WINDOW_CLOSED:
                schemaEditor.stop();
                break;
        }
    }

    @Override
    protected void fillActions(MenuActionList actionList) {
        actionList.add("main", "Schema");
        actionList.add(true, "zoom", "Zoom in", "Schema")
                .setKey("control I")
                .setIcon("zoomin")
                .setListener(e -> schemaEditor.zoomIn());
        actionList.add(true, "zoom", "Zoom out", "Schema")
                .setKey("control I")
                .setIcon("zoomout")
                .setListener(e -> schemaEditor.zoomOut());
        actionList.add(true, "zoom", "Reset zoom", "Schema")
                .setKey("control R")
                .setIcon("zoom")
                .setListener(e -> schemaEditor.resetZoom());
    }

    protected class SchemaEditorLayerUI extends LayerUI<SchemaEditor> {

        private JLabel tooltip = null;

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);
            if (tooltip != null) {
                final Rectangle bounds = tooltip.getBounds();
                if (bounds.getMaxX() > schemaEditor.getWidth()) {
                    tooltip.setLocation(schemaEditor.getWidth() - bounds.width, tooltip.getY());
                }
                if (bounds.getMaxY() > schemaEditor.getHeight()) {
                    tooltip.setLocation(tooltip.getX(), schemaEditor.getHeight() - bounds.height);
                }
                try {
                    g.translate(tooltip.getX(), tooltip.getY());
                    tooltip.print(g);
                } finally {
                    g.translate(-tooltip.getX(), -tooltip.getY());
                }
            }
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            ((JLayer) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK);
        }

        @Override
        public void uninstallUI(JComponent c) {
            super.uninstallUI(c);
            ((JLayer) c).setLayerEventMask(0L);
        }

        @Override
        public void eventDispatched(AWTEvent e, JLayer<? extends SchemaEditor> l) {
            super.eventDispatched(e, l);
            switch (e.getID()) {
                case MouseEvent.MOUSE_ENTERED:
                    if (e.getSource() instanceof BlockComponent.Input) {
                        final BlockComponent.Input input = (BlockComponent.Input) e.getSource();
                        final Point point = ((MouseEvent) e).getLocationOnScreen();
                        SwingUtilities.convertPointFromScreen(point, schemaEditor);
                        showTooltip(format("%s: %s", input.getInput().getName(), input.getInput().getInputType().getCanonicalName()), point);
                    } else if (e.getSource() instanceof BlockComponent.Output) {
                        final BlockComponent.Output output = (BlockComponent.Output) e.getSource();
                        final Point point = ((MouseEvent) e).getLocationOnScreen();
                        SwingUtilities.convertPointFromScreen(point, schemaEditor);
                        showTooltip(format("%s: %s", output.getOutput().getName(), output.getOutput().getOutputType().getCanonicalName()), point);
                    }
                    break;
                case MouseEvent.MOUSE_EXITED:
                    if (e.getSource() instanceof BlockComponent.Input || e.getSource() instanceof BlockComponent.Output) {
                        hideTooltip();
                    }
                    break;
                default:
                    if (e instanceof LinkShapeEvent) {
                        switch (e.getID()) {
                            case LinkShapeEvent.MOUSE_ENTERED:
                                final LinkShapeEvent event = (LinkShapeEvent) e;
                                final LinkShape link = event.getSource();
                                if (!link.isValid()) {
                                    showError(m("Types mismatch: {0} -> {1}",
                                            link.getOutputType().getCanonicalName(),
                                            link.getInputType().getCanonicalName()), event.getPoint());
                                }
                                break;
                            case LinkShapeEvent.MOUSE_EXITED:
                                hideTooltip();
                                break;
                        }
                    }
                    break;
            }
        }

        public void hideTooltip() {
            if (tooltip != null) {
                tooltip = null;
                layer.repaint();
            }
        }

        public void showTooltip(String text, Point point) {
            showMessage(Images.getIcon("info.png"), SystemColor.info, text, point);
        }

        public void showError(String text, Point point) {
            showMessage(Images.getIcon("warning.png"), RED.brighter().brighter(), text, point);
        }

        public void showMessage(ImageIcon icon, Color color, String text, Point point) {
            final JLabel label = new JLabel(text, icon, SwingConstants.LEFT);
            label.setOpaque(true);
            label.setForeground(infoText);
            label.setBackground(SwingUtil.color(color, 200));
            label.setBorder(createCompoundBorder(createEtchedBorder(), createEmptyBorder(3, 3, 3, 3)));
            label.setLocation(point.x + 10, point.y + 10);
            label.setSize(label.getPreferredSize());
            tooltip = label;
            layer.repaint();
        }
    }
}
