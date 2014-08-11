package org.keycloak.services.resources.admin;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.spi.NotFoundException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserFederationProvider;
import org.keycloak.models.UserFederationProviderFactory;
import org.keycloak.models.UserFederationProviderModel;
import org.keycloak.models.utils.ModelToRepresentation;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.representations.idm.UserFederationProviderFactoryRepresentation;
import org.keycloak.representations.idm.UserFederationProviderRepresentation;
import org.keycloak.services.managers.PeriodicSyncManager;
import org.keycloak.timer.TimerProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.LinkedList;
import java.util.List;

/**
 * Base resource for managing users
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UserFederationResource {
    protected static final Logger logger = Logger.getLogger(UserFederationResource.class);

    protected RealmModel realm;

    protected  RealmAuth auth;

    @Context
    protected UriInfo uriInfo;

    @Context
    protected KeycloakSession session;

    public UserFederationResource(RealmModel realm, RealmAuth auth) {
        this.auth = auth;
        this.realm = realm;

        auth.init(RealmAuth.Resource.USER);
    }

    /**
     * Get List of available provider factories
     *
     * @return
     */
    @GET
    @NoCache
    @Path("providers")
    @Produces("application/json")
    public List<UserFederationProviderFactoryRepresentation> getProviders() {
        logger.info("get provider list");
        auth.requireView();
        List<UserFederationProviderFactoryRepresentation> providers = new LinkedList<UserFederationProviderFactoryRepresentation>();
        for (ProviderFactory factory : session.getKeycloakSessionFactory().getProviderFactories(UserFederationProvider.class)) {
            UserFederationProviderFactoryRepresentation rep = new UserFederationProviderFactoryRepresentation();
            rep.setId(factory.getId());
            rep.setOptions(((UserFederationProviderFactory)factory).getConfigurationOptions());
            providers.add(rep);
        }
        logger.info("provider list.size() " + providers.size());
        return providers;
    }

    /**
     * Get List of available provider factories
     *
     * @return
     */
    @GET
    @NoCache
    @Path("providers/{id}")
    @Produces("application/json")
    public UserFederationProviderFactoryRepresentation getProvider(@PathParam("id") String id) {
        logger.info("get provider list");
        auth.requireView();
        for (ProviderFactory factory : session.getKeycloakSessionFactory().getProviderFactories(UserFederationProvider.class)) {
            if (!factory.getId().equals(id)) {
                continue;
            }
            UserFederationProviderFactoryRepresentation rep = new UserFederationProviderFactoryRepresentation();
            rep.setId(factory.getId());
            rep.setOptions(((UserFederationProviderFactory)factory).getConfigurationOptions());
            return rep;
        }
        throw new NotFoundException("Could not find provider");
    }

    /**
     * Create a provider
     *
     * @param rep
     * @return
     */
    @POST
    @Path("instances")
    @Consumes("application/json")
    public Response createProviderInstance(UserFederationProviderRepresentation rep) {
        logger.info("createProvider");
        auth.requireManage();
        String displayName = rep.getDisplayName();
        if (displayName != null && displayName.trim().equals("")) {
            displayName = null;
        }
        UserFederationProviderModel model = realm.addUserFederationProvider(rep.getProviderName(), rep.getConfig(), rep.getPriority(), displayName,
                rep.getFullSyncPeriod(), rep.getChangedSyncPeriod(), rep.getLastSync());
        new PeriodicSyncManager().startPeriodicSyncForProvider(session.getKeycloakSessionFactory(), session.getProvider(TimerProvider.class), model, realm.getId());

        return Response.created(uriInfo.getAbsolutePathBuilder().path(model.getId()).build()).build();
    }

    /**
     * Update a provider
     *
     * @param id
     * @param rep
     */
    @PUT
    @Path("instances/{id}")
    @Consumes("application/json")
    public void updateProviderInstance(@PathParam("id") String id, UserFederationProviderRepresentation rep) {
        logger.info("updateProvider");
        auth.requireManage();
        String displayName = rep.getDisplayName();
        if (displayName != null && displayName.trim().equals("")) {
            displayName = null;
        }
        UserFederationProviderModel model = new UserFederationProviderModel(id, rep.getProviderName(), rep.getConfig(), rep.getPriority(), displayName,
                rep.getFullSyncPeriod(), rep.getChangedSyncPeriod(), rep.getLastSync());
        realm.updateUserFederationProvider(model);
        new PeriodicSyncManager().startPeriodicSyncForProvider(session.getKeycloakSessionFactory(), session.getProvider(TimerProvider.class), model, realm.getId());
    }

    /**
     * get a provider
     *
     * @param id
     */
    @GET
    @NoCache
    @Path("instances/{id}")
    @Consumes("application/json")
    public UserFederationProviderRepresentation getProviderInstance(@PathParam("id") String id) {
        logger.info("getProvider");
        auth.requireView();
        for (UserFederationProviderModel model : realm.getUserFederationProviders()) {
            if (model.getId().equals(id)) {
                return ModelToRepresentation.toRepresentation(model);
            }
        }
        throw new NotFoundException("could not find provider");
    }

    /**
     * Delete a provider
     *
     * @param id
     */
    @DELETE
    @Path("instances/{id}")
    public void deleteProviderInstance(@PathParam("id") String id) {
        logger.info("deleteProvider");
        auth.requireManage();
        UserFederationProviderModel model = new UserFederationProviderModel(id, null, null, -1, null, -1, -1, 0);
        realm.removeUserFederationProvider(model);
        new PeriodicSyncManager().removePeriodicSyncForProvider(session.getProvider(TimerProvider.class), model);
    }


    /**
     * list configured providers
     *
     * @return
     */
    @GET
    @Path("instances")
    @Produces("application/json")
    public List<UserFederationProviderRepresentation> getUserFederationInstances() {
        logger.info("getUserFederationInstances");
        auth.requireManage();
        List<UserFederationProviderRepresentation> reps = new LinkedList<UserFederationProviderRepresentation>();
        for (UserFederationProviderModel model : realm.getUserFederationProviders()) {
            UserFederationProviderRepresentation rep = ModelToRepresentation.toRepresentation(model);
            reps.add(rep);
        }
        return reps;
    }


}