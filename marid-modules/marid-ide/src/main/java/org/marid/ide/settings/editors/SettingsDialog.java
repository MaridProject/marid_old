/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
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

package org.marid.ide.settings.editors;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import org.marid.ee.ui.UI;
import org.marid.ide.scenes.IdeScene;
import org.marid.ide.settings.AbstractSettings;
import org.marid.l10n.L10nSupport;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

/**
 * @author Dmitry Ovchinnikov
 */
@UI
public class SettingsDialog extends Dialog<ButtonType> implements L10nSupport {

    @Inject
    public SettingsDialog(IdeScene ideScene, Instance<SettingsEditor> editors) {
        final DialogPane dialogPane = getDialogPane();
        dialogPane.setPrefSize(800, 600);
        final SettingsEditor[] settingsEditors = settingsEditors(editors);
        dialogPane.setContent(tabPane(settingsEditors));
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);
        setTitle(s("IDE settings"));
        initModality(Modality.WINDOW_MODAL);
        initOwner(ideScene.getWindow());
        setResizable(true);
        final Map<AbstractSettings, byte[]> snapshot = Stream.of(settingsEditors)
                .collect(toMap(SettingsEditor::getSettings, e -> e.getSettings().save()));
        setResultConverter(param -> {
            switch (param.getButtonData()) {
                case CANCEL_CLOSE:
                    snapshot.forEach(AbstractSettings::load);
                    return null;
                default:
                    return param;
            }
        });
    }

    private SettingsEditor[] settingsEditors(Instance<SettingsEditor> editors) {
        return StreamSupport.stream(editors.spliterator(), false).toArray(SettingsEditor[]::new);
    }

    private TabPane tabPane(SettingsEditor[] settingsEditors) {
        final TabPane tabPane = new TabPane(Stream.of(settingsEditors)
                .sorted(Comparator.comparing(e -> e.getSettings().getName()))
                .map(editor -> new Tab(s(editor.getSettings().getName()), editor.getNode()))
                .toArray(Tab[]::new));
        for (final Tab tab : tabPane.getTabs()) {
            ((Region) tab.getContent()).setPadding(new Insets(10, 0, 10, 0));
            tab.getContent().setStyle("-fx-background-color: -fx-background");
        }
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return tabPane;
    }
}
