import {i18n} from './i18n.js';

/*-
 * #%L
 * marid-ide-client
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
export class Ide {

  constructor() {
    this._ui = webix.ui({
      type: "space",
      rows: [
        {
          type: "wide",
          rows: [
            {
              view: "menu",
              ready: function() {
                console.info("XXX");
                this.parse([
                  {
                    id: "1",
                    value: i18n.capd("session"),
                    icon: "user",
                    submenu: [
                      "A",
                      "B"
                    ]
                  }
                ])
              }
            },
            {
              view: "template"
            }
          ]
        }
      ]
    });
  }

  launch() {
    this._ui.show();

    webix.ui.fullScreen();
  }
}
