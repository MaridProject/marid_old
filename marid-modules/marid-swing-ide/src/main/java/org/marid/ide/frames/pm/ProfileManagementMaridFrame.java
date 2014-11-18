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

package org.marid.ide.frames.pm;

import org.marid.dyn.MetaInfo;
import org.marid.ide.components.ProfileManager;
import org.marid.ide.frames.CloseableFrame;
import org.marid.ide.frames.MaridFrame;
import org.marid.ide.profile.Profile;
import org.marid.ide.swing.mbean.MBeanServerTreeTable;
import org.marid.logging.Logging;
import org.marid.logging.SimpleHandler;
import org.marid.swing.log.LogComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.support.GenericApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Handler;

import static java.util.Collections.emptyList;

/**
 * @author Dmitry Ovchinnikov.
 */
@CloseableFrame
@MetaInfo(name = "Profile Management")
public class ProfileManagementMaridFrame extends MaridFrame {

    private final Profile profile;
    private final Handler logHandler;
    private final JPanel panel = new JPanel(new BorderLayout());
    private final LogComponent logComponent;
    private final MBeanServerTreeTable beanTree;
    private final JSplitPane splitPane;
    private final InheritableThreadLocal<String> itl = new InheritableThreadLocal<>();
    private final ApplicationListener<ApplicationEvent> applicationListener;

    @Autowired
    public ProfileManagementMaridFrame(GenericApplicationContext context, ProfileManager profileManager) {
        super(context, "Profile Management: %s", profileManager.getCurrentProfile());
        profile = profileManager.getCurrentProfile();
        logComponent = new LogComponent(preferences(), emptyList(), r -> true);
        logComponent.setPreferredSize(new Dimension(logComponent.getPreferredSize().width, 150));
        beanTree = new MBeanServerTreeTable(profile.getName(), profile::getConnection);
        panel.add(new JScrollPane(beanTree));
        logHandler = new SimpleHandler((h, r) -> {
            if (profile.getName().equals(itl.get())) {
                logComponent.publish(r);
            }
        });
        toolBar.addSeparator();
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel, logComponent);
        restoreDividerLocation();
        centerPanel.add(splitPane);
        applicationListener = event -> {
            info("Event: {0}", event);
            if (event instanceof ContextStartedEvent) {
                EventQueue.invokeLater(() -> {
                    actionByKey("/Control/c/Run").setEnabled(false);
                    actionByKey("/Control/c/Stop").setEnabled(true);
                });
            } else if (event instanceof ContextClosedEvent) {
                EventQueue.invokeLater(() -> {
                    Logging.rootLogger().removeHandler(logHandler);
                    actionByKey("/Control/c/Run").setEnabled(true);
                    actionByKey("/Control/c/Stop").setEnabled(false);
                });
            }
            EventQueue.invokeLater(beanTree::update);
        };
    }

    @Override
    protected void fillActions() {
        addAction("/Control/c/Run", "Run", "start", this::startProfile).setEnabledState(!profile.isStarted()).enableToolbar();
        addAction("/Control/c/Stop", "Stop", "stop", this::stopProfile).setEnabledState(profile.isStarted()).enableToolbar();
        addAction("/Log/s/Log", "Log", "log", this::showLog).setSelected(true).enableToolbar();
    }

    protected void startProfileTrigger() {
        itl.set(profile.getName());
        Logging.rootLogger().addHandler(logHandler);
    }

    protected void startProfile(Action action, ActionEvent event) {
        profile.start(this::startProfileTrigger);
    }

    public void stopProfile(Action action, ActionEvent event) {
        profile.stop();
    }

    public void showLog(Action action, ActionEvent event) {
        logComponent.setVisible(Boolean.TRUE.equals(action.getValue(Action.SELECTED_KEY)));
        if (logComponent.isVisible()) {
            restoreDividerLocation();
        }
    }

    protected void restoreDividerLocation() {
        splitPane.setDividerLocation(preferences().getInt("splitPos", panel.getPreferredSize().height));
    }

    @Override
    public void init() {
        super.init();
        profile.addApplicationListener(applicationListener);
    }

    @Override
    public void dispose() {
        try {
            profile.stop();
            preferences().putInt("splitPos", splitPane.getDividerLocation());
            beanTree.savePreferences();
        } finally {
            profile.removeApplicationListener(applicationListener);
            super.dispose();
        }
    }
}
