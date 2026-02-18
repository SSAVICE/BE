package teamssavice.ssavice.global.config;

import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import teamssavice.ssavice.global.property.OpenSearchProperties;

@Configuration
@RequiredArgsConstructor
public class OpenSearchConfig {

    private final OpenSearchProperties openSearchProperties;

    @Bean
    public OpenSearchClient openSearchClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost(
                        openSearchProperties.scheme(),
                        openSearchProperties.host(),
                        openSearchProperties.port()
                )
        ).build();

        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        return new OpenSearchClient(transport);
    }
}