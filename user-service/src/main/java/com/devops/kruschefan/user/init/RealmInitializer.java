package com.devops.kruschefan.user.init;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RealmInitializer {

    private final Keycloak keycloak;

    @Value("${keycloak.realm:${KEYCLOAK_REALM}}")
    private String realm;

    public RealmInitializer(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @PostConstruct
    public void initRealmIfNotExists() {
        List<RealmRepresentation> realms = keycloak.realms().findAll();
        boolean exists = realms.stream().anyMatch(r -> r.getRealm().equalsIgnoreCase(realm));

        if (!exists) {
            log.info("Realm '{}' does not exist. Creating...", realm);
            RealmRepresentation newRealm = new RealmRepresentation();
            newRealm.setRealm(realm);
            newRealm.setEnabled(true);

            keycloak.realms().create(newRealm);
            log.info("Realm '{}' successfully created.", realm);
        } else {
            log.info("Realm '{}' already exists.", realm);
        }
    }
}
