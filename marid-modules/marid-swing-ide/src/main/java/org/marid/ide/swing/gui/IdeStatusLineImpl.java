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

package org.marid.ide.swing.gui;

import org.marid.ide.base.IdeStatusLine;
import org.marid.ide.components.ProfileManager;
import org.marid.ide.profile.Profile;
import org.marid.logging.LogSupport;
import org.marid.pref.SysPrefSupport;
import org.marid.util.SysPropsSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class IdeStatusLineImpl extends JPanel implements IdeStatusLine, SysPrefSupport, SysPropsSupport, LogSupport {

    protected final IdeFrameImpl ideFrame;
    protected final ProfileManager profileManager;
    protected final JLabel status = new JLabel("Done");
    protected final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    protected final JLabel timeLabel = new JLabel(currentTime());
    protected final ProfileManagerListModel profileListModel;
    protected final JComboBox<Profile> profilesCombo;

    @Autowired
    public IdeStatusLineImpl(IdeFrameImpl ideFrame, ProfileManager profileManager) {
        this.ideFrame = ideFrame;
        setLayout(new GridBagLayout());
        this.profileManager = profileManager;
        this.profileListModel = new ProfileManagerListModel();
        ideFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                if (profileListModel.selectedItem != null) {
                    putSysPref("currentProfile", profileListModel.selectedItem.getName());
                    info("Saved {0} as default", profileListModel.selectedItem.getName());
                }
            }
        });
        final GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.BASELINE;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 3, 2, 3);
        c.weightx = 1.0;
        add(status, c);
        c.weightx = 0.0;
        add(new JSeparator(SwingConstants.VERTICAL), c);
        add(profilesCombo = new JComboBox<>(profileListModel), c);
        add(new JSeparator(SwingConstants.VERTICAL), c);
        add(timeLabel, c);
    }

    private String currentTime() {
        return dateFormat.format(new Date());
    }

    protected class ProfileManagerListModel extends AbstractListModel<Profile> implements ComboBoxModel<Profile> {

        protected final List<Profile> profiles;
        protected Profile selectedItem;

        public ProfileManagerListModel() {
            profiles = profileManager.getProfiles();
            profileManager.addProfileAddConsumer(this, p -> {
                profiles.clear();
                profiles.addAll(profileManager.getProfiles());
                final int index = profiles.indexOf(p);
                if (index >= 0) {
                    fireIntervalAdded(this, index, index);
                }
            });
            profileManager.addProfileRemoveConsumer(this, p -> {
                final int index = profiles.indexOf(p);
                if (index >= 0) {
                    profiles.remove(p);
                    fireIntervalRemoved(this, index, index);
                }
            });
            final String profileName = getSysPref("currentProfile", "default");
            selectedItem = profiles.stream().filter(p -> p.getName().equals(profileName)).findAny().orElse(null);
        }

        @Override
        public void setSelectedItem(Object anItem) {
            selectedItem = (Profile) anItem;
        }

        @Override
        public Profile getSelectedItem() {
            return selectedItem;
        }

        @Override
        public int getSize() {
            return profiles.size();
        }

        @Override
        public Profile getElementAt(int index) {
            return profiles.get(index);
        }

        public void update() {
            fireContentsChanged(this, 0, getSize());
        }
    }
}
