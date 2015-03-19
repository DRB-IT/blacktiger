package dk.drb.blacktiger.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

/**
 *
 * @author michael
 */
public class StoredProcedureAuthenticationProvider implements AuthenticationProvider {

    public static final String ROLE_ROOMACCESS_PREFIX = "ROLE_ROOMACCESS_";
    private static final Logger LOG = LoggerFactory.getLogger(StoredProcedureAuthenticationProvider.class);
    private JdbcTemplate jdbcTemplate;
    private String encryptionKey;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    @PostConstruct
    protected void init() {
        Assert.notNull(encryptionKey, "Encryption key not set.");
    }
    
    /**
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        String username = token.getName();
        String password = (String) token.getCredentials();
        String passwordHash = encryptPassword(password + " " + username);
       
        LOG.debug("Authenticating user [auth={}]", authentication);
        
        try {
            boolean authenticated = this.jdbcTemplate.queryForObject("select verify_hall_login(?,?,?)", Boolean.class, username, passwordHash, 
                    encryptionKey);

            if(!authenticated) {
                throw new BadCredentialsException("Username or password is not valid.");
            }

            List<GrantedAuthority> authList = new ArrayList<>();

            authList.add(new SimpleGrantedAuthority(ROLE_ROOMACCESS_PREFIX + username));
            authList.add(new SimpleGrantedAuthority("ROLE_USER"));
            authList.add(new SimpleGrantedAuthority("ROLE_HOST"));

            return new UsernamePasswordAuthenticationToken(username, password, authList);
        } catch(DataAccessException ex) {
            LOG.error("Error when communicating with database.", ex);
            throw new AuthenticationServiceException("Unable to authenticate user because of error while communicating with backendprovider.", ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private static String encryptPassword(String password) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException|UnsupportedEncodingException e) {
            LOG.error("Unable to encrypt password.", e);
            return null;
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash) {
        String result;
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            result = formatter.toString();
            formatter.close();
        }
        return result;
    }
}
