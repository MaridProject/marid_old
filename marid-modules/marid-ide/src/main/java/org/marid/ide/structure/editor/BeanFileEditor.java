/*
 * Copyright (c) 2017 Dmitry Ovchinnikov
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

package org.marid.ide.structure.editor;

import org.marid.IdeDependants;
import org.marid.dependant.beaneditor.BeanEditorConfiguration;
import org.marid.dependant.beaneditor.BeanEditorParam;
import org.marid.ide.model.TextFile;
import org.marid.ide.project.ProjectManager;
import org.marid.ide.project.ProjectProfile;
import org.marid.jfx.action.SpecialAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class BeanFileEditor extends AbstractFileEditor<ProjectProfile> {

    private final ProjectManager projectManager;
    private final IdeDependants dependants;
    private final SpecialAction editAction;

    @Autowired
    public BeanFileEditor(@Qualifier("java") PathMatcher javaPathMatcher,
                          ProjectManager projectManager,
                          IdeDependants dependants,
                          SpecialAction editAction) {
        super(javaPathMatcher);
        this.projectManager = projectManager;
        this.dependants = dependants;
        this.editAction = editAction;
    }

    @Nonnull
    @Override
    public String getName() {
        return "Bean File Editor";
    }

    @Nonnull
    @Override
    public String getIcon() {
        return icon("M_APPS");
    }

    @Nonnull
    @Override
    public String getGroup() {
        return "bean";
    }

    @Override
    protected ProjectProfile editorContext(@Nonnull Path path) {
        return projectManager.getProfile(path).orElse(null);
    }

    @Override
    protected void edit(@Nonnull Path file, @Nonnull ProjectProfile context) {
        dependants.start(BeanEditorConfiguration.class, new BeanEditorParam(context, new TextFile(file)), c -> {});
    }

    @Nullable
    @Override
    public SpecialAction getSpecialAction() {
        return editAction;
    }
}