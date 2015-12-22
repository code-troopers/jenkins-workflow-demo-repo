package com.codetroopers.demo.jenkins;

import restx.config.ConfigLoader;
import restx.config.ConfigSupplier;
import restx.factory.Provides;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import restx.security.*;
import restx.factory.Module;
import restx.factory.Provides;
import javax.inject.Named;

import java.nio.file.Paths;

@Module
public class AppModule {
    @Provides
    public SignatureKey signatureKey() {
         return new SignatureKey("8a5fbbcc-d063-47f0-a08a-196e294ced03 jenkins-workflow-demo jenkins-workflow-demo 6747048213989400763".getBytes(Charsets.UTF_8));
    }

    @Provides
    @Named("restx.admin.password")
    public String restxAdminPassword() {
        return "6460";
    }

    @Provides
    public ConfigSupplier appConfigSupplier(ConfigLoader configLoader) {
        // Load settings.properties in com.codetroopers.demo.jenkins package as a set of config entries
        return configLoader.fromResource("com/codetroopers/demo/jenkins/settings");
    }

    @Provides
    public CredentialsStrategy credentialsStrategy() {
        return new BCryptCredentialsStrategy();
    }

    @Provides
    public BasicPrincipalAuthenticator basicPrincipalAuthenticator(
            SecuritySettings securitySettings, CredentialsStrategy credentialsStrategy,
            @Named("restx.admin.passwordHash") String defaultAdminPasswordHash, ObjectMapper mapper) {
        return new StdBasicPrincipalAuthenticator(new StdUserService<>(
                // use file based users repository.
                // Developer's note: prefer another storage mechanism for your users if you need real user management
                // and better perf
                new FileBasedUserRepository<>(
                        StdUser.class, // this is the class for the User objects, that you can get in your app code
                        // with RestxSession.current().getPrincipal().get()
                        // it can be a custom user class, it just need to be json deserializable
                        mapper,

                        // this is the default restx admin, useful to access the restx admin console.
                        // if one user with restx-admin role is defined in the repository, this default user won't be
                        // available anymore
                        new StdUser("admin", ImmutableSet.<String>of("*")),

                        // the path where users are stored
                        Paths.get("data/users.json"),

                        // the path where credentials are stored. isolating both is a good practice in terms of security
                        // it is strongly recommended to follow this approach even if you use your own repository
                        Paths.get("data/credentials.json"),

                        // tells that we want to reload the files dynamically if they are touched.
                        // this has a performance impact, if you know your users / credentials never change without a
                        // restart you can disable this to get better perfs
                        true),
                credentialsStrategy, defaultAdminPasswordHash),
                securitySettings);
    }
}
