/*-
 * #%L
 * marid-webapp
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.marid.app.fonts;

import com.vaadin.icons.VaadinIcons;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumSet;

public class VaadinFontList {

  public static void main(String... args) {
    final Object[][] data = EnumSet.allOf(VaadinIcons.class).stream()
        .map(e -> new Object[] {new String(new int[] {e.getCodepoint()}, 0, 1), e.name()})
        .toArray(Object[][]::new);
    final Object[] columns = {"Symbol", "Name"};

    EventQueue.invokeLater(() -> {
      final JTable table = new JTable(data, columns);
      table.setRowHeight(28);

      final var loader = Thread.currentThread().getContextClassLoader();
      try (final var is = loader.getResourceAsStream("VAADIN/themes/valo/fonts/vaadin-icons/Vaadin-Icons.ttf")) {
        final var font = Font.createFont(Font.TRUETYPE_FONT, is);
        final var renderer = new DefaultTableCellRenderer() {
          @Override
          public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(font.deriveFont(Font.PLAIN, 20));
            return c;
          }
        };
        table.getColumnModel().getColumn(0).setCellRenderer(renderer);
      } catch (IOException x) {
        throw new UncheckedIOException(x);
      } catch (FontFormatException x) {
        throw new IllegalStateException(x);
      }

      final var frame = new JFrame("Vaadin Icons");
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      frame.add(new JScrollPane(table));
      frame.setMinimumSize(new Dimension(800, 800));
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }
}
