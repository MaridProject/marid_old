package org.marid.applib.security;

/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import io.undertow.security.idm.Account;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Pac4JPrincipal;
import org.pac4j.core.profile.UserProfile;

import java.util.Collections;
import java.util.Set;

public class MaridAccount implements Account {

  private final Pac4JPrincipal principal;
  private final UserProfile profile;

  public MaridAccount(UserProfile profile) {
    this.principal = new Pac4JPrincipal((CommonProfile) (this.profile = profile));
  }

  @Override
  public Pac4JPrincipal getPrincipal() {
    return principal;
  }

  public UserProfile getProfile() {
    return profile;
  }

  @Override
  public Set<String> getRoles() {
    return Collections.singleton("user");
  }

  @Override
  public int hashCode() {
    return principal.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || (obj instanceof MaridAccount
        && ((MaridAccount) obj).principal.equals(principal)
        && ((MaridAccount) obj).getRoles().equals(getRoles())
    );
  }

  @Override
  public String toString() {
    return principal.toString();
  }
}
