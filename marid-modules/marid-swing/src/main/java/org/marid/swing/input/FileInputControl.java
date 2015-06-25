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

package org.marid.swing.input;

import org.marid.logging.LogSupport;
import org.marid.swing.actions.MaridAction;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

import static javax.swing.Action.SHORT_DESCRIPTION;

/**
 * @author Dmitry Ovchinnikov
 */
public class FileInputControl extends JPanel implements InputControl<File>, LogSupport {

    private final JTextField textField = new JTextField();

    public FileInputControl(int type, FileFilter... fileFilters) {
        final GroupLayout g = new GroupLayout(this);
        g.setAutoCreateGaps(true);
        final JButton browseButton = new JButton(new MaridAction("", "browse", (a, e) -> {
            final File file = getInputValue();
            try {
                final File parent = file != null ? file.getParentFile() : null;
                final JFileChooser ch = new JFileChooser(parent == null ? new File(".") : parent);
                ch.setFileSelectionMode(type);
                ch.setAcceptAllFileFilterUsed(true);
                for (final FileFilter fileFilter : fileFilters) {
                    ch.addChoosableFileFilter(fileFilter);
                }
                if (fileFilters.length > 0) {
                    ch.setFileFilter(fileFilters[0]);
                }
                ch.setMultiSelectionEnabled(false);
                if (file != null) {
                    ch.setSelectedFile(file);
                }
                switch (ch.showOpenDialog(this)) {
                    case JFileChooser.APPROVE_OPTION:
                        textField.setText(ch.getSelectedFile().toString());
                        break;
                }
            } catch (Exception x) {
                log(WARNING, "Unable to show file chooser", x);
            }
        }, SHORT_DESCRIPTION, s("Browse")));
        g.setVerticalGroup(g.createParallelGroup().addComponent(textField).addComponent(browseButton));
        g.setHorizontalGroup(g.createSequentialGroup().addComponent(textField, -1, 400, -1).addComponent(browseButton));
        setLayout(g);
    }

    public FileInputControl(String description, String... extensions) {
        this(JFileChooser.FILES_ONLY, new FileNameExtensionFilter(description, extensions));
    }

    public FileInputControl() {
        this(JFileChooser.DIRECTORIES_ONLY);
    }

    @Override
    public File getInputValue() {
        return textField.getText().trim().isEmpty() ? null : new File(textField.getText().trim());
    }

    @Override
    public void setInputValue(File value) {
        if (value != null) {
            textField.setText(value.toString());
        }
    }

    @Override
    public int getBaseline(int width, int height) {
        return textField.getBaseline(width, height);
    }

    @Override
    public BaselineResizeBehavior getBaselineResizeBehavior() {
        return textField.getBaselineResizeBehavior();
    }
}
