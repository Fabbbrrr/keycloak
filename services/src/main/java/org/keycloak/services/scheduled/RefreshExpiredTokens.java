package org.keycloak.services.scheduled;

import org.keycloak.models.KeycloakSession;
import org.keycloak.timer.ScheduledTask;

/*
    Sportsbet - Scheduled task to refresh oxi tokens
 */
public class RefreshExpiredTokens implements ScheduledTask {

    @Override
    public void run(KeycloakSession session) {

        session.tokens().refreshTokens();

    }

}