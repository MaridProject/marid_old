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

package org.marid.swing;

import images.Images;
import org.marid.logging.LogSupport;
import org.marid.pref.PrefSupport;
import org.marid.swing.forms.FrameConfigurationDialog;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.prefs.Preferences;

import static java.awt.BorderLayout.NORTH;
import static java.util.Arrays.copyOfRange;
import static javax.swing.SwingConstants.HORIZONTAL;
import static org.marid.l10n.L10n.s;

/**
 * @author Dmitry Ovchinnikov
 */
public abstract class AbstractMultiFrame extends AbstractFrame implements LogSupport {

    protected final JPanel centerPanel = new JPanel(new BorderLayout());
    protected final JToolBar toolBar;
    protected final MultiFrameDesktop desktop;
    protected final JMenuBar menuBar;

    public AbstractMultiFrame(String title) {
        super(title);
        setJMenuBar(menuBar = new JMenuBar());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        centerPanel.add(toolBar = new JToolBar(getPref("tOrientation", HORIZONTAL)), getPref("tPosition", NORTH));
        centerPanel.add(desktop = new MultiFrameDesktop());
        add(centerPanel);
        toolBar.setBorderPainted(true);
        menuBar.add(widgetsMenu());
        menuBar.add(new JSeparator(JSeparator.VERTICAL));
        doActions();
    }

    private JMenuItem menuItem(String title, String icon, String cmd, ActionListener actionListener) {
        final JMenuItem item = new JMenuItem(s(title), Images.getIcon(icon, 16));
        item.setActionCommand(cmd);
        item.addActionListener(actionListener);
        return item;
    }

    private JMenu widgetsMenu() {
        final JMenu widgetsMenu = new JMenu(s("Widgets"));
        final ActionListener widgetsListener = e -> {
            try {
                final InternalFrame[] frames = desktop.getAllFrames();
                switch (e.getActionCommand()) {
                    case "cascade":
                        for (int i = 0; i < frames.length; i++) {
                            frames[i].setIcon(false);
                            frames[i].setLocation(i * 20, i * 20);
                            frames[i].setSize(frames[i].getPreferredSize());
                        }
                        break;
                    case "tileVertical":
                        for (int i = 0; i < frames.length; i++) {
                            frames[i].setIcon(false);
                            frames[i].setLocation(0, (desktop.getHeight() / frames.length) * i);
                            frames[i].setSize(desktop.getWidth(), desktop.getHeight() / frames.length);
                        }
                        break;
                    case "tileHorizontal":
                        for (int i = 0; i < frames.length; i++) {
                            frames[i].setIcon(false);
                            frames[i].setLocation((desktop.getWidth() / frames.length) * i, 0);
                            frames[i].setSize(desktop.getWidth() / frames.length, desktop.getHeight());
                        }
                        break;
                    case "configuration":
                        new FrameConfigurationDialog(this).setVisible(true);
                        break;
                    case "fullscreen":
                        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        final GraphicsDevice gd = ge.getDefaultScreenDevice();
                        if (gd.isFullScreenSupported()) {
                            setVisible(false);
                            try {
                                setUndecorated(!isUndecorated());
                                gd.setFullScreenWindow(AbstractMultiFrame.this);
                            } finally {
                                setVisible(true);
                            }
                        } else {
                            warning("Fullscreen mode is not supported on {0}", gd);
                        }
                        break;
                }
            } catch (Exception x) {
                warning("{0} error", x, e.getActionCommand());
            }
        };
        widgetsMenu.add(menuItem("Cascade", "cascade16.png", "cascade", widgetsListener));
        widgetsMenu.add(menuItem("Tile vertical", "tileVertical16.png", "tileVertical", widgetsListener));
        widgetsMenu.add(menuItem("Tile horizontal", "tileHorizontal16.png", "tileHorizontal", widgetsListener));
        widgetsMenu.addSeparator();
        widgetsMenu.add(widgetListMenu());
        widgetsMenu.addSeparator();
        widgetsMenu.add(menuItem("Configuration...", "configuration16.png", "configuration", widgetsListener));
        return widgetsMenu;
    }

