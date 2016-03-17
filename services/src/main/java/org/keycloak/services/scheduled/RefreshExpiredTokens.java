package org.keycloak.services.scheduled;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
/*
    Sportsbet - Scheduled task to refresh oxi tokens
 */
public class RefreshExpiredTokens implements ScheduledTask {

    @Override
    public void run(KeycloakSession session) {

        long ttl = Config.scope("cache").scope("tokens").getLong("ttl");
        session.tokens().refreshTokensWithTTL(ttl);

    }

}