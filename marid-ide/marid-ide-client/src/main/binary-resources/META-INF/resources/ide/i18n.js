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

class I18N {

  constructor() {
    this._data = {
      en: {
        session: "session"
      },
      es: {
        session: "sesión"
      }
    };
  }

  /**
   * @param {string} key A key
   * @return {string} A value
   */
  str(key) {
    for (const lang of navigator.languages) {
      const map = this._data[lang];
      if (map) {
        const v = map[key];
        if (v != null) {
          return v;
        }
      }
    }
    return key;
  }

  /**
   * @param {string} key A key
   * @return {string} A value
   */
  cap(key) {
    const v = this.str(key);
    return v.charAt(0).toUpperCase() + v.substring(1);
  }

  /**
   * @param {string} key A key
   * @return {string} A value
   */
  capd(key) {
    const v = this.cap(key);
    return v + "...";
  }
}

export const i18n = new I18N();
