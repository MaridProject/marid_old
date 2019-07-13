package org.marid.applib.security;

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
