package com.souzadriano.multitenant.security;

import java.util.Collection;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserDetails implements org.springframework.security.core.userdetails.UserDetails, CredentialsContainer {
	
	private static final long serialVersionUID = -8381466622477366997L;
	
	private User user;
	private String tenantId;
	
	public UserDetails(String username, String password,
			Collection<? extends GrantedAuthority> authorities, String tenantId) {
		this(username, password, true, true, true, true, authorities, tenantId);
	}
	
	public UserDetails(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String tenantId) {
		
		this.user = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.tenantId = tenantId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public void eraseCredentials() {
		user.eraseCredentials();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getAuthorities();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return user.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return user.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return user.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}
}
