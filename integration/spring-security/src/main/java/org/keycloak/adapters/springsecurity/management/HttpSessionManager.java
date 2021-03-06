package org.keycloak.adapters.springsecurity.management;

import org.keycloak.adapters.spi.UserSessionManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.List;

/**
 * User session manager for handling logout of Spring Secured sessions.
 *
 * @author <a href="mailto:srossillo@smartling.com">Scott Rossillo</a>
 * @version $Revision: 1 $
 */
@Component
public class HttpSessionManager implements HttpSessionListener, UserSessionManagement {

    private static final Logger log = LoggerFactory.getLogger(HttpSessionManager.class);
    private SessionManagementStrategy sessions = new LocalSessionManagementStrategy();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        log.debug("Session created: {}", event.getSession().getId());
        HttpSession session = event.getSession();
        sessions.store(session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        sessions.remove(event.getSession().getId());
    }

    @Override
    public void logoutAll() {
        log.info("Received request to log out all users.");
        for (HttpSession session : sessions.getAll()) {
            session.invalidate();
        }
        sessions.clear();
    }

    @Override
    public void logoutHttpSessions(List<String> ids) {
        log.info("Received request to log out {} session(s): {}", ids.size(), ids);
        for (String id : ids) {
            HttpSession session = sessions.remove(id);
            if (session != null) {
                session.invalidate();
            }
        }
        sessions.clear();
    }
}
