package org.marid.applib.security;

/*-
 * #%L
 * marid-ide-server
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */

import io.undertow.security.idm.Account;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Pac4JPrincipal;

import java.util.Collections;
import java.util.Set;

public class MaridAccount implements Account {

  private final Pac4JPrincipal principal;
  private final CommonProfile profile;

  public MaridAccount(CommonProfile profile) {
    this.principal = new Pac4JPrincipal(this.profile = profile);
  }

  @Override
  public Pac4JPrincipal getPrincipal() {
    return principal;
  }

  public CommonProfile getProfile() {
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
