/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.rrm.ehour.ui.common.authorization;

import com.sun.jndi.ldap.LdapCtxFactory;
import net.rrm.ehour.domain.User;
import net.rrm.ehour.domain.UserDepartment;
import net.rrm.ehour.domain.UserRole;
import net.rrm.ehour.exception.ObjectNotUniqueException;
import net.rrm.ehour.user.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Provides userDetails
 */
@Service("authService")
public class AuthService implements UserDetailsService {
    @Autowired
    private UserService userService;
    private static final Logger LOGGER = Logger.getLogger(AuthService.class);

    @Autowired
    private MessageDigestPasswordEncoder passwordEncoder;



    /**
     * Get user by username (acegi)
     *
     * @param username
     */

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser authUser;


        if (username.endsWith(Test.EMAIL_SUFFIX)) {
            username = username.split("@")[0];
        }
        User user = userService.getUser(username);

        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setFirstName(username);
            user.setLastName(username);
            user.setEmail(username+Test.EMAIL_SUFFIX);
            Set<UserRole> roles = new HashSet<>();
            roles.add(UserRole.USER);
            user.setUserRoles(roles);
            user.setActive(true);
            try {
                userService.persistNewUser(user, "password");
            } catch (ObjectNotUniqueException e) {
                e.printStackTrace();
            }
        }

        if (user == null || !user.isActive()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Load user by username for " + username + " but user unknown or inactive");
            }

            throw new UsernameNotFoundException("User unknown");
        } else {
            authUser = new AuthUser(user);
        }

        return authUser;
    }

    public static void main(String[] args) throws Exception {
        Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        //ldapEnv.put(Context.PROVIDER_URL,  "ldap://societe.fr:389");
        ldapEnv.put(Context.PROVIDER_URL,  "ldap://10.100.11.11:389");
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        //ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=administrateur,cn=users,dc=societe,dc=fr");
        ldapEnv.put(Context.SECURITY_PRINCIPAL, "taiga@chinacscs.com");
        ldapEnv.put(Context.SECURITY_CREDENTIALS, "Password2015");
        //ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
        //ldapEnv.put(Context.SECURITY_PROTOCOL, "simple");
        try {
            DirContext ldapContext = new InitialDirContext(ldapEnv);
            System.out.println(ldapContext.getNameInNamespace());
//            Attributes attrs = ldapContext.getAttributes("CN=严杰,OU=CSCS全体人员,OU=CSCS,OU=Domain Users,DC=chinacscs,DC=com");
            SearchControls sc = new SearchControls();
            String[] returnAttrs = {"sn"};
            sc.setReturningAttributes(returnAttrs);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> result = ldapContext.search("OU=CSCS,OU=Domain Users,DC=chinacscs,DC=com", "userPrincipalName=taiga1@chinacscs.com", sc);
            if (result.hasMore()) {
                System.out.println(result.next().getAttributes().get("sn").get());
            } else {
                System.out.println("not exist");
            }
            System.out.println(result.hasMore());

            Assert.notNull(ldapContext);
            System.out.print("Authenticated");
        } catch (Exception  ex) {
            ex.printStackTrace();
        }

    }
}

class  Test extends DaoAuthenticationProvider {
    private static final Logger LOGGER = Logger.getLogger(Test.class);
    public static final String EMAIL_SUFFIX = "@chinacscs.com";

    public  Test () {
        ReflectionSaltSource saltSource = new ReflectionSaltSource();
        saltSource.setUserPropertyToUse("getSalt");
        this.setSaltSource(saltSource);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }

        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

        if ("admin".equals(username)) {
            super.additionalAuthenticationChecks(userDetails, authentication);
            return;
        }
        if (username.endsWith(EMAIL_SUFFIX)) {
            username = username.split("@")[0];
        }
        String presentedPassword = authentication.getCredentials().toString();

        Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnv.put(Context.PROVIDER_URL,  "ldap://10.100.11.11:389");
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        ldapEnv.put(Context.SECURITY_PRINCIPAL, username + EMAIL_SUFFIX);
        ldapEnv.put(Context.SECURITY_CREDENTIALS, presentedPassword);
        try {
            DirContext ldapContext = new InitialDirContext(ldapEnv);

            Assert.notNull(ldapContext);
            LOGGER.info("Authenticated");
        } catch (Exception  ex) {
            LOGGER.info("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }
    }
}