    private JMenu widgetListMenu() {
        final JMenu widgetListMenu = new JMenu(s("Widget list"));
        widgetListMenu.setIcon(Images.getIcon("widgetList16.png", 16));
        widgetListMenu.setActionCommand("widgetList");
        widgetListMenu.getPopupMenu().addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                for (final InternalFrame frame : desktop.getAllFrames()) {
                    final ActionListener frameActionListener = event -> {
                        try {
                            switch (event.getActionCommand()) {
                                case "maximize":
                                    frame.setMaximum(true);
                                    break;
                                case "minimize":
                                    frame.setIcon(true);
                                    break;
                                case "normalize":
                                    frame.setIcon(false);
                                    frame.setMaximum(false);
                                    frame.setLocation(0, 0);
                                    frame.setSize(frame.getPreferredSize());
                                    break;
                            }
                        } catch (PropertyVetoException x) {
                            // skip
                        }
                    };
                    final JMenu item = new JMenu(frame.getTitle());
                    item.setIcon(frame.getFrameIcon());
                    item.addActionListener(e1 -> frame.show());
                    item.add(menuItem("Minimize", "minimize16.png", "minimize", frameActionListener));
                    item.add(menuItem("Maximize", "maximize16.png", "maximize", frameActionListener));
                    item.add(menuItem("Normalize", "normalize16.png", "normalize", frameActionListener));
                    widgetListMenu.add(item);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                widgetListMenu.removeAll();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                widgetListMenu.removeAll();
            }
        });
        return widgetListMenu;
    }

    public void add(JInternalFrame frame) {
        desktop.add(frame);
        frame.show();
    }

    private void doActions() {
        final TreeSet<FrameAction.FrameActionElement> actions = new TreeSet<>();
        final TreeSet<FrameAction.FrameActionElement> toolbarActions = new TreeSet<>();
        for (final Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(FrameAction.class)) {
                final FrameAction fa = method.getAnnotation(FrameAction.class);
                if (fa.path().isEmpty()) {
                    continue;
                }
                final FrameAction.FrameActionElement e = new FrameAction.MethodActionElement(method, fa);
                actions.add(e);
                if (fa.tool()) {
                    toolbarActions.add(e);
                }
            }
        }
        for (final Class<?> klass : getClass().getClasses()) {
            if (klass.isAnnotationPresent(FrameAction.class)) {
                final FrameAction fa = klass.getAnnotation(FrameAction.class);
                if (fa.path().isEmpty()) {
                    continue;
                }
                final FrameAction.FrameActionElement e = new FrameAction.InternalFrameActionElement(klass, fa);
                actions.add(e);
                if (fa.tool()) {
                    toolbarActions.add(e);
                }
            }
        }
        final int start = menuBar.getMenuCount();
        final Map<String, JMenu> menus = new LinkedHashMap<>();
        for (final FrameAction.FrameActionElement action : actions) {
            if (!menus.containsKey(action.getPath())) {
                final String[] path = action.getPath().split("/");
                JMenu menu = null;
                for (int i = start; i < menuBar.getMenuCount(); i++) {
                    final JMenu m = menuBar.getMenu(i);
                    if (m.getActionCommand().equals(path[0])) {
                        menu = m;
                        break;
                    }
                }
                if (menu == null) {
                    menuBar.add(menu = new JMenu(s(path[0])));
                    menu.setActionCommand(path[0]);
                }
                for (String[] p = copyOfRange(path, 1, path.length); p.length > 0; p = copyOfRange(p, 1, p.length)) {
                    JMenu subMenu = null;
                    for (int i = start; i < menu.getMenuComponentCount(); i++) {
                        final JMenu m = (JMenu) menu.getMenuComponent(i);
                        if (m.getActionCommand().equals(p[0])) {
                            subMenu = m;
                            break;
                        }
                    }
                    if (subMenu == null) {
                        if (menu.getMenuComponentCount() == 0) {
                            menu.addSeparator();
                        }
                        menu.add(subMenu = new JMenu(s(p[0])));
                        subMenu.setActionCommand(p[0]);
                    }
                    menu = subMenu;
                }
                menus.put(action.getPath(), menu);
            }
        }
        final NavigableSet<FrameAction.FrameActionElement> descendingActions = actions.descendingSet();
        for (final FrameAction.FrameActionElement e : descendingActions) {
            final JMenu menu = menus.get(e.getPath());
            menu.insert(e.getAction(this), 0);
            final NavigableSet<FrameAction.FrameActionElement> next = descendingActions.tailSet(e, false);
            if (!next.isEmpty()) {
                if (next.first().getPath().equals(e.getPath()) && !next.first().getGroup().equals(e.getGroup())) {
                    menu.insertSeparator(0);
                }
            }
        }
        for (final FrameAction.FrameActionElement e : toolbarActions) {
            toolBar.add(e.getAction(this)).setFocusable(false);
            final NavigableSet<FrameAction.FrameActionElement> next = toolbarActions.tailSet(e, false);
            if (!next.isEmpty()) {
                if (!next.first().getPath().equals(e.getPath()) || !next.first().getGroup().equals(e.getGroup())) {
                    toolBar.addSeparator();
                }
            }
        }
    }

    private String getToolbarPosition() {
        final String position = (String) ((BorderLayout) centerPanel.getLayout()).getConstraints(toolBar);
        return position == null ? BorderLayout.NORTH : position;
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        switch (e.getID()) {
            case WindowEvent.WINDOW_CLOSED:
                for (final JInternalFrame frame : desktop.getAllFrames()) {
                    frame.doDefaultCloseAction();
                }
                putPref("tPosition", getToolbarPosition());
                putPref("tOrientation", toolBar.getOrientation());
                for (final InternalFrame frame : desktop.getAllFrames()) {
                    frame.putPref("location", frame.getLocation());
                }
                break;
            case WindowEvent.WINDOW_OPENED:
                break;
        }
    }

    public void showFrame(Class<?> type) {
        for (final JInternalFrame frame : desktop.getAllFrames()) {
            if (type == frame.getClass()) {
                frame.show();
                return;
            }
        }
        for (final Class<?> frameClass : getClass().getClasses()) {
            if (frameClass.isAnnotationPresent(FrameWidget.class)) {
                if (type == frameClass) {
                    try {
                        final InternalFrame frame = frameClass.isMemberClass()
                                ? (InternalFrame) frameClass.getConstructor(getClass()).newInstance(this)
                                : (InternalFrame) frameClass.newInstance();
                        desktop.add(frame);
                        frame.show();
                    } catch (Exception x) {
                        warning("{0} creating error", x, frameClass.getSimpleName());
                    }
                    return;
                }
            }
        }
        warning("No such frame: {0}", type.getSimpleName());
    }

    protected class MultiFrameDesktop extends JDesktopPane {

        protected MultiFrameDesktop() {
            setDesktopManager(new MultiFrameDesktopManager());
        }

        @Override
        public MultiFrameDesktopManager getDesktopManager() {
            return (MultiFrameDesktopManager) super.getDesktopManager();
        }

        @Override
        public InternalFrame[] getAllFrames() {
            final JInternalFrame[] frames = super.getAllFrames();
            return Arrays.copyOf(frames, frames.length, InternalFrame[].class);
        }

        @Override
        public InternalFrame[] getAllFramesInLayer(int layer) {
            final JInternalFrame[] frames = super.getAllFramesInLayer(layer);
            return Arrays.copyOf(frames, frames.length, InternalFrame[].class);
        }

        protected class MultiFrameDesktopManager extends DefaultDesktopManager {

        }
    }

    protected class InternalFrame extends JInternalFrame implements PrefSupport {

        protected InternalFrame() {
            final FrameWidget meta = getClass().getAnnotation(FrameWidget.class);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setName(meta.key().isEmpty() ? getClass().getSimpleName() : meta.key());
            setTitle(meta.title().isEmpty() ? s(getClass().getSimpleName()) : s(meta.title()));
            setResizable(meta.resizable());
            setIconifiable(meta.iconifiable());
            setClosable(meta.closable());
            setMaximizable(meta.maximizable());
            setPreferredSize(getPref("size", getInitialSize()));
            addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    try {
                        putPref("size", getSize());
                        putPref("location", getLocation());
                    } finally {
                        removeInternalFrameListener(this);
                    }
                }
            });
        }

        @Override
        public void show() {
            setLocation(getPref("location", getInitialLocation()));
            super.show();
        }

        @Override
        public Preferences preferences() {
            return AbstractMultiFrame.this.preferences().node("frames").node(getName());
        }

        protected Dimension getInitialSize() {
            return new Dimension(500, 400);
        }

        protected Point getInitialLocation() {
            return new Point(0, 0);
        }
    }
}
