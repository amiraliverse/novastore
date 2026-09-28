package com.app.novastore;

import com.app.novastore.util.DefaultProfileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;

@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@ConfigurationPropertiesScan
@Slf4j
@EnableCaching
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.app.novastore"}, exclude = {ErrorMvcAutoConfiguration.class})
public class NovastoreApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(NovastoreApplication.class);
        DefaultProfileUtils.addDefaultProfile(application);
        Environment env = application.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("""

                        ----------------------------------------------------------
                        \tApplication '{}' is running! Access URLs:
                        \tLocal: \t\t{}://localhost:{}
                        \tExternal: \t{}://{}:{}
                        \tProfile(s): \t{}
                        ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                protocol,
                env.getProperty("server.port"),
                protocol,
                hostAddress,
                env.getProperty("server.port"),
                DefaultProfileUtils.getActiveProfiles(env));
    }

}
