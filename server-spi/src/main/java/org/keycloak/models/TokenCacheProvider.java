package org.keycloak.models;

import org.keycloak.provider.Provider;

public interface TokenCacheProvider extends Provider {

    String getToken(String guid);

    /**
     *
     * @param guid
     * @param token
     * @param expiration
     * @param ttl in seconds
     * @param username
     * @param realm
     */
    void addToken(String guid, String token, long expiration, long ttl, String username, String realm);
    void removeToken(String guid);

    void close();

    void refreshTokens();

}