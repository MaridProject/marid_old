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

package org.marid.ide.gui;

import org.marid.ide.base.Ide;
import org.marid.ide.base.IdeFrame;
import org.marid.image.MaridIcons;
import org.marid.logging.LogSupport;
import org.marid.pref.PrefSupport;
import org.marid.swing.WindowPrefs;
import org.marid.swing.actions.MaridAction;
import org.marid.swing.actions.MaridActions;
import org.marid.swing.log.SwingHandler;
import org.marid.swing.menu.SwingMenuBarWrapper;
import org.marid.swing.util.MessageSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static javax.swing.JOptionPane.*;

/**
 * @author Dmitry Ovchinnikov
 */
@Component("ideFrame")
public class IdeFrameImpl extends JFrame implements IdeFrame, PrefSupport, LogSupport, MessageSupport {

    private final IdeImpl ide;

    @Autowired
    private IdeDesktopImpl desktop;

    @Autowired
    private IdeStatusLineImpl statusLine;

    @Autowired
    private ActionMap ideActionMap;

    @Autowired
    public IdeFrameImpl(IdeImpl ide) {
        super(LS.s("Marid IDE"), WindowPrefs.graphicsConfiguration("IDE"));
        setName("IDE");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.ide = ide;
        setIconImages(MaridIcons.ICONS);
        setLocationRelativeTo(null);
        setJMenuBar(new JMenuBar());
    }

    @PostConstruct
    private void init() {
        add(desktop);
        add(statusLine, BorderLayout.SOUTH);
        pack();
        setBounds(getPref("bounds", new Rectangle(0, 0, 700, 500)));
        setState(getPref("state", getState()));
        setExtendedState(getPref("extendedState", getExtendedState()));
        setVisible(true);
    }

    @PreDestroy
    private void destroy() {
        dispose();
    }

    private JMenu lafMenu() {
        final JMenu menu = new JMenu(s("Look and feel"));
        for (final UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels()) {
            menu.add(new MaridAction(lookAndFeelInfo.getName(), null, ev -> {
                try {
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    Arrays.asList(JFrame.getFrames()).forEach(SwingUtilities::updateComponentTreeUI);
                } catch (Exception x) {
                    showMessage(WARNING_MESSAGE, "LAF", "Error", x);
                }
            }));
        }
        return menu;
    }

    @Override
    public Ide getIde() {
        return ide;
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        switch (e.getID()) {
            case WindowEvent.WINDOW_CLOSING:
                WindowPrefs.saveGraphicsDevice(this);
                putPref("state", getState());
                putPref("extendedState", getExtendedState());
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
                    putPref("bounds", getBounds());
                }
                break;
            case WindowEvent.WINDOW_OPENED:
                MaridActions.fillMenu(ideActionMap, new SwingMenuBarWrapper(getJMenuBar()));
                setState(getPref("state", getState()));
                setExtendedState(getPref("extendedState", getExtendedState()));
                break;
        }
        super.processWindowEvent(e);
    }

    @Override
    public IdeStatusLineImpl getStatusLine() {
        return statusLine;
    }

    @Override
    public IdeDesktopImpl getDesktop() {
        return desktop;
    }

    public void showLog() {
        for (final Handler handler : Logger.getGlobal().getParent().getHandlers()) {
            if (handler instanceof SwingHandler) {
                ((SwingHandler) handler).show();
            }
        }
    }

    public void exitWithConfirm() {
        switch (showConfirmDialog(null, m("Do you want to exit?"), s("Exit"), YES_NO_OPTION, QUESTION_MESSAGE)) {
            case YES_OPTION:
                ide.exit();
                break;
        }
    }
}
