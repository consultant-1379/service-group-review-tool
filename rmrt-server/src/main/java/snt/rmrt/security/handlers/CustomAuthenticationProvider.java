package snt.rmrt.security.handlers;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import snt.rmrt.models.user.User;
import snt.rmrt.models.user.UserRoles;
import snt.rmrt.repositories.UserRepository;

import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    @Value("${gerrit.domain}")
    private String domain;

    @Autowired
    public CustomAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        boolean authenticated = authenticatedFromGerrit(username, password);
        Optional<User> principal = userRepository.findById(username);

        if (authenticated && principal.isPresent()) {
            User user = principal.get();
            if(userRepository.count() < 2) {
                user.setRole(UserRoles.ROLE_ADMIN);
                userRepository.save(user);
            }
            return
                    new UsernamePasswordAuthenticationToken(
                            user,
                            authentication.getCredentials(),
                            Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
                    );
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean authenticatedFromGerrit(final String username, final String password) {
        final GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        final GerritAuthData.Basic authData = new GerritAuthData.Basic(domain, username, password);
        GerritApi gerritApi = gerritRestApiFactory.create(authData);

        try {
            AccountInfo accountInfo = gerritApi.accounts().self().get();

            User user = userRepository.findById(username).orElse(new User());
            user.setName(accountInfo.name);
            user.setUsername(accountInfo.username);

            userRepository.save(user);

        } catch (RestApiException exception) {
            log.warn(exception.getMessage());
            return false;
        }

        return true;
    }
}