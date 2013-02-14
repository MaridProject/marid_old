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

package org.marid.ide.gui

import groovy.util.logging.Log
import org.marid.ide.res.MaridAction
import org.marid.l10n.Localized.S

import javax.swing.*
import javax.swing.GroupLayout.ParallelGroup
import javax.swing.GroupLayout.SequentialGroup
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.util.logging.Level

import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
import static javax.swing.KeyStroke.getKeyStroke
import static java.util.prefs.Preferences.userNodeForPackage

@Log
abstract class AbstractDialog extends JDialog implements WindowListener {

    private def initialized = false;

    protected final def acceptAction = new MaridAction(name: acceptName, icon: acceptIcon) {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                accept();
            } catch (Exception x) {
                log.log(Level.WARNING, "Accepting error", x);
            } finally {
                dispose();
            }
        }
    };

    protected final def rejectAction = new MaridAction(name: rejectName, icon: rejectIcon) {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                reject();
            } catch (Exception x) {
                log.log(Level.WARNING, "Rejecting error", x);
            } finally {
                dispose();
            }
        }
    };

    protected final def prefs = userNodeForPackage(getClass()).node(getClass().simpleName);

    AbstractDialog(Frame frame, String title, boolean modal, GraphicsConfiguration conf) {
        super(frame, S.l(title), modal, conf);
    }

    AbstractDialog(Frame frame, String title, boolean modal) {
        super(frame, S.l(title), modal);
    }

    AbstractDialog(Frame frame, String title) {
        super(frame, S.l(title));
    }

    AbstractDialog(Frame frame) {
        super(frame);
    }

    AbstractDialog() {
    }

    AbstractDialog(Window w, String title, Dialog.ModalityType modality, GraphicsConfiguration c) {
        super(w, S.l(title), modality, c);
    }

    AbstractDialog(Window window, String title, Dialog.ModalityType modality) {
        super(window, S.l(title), modality);
    }

    AbstractDialog(Window window, String title) {
        super(window, S.l(title));
    }

    AbstractDialog(Dialog d, String title, Dialog.ModalityType modality, GraphicsConfiguration c) {
        super(d, S.l(title), modality, c);
    }

    protected abstract void fill(GroupLayout gl, SequentialGroup vg, ParallelGroup hg);

    protected void reject() {
    }

    protected void accept() {
    }

    protected String getAcceptName() {
        return "OK";
    }

    protected String getRejectName() {
        return "Cancel";
    }

    protected String getAcceptIcon() {
        return "s16/ok.png";
    }

    protected String getRejectIcon() {
        return "s16/cancel.png";
    }

    protected void addDefaultButtons(GroupLayout gl, SequentialGroup vg, ParallelGroup hg) {
        vg.addGap(24, 32, Integer.MAX_VALUE);
        def acceptButton = new JButton(acceptAction);
        def rejectButton = new JButton(rejectAction);
        vg.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(rejectButton)
                .addComponent(acceptButton));
        hg.addGroup(gl.createSequentialGroup()
                .addComponent(rejectButton)
                .addGap(32, 64, Integer.MAX_VALUE)
                .addComponent(acceptButton));
        rootPane.defaultButton = acceptButton;
    }

    private void init() {
        addWindowListener(this);
        def gl = new GroupLayout(contentPane);
        gl.autoCreateContainerGaps = true;
        gl.autoCreateGaps = true;
        def vg = gl.createSequentialGroup();
        def hg = gl.createParallelGroup();
        fill(gl, vg, hg);
        gl.verticalGroup = vg;
        gl.horizontalGroup = hg;
        contentPane.layout = gl;
        def escape = getKeyStroke("ESCAPE");
        rootPane.registerKeyboardAction(rejectAction, escape, WHEN_IN_FOCUSED_WINDOW);
        pack();
        setLocationRelativeTo(owner);
    }

    @Override
    void windowClosing(WindowEvent e) {
        rejectAction.actionPerformed(new ActionEvent(this, 0, "close"));
    }

    @Override
    void windowOpened(WindowEvent e) {
    }

    @Override
    void windowClosed(WindowEvent e) {
    }

    @Override
    void windowIconified(WindowEvent e) {
    }

    @Override
    void windowDeiconified(WindowEvent e) {
    }

    @Override
    void windowActivated(WindowEvent e) {
    }

    @Override
    void windowDeactivated(WindowEvent e) {
    }

    @Override
    void setVisible(boolean b) {
        if (!initialized) {
            init();
            initialized = true;
        }
        super.setVisible(b);
    }
}
