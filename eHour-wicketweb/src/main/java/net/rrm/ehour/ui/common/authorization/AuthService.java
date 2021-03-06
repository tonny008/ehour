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

import net.rrm.ehour.domain.User;
import net.rrm.ehour.domain.UserRole;
import net.rrm.ehour.exception.ObjectNotUniqueException;
import net.rrm.ehour.user.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
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
     * @param username username
     */

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser authUser;


        if (username.endsWith(LdapDaoAuthenticationProvider.EMAIL_SUFFIX)) {
            username = username.split("@")[0];
        }
        User user = userService.getUser(username);

        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setFirstName(username);
            user.setLastName(username);

            Hashtable<String, String> ldapEnv =
                    LdapDaoAuthenticationProvider.getLdapEnv("taiga", "Password2015");
            try {
                DirContext ldapContext = new InitialDirContext(ldapEnv);
                Assert.notNull(ldapContext);
                SearchControls sc = new SearchControls();
                String[] returnAttrs = {"sn"};
                sc.setReturningAttributes(returnAttrs);
                sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
                NamingEnumeration<SearchResult> result = ldapContext.search(
                        "OU=CSCS,OU=Domain Users,DC=chinacscs,DC=com",
                        String.format("userPrincipalName=%s@chinacscs.com", username),
                        sc);
                if (result.hasMore()) {
                    String name = (String) result.next().getAttributes().get("sn").get();
                    if (name != null && name.length() > 1) {
                        user.setFirstName(name.substring(1));
                        user.setLastName(name.substring(0, 1)); // 不考虑复姓
                    }
                    user.setEmail(username + LdapDaoAuthenticationProvider.EMAIL_SUFFIX);
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (!user.isActive()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Load user by username for " + username + " but user unknown or inactive");
            }

            throw new UsernameNotFoundException("User unknown");
        } else {
            authUser = new AuthUser(user);
        }

        return authUser;
    }

    public static void main(String[] args) {
        Hashtable<String, String> ldapEnv = new Hashtable<>(11);
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        //ldapEnv.put(Context.PROVIDER_URL,  "ldap://societe.fr:389");
        ldapEnv.put(Context.PROVIDER_URL, "ldap://10.100.11.11:389");
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        //ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=administrateur,cn=users,dc=societe,dc=fr");
        ldapEnv.put(Context.SECURITY_PRINCIPAL, "taiga@chinacscs.com");
        ldapEnv.put(Context.SECURITY_CREDENTIALS, "Password2015");
        //ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
        //ldapEnv.put(Context.SECURITY_PROTOCOL, "simple");
        try {
            DirContext ldapContext = new InitialDirContext(ldapEnv);
            System.out.println(ldapContext.getNameInNamespace());
            SearchControls sc = new SearchControls();
            String[] returnAttrs = {"sn"};
            sc.setReturningAttributes(returnAttrs);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> result = ldapContext.search(
                    "OU=CSCS,OU=Domain Users,DC=chinacscs,DC=com",
                    "userPrincipalName=taiga1@chinacscs.com",
                    sc);
            if (result.hasMore()) {
                System.out.println(result.next().getAttributes().get("sn").get());
            } else {
                System.out.println("not exist");
            }
            System.out.println(result.hasMore());

            Assert.notNull(ldapContext);
            System.out.print("Authenticated");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}



