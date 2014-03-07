package dk.drb.blacktiger.security;

import dk.drb.blacktiger.repository.jdbc.JdbcUserRepository;
import static dk.drb.blacktiger.security.SystemUserDetailsManager.ROLE_ROOMACCESS_PREFIX;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author michael
 */
public class StoredProcedureAuthenticationProvider implements AuthenticationProvider {

    /**
     * Users 'H45-8654', '643+25Airp7x' 'H45-9000-1', 'Ftddrnyw9k@z' 'H45-9000-2', '72p9an#dvFbz'
     */
    private static final String ENC_KEY = "{ -------- telesal.org -------- }";
    private static final Logger LOG = LoggerFactory.getLogger(JdbcUserRepository.class);
    private static final String TABLE_NAME = "sip";
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
       
        try {
            boolean authenticated = this.jdbcTemplate.queryForObject("select verify_hall_login(?,?,?)", Boolean.class, username, passwordHash, 
                    ENC_KEY);

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
