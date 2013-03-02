/*
 * Copyright (C) 2013 Dmitry Ovchinnikov
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

package org.marid.ide.menu

import images.Images

import javax.swing.*
import java.awt.event.ActionEvent

/**
 * Menu bar.
 *
 * @author Dmitry Ovchinnikov 
 */
class MenuBar extends JMenuBar {
    /**
     * Constructs the menu bar.
     *
     * @param maridMenu
     */
    MenuBar(List<MenuEntry> menuEntries) {
        for (def entry in menuEntries
                .findAll{it.path.length == 0}
                .sort(false, {e1, e2 -> e1.priority <=> e2.priority})) {
            switch (entry.type) {
                case MenuType.ITEM:
                    add(getItem(entry));
                    break;
                case MenuType.CHECKED:
                    add(getCheckedItem(entry));
                    break;
                case MenuType.RADIO:
                    add(getRadioItem(entry));
                    break;
                case MenuType.MENU:
                    add(getMenu(menuEntries, entry));
                    break;
            }
        }
    }

    private def getItem(MenuEntry entry) {
        return new JMenuItem(new MenuBarAction(entry));
    }

    private def getRadioItem(MenuEntry entry) {
        return new JRadioButtonMenuItem(new MenuBarAction(entry));
    }

    private def getCheckedItem(MenuEntry entry) {
        return new JCheckBoxMenuItem(new MenuBarAction(entry));
    }

    private def getMenu(List<MenuEntry> menuEntries, MenuEntry entry) {
        def JMenu menu = new JMenu(new MenuBarAction(entry));
        def n = entry.path.length;
        for (def e in menuEntries
                .findAll{it.path.length == n + 1 && it.path.take(n) == entry.path}
                .sort(false, {e1, e2 -> e1.priority <=> e2.priority})) {
            switch (e.type) {
                case MenuType.ITEM:
                    menu.add(getItem(e));
                    break;
                case MenuType.CHECKED:
                    menu.add(getCheckedItem(e));
                    break;
                case MenuType.RADIO:
                    menu.add(getRadioItem(e));
                    break;
                case MenuType.MENU:
                    menu.add(getMenu(menuEntries, e));
                    break;
            }
        }
        return menu;
    }

    private class MenuBarAction extends AbstractAction {

        final MenuEntry entry;

        public MenuBarAction(MenuEntry entry) {
            this.entry = entry;
            putValue(ACTION_COMMAND_KEY, entry.command);
            putValue(NAME, entry.label.ls());
            def shortcut = entry.shortcut;
            def icon = entry.icon;
            def description = entry.description?.ls();
            def info = entry.info?.ls();
            if (shortcut != null) {
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(shortcut));
            }
            if (icon != null) {
                def smallIcon = Images.getIcon(icon, 16, 16);
                if (smallIcon != null) {
                    putValue(SMALL_ICON, smallIcon);
                }
            }
            if (description != null) {
                putValue(LONG_DESCRIPTION, description);
            }
            if (info != null) {
                putValue(SHORT_DESCRIPTION, info);
            }
        }

        @Override
        void actionPerformed(ActionEvent e) {
            entry.call(e);
        }

        void update() {
            if (entry.mutableDescription) {
                putValue(LONG_DESCRIPTION, entry.description.ls());
            }
            if (entry.mutableInfo) {
                putValue(SHORT_DESCRIPTION, entry.info.ls());
            }
            if (entry.mutableLabel) {
                putValue(NAME, entry.label.ls());
            }
            if (entry.mutableIcon) {
                putValue(SMALL_ICON, Images.getIcon(entry.icon, 16, 16));
            }
            if (entry.hasSelectedPredicate()) {
                putValue(SELECTED_KEY, entry.selected);
            }
            if (entry.hasEnabledPredicate()) {
                enabled = entry.enabled;
            }
        }
    }
}
