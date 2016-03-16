/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.adapters.saml;

import org.keycloak.adapters.saml.profile.SamlAuthenticationHandler;
import org.keycloak.adapters.saml.profile.webbrowsersso.BrowserHandler;
import org.keycloak.adapters.saml.profile.webbrowsersso.SamlEndpoint;
import org.keycloak.adapters.spi.HttpFacade;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CatalinaSamlEndpoint extends SamlAuthenticator {
    public CatalinaSamlEndpoint(HttpFacade facade, SamlDeployment deployment, SamlSessionStore sessionStore) {
        super(facade, deployment, sessionStore);
    }

    @Override
    protected void completeAuthentication(SamlSession account) {
        // complete
    }

    @Override
    protected SamlAuthenticationHandler createBrowserHandler(HttpFacade facade, SamlDeployment deployment, SamlSessionStore sessionStore) {
        return new SamlEndpoint(facade, deployment, sessionStore);
    }


}
