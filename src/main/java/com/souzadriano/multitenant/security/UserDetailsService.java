package com.souzadriano.multitenant.security;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.souzadriano.multitenant.domain.DataSourceConfig;
import com.souzadriano.multitenant.domain.User;
import com.souzadriano.multitenant.repository.UserRepository;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        Optional<User> userFromDatabase = userRepository.findOneWithAuthoritiesByLogin(lowercaseLogin);
        return userFromDatabase.map(user -> {
            if (!user.getActivated()) {
                throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
            }
            List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
            
            DataSourceConfig dataSourceConfig = user.getDataSourceConfig();
            
            String tenantId = null;
            if (dataSourceConfig != null) {
            	tenantId = dataSourceConfig.getName();
            }
            com.souzadriano.multitenant.security.UserDetails userDetails = new com.souzadriano.multitenant.security.UserDetails(lowercaseLogin,
                user.getPassword(),
                grantedAuthorities, tenantId);
			return userDetails;
        }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " +
        "database"));
    }
}
