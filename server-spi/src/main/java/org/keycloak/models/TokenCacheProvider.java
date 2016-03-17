package org.keycloak.models;

import org.keycloak.provider.Provider;

public interface TokenCacheProvider extends Provider {

    String getToken(String guid);
    void addToken(String guid, String token, long expiration, long ttl, String username, String realm);
    void removeToken(String guid);

    void close();

    void refreshTokensWithTTL(long i);

}