package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String GET_BEERS_PATH = "/api/v1/beers";

    private final RestTemplateBuilder restTemplateBuilder;

    @Override
    public Page<BeerDTO> listBeers() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<String> stringResponse = restTemplate.getForEntity(BASE_URL + GET_BEERS_PATH, String.class);
        ResponseEntity<Map> mapResponse = restTemplate.getForEntity(BASE_URL + GET_BEERS_PATH, Map.class);
        ResponseEntity<JsonNode> jsonResponse = restTemplate.getForEntity(BASE_URL + GET_BEERS_PATH, JsonNode.class);

        System.out.println(stringResponse.getBody());
        jsonResponse.getBody().findPath("content")
                .elements()
                .forEachRemaining(node -> System.out.println(node.get("beerName").asText()));

        return null;
    }
}
