package net.rrm.ehour.ui.common.authorization;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

class  LdapDaoAuthenticationProvider extends DaoAuthenticationProvider {
    private static final Logger LOGGER = Logger.getLogger(LdapDaoAuthenticationProvider.class);
    public static final String EMAIL_SUFFIX = "@chinacscs.com";

    public  LdapDaoAuthenticationProvider () {
        ReflectionSaltSource saltSource = new ReflectionSaltSource();
        saltSource.setUserPropertyToUse("getSalt");
        this.setSaltSource(saltSource);
    }


    public static Hashtable<String, String> getLdapEnv(String username, String password) {
        Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnv.put(Context.PROVIDER_URL,  "ldap://10.100.11.11:389");
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        ldapEnv.put(Context.SECURITY_PRINCIPAL, username + EMAIL_SUFFIX);
        ldapEnv.put(Context.SECURITY_CREDENTIALS, password);
        return ldapEnv;
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

        Hashtable<String, String> ldapEnv = getLdapEnv(username, presentedPassword);
        try {
            DirContext ldapContext = new InitialDirContext(ldapEnv);

            Assert.notNull(ldapContext);
            LOGGER.info("Authenticated");
        } catch (Exception  ex) {
            // TODO:check Exception
            LOGGER.info("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }
    }
}
