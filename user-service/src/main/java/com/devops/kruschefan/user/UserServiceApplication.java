package com.devops.kruschefan.user;

import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class UserServiceApplication {

    @Autowired
    private Keycloak keycloak;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @PostConstruct
    public void initRealm() {
        System.out.println("Running Realm initializer...");

        String realmName = "FormsAI";
        boolean exists = keycloak.realms().findAll()
            .stream()
            .anyMatch(r -> r.getRealm().equals(realmName));

        if (!exists) {
            RealmRepresentation realm = new RealmRepresentation();
            realm.setRealm(realmName);
            realm.setEnabled(true);
            keycloak.realms().create(realm);
            System.out.println("Realm created: " + realmName);
        } else {
            System.out.println("Realm already exists: " + realmName);
        }
    }

}