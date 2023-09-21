package br.com.codeflix.catalog.admin.infrastructure.configuration;

import br.com.codeflix.catalog.admin.infrastructure.configuration.properties.google.GoogleStorageProperties;
import br.com.codeflix.catalog.admin.infrastructure.configuration.properties.storage.StorageProperties;
import br.com.codeflix.catalog.admin.infrastructure.services.StorageService;
import br.com.codeflix.catalog.admin.infrastructure.services.impl.GoogleCloudStorageService;
import br.com.codeflix.catalog.admin.infrastructure.services.local.InMemoryStorageService;
import com.google.cloud.storage.Storage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class StorageConfig {

    @Bean
    @ConfigurationProperties(value = "storage.video-catalog")
    public StorageProperties storageProperties() {
        return new StorageProperties();
    }

    @Bean(name = "storageService")
    @Profile({"development", "integration-test", "e2e-test"})
    public StorageService inMemoryStorageService() {
        return new InMemoryStorageService();
    }

    @Bean(name = "storageService")
    @ConditionalOnMissingBean
    public StorageService googleCloudStorageService(final GoogleStorageProperties properties, final Storage storage) {
        return new GoogleCloudStorageService(properties.getBucket(), storage);
    }
}
