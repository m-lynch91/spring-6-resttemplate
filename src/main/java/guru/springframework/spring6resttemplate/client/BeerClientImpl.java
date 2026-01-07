package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    public static final String GET_BEERS_PATH = "/api/v1/beers";
    public static final String GET_BEERS_BY_ID_PATH = "/api/v1/beers/{beerId}";

    private final RestTemplateBuilder restTemplateBuilder;

    @Override
    public Page<BeerDTO> getBeers() {
        return this.getBeers(null, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> getBeers(String beerName) {
        return this.getBeers(beerName, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> getBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        // Spring will use the base URL from RestTemplate in conjuction with GET_BEERS_PATH
        // components builder allows for us to utilize query params easier
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEERS_PATH);

        if (beerName != null) {
            uriComponentsBuilder.queryParam("beerName", beerName);
        }
        if (beerStyle != null) {
            uriComponentsBuilder.queryParam("beerStyle", beerStyle);
        }
        if (showInventory != null) {
            uriComponentsBuilder.queryParam("showInventory", showInventory);
        }
        if (pageNumber != null) {
            uriComponentsBuilder.queryParam("pageNumber", pageNumber);
        }
        if (pageSize != null) {
            uriComponentsBuilder.queryParam("pageSize", pageSize);
        }

        ResponseEntity<BeerDTOPageImpl> response = restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

        return response.getBody();
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate.getForObject(GET_BEERS_BY_ID_PATH, BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO beerDTO) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        URI uri = restTemplate.postForLocation(GET_BEERS_PATH, beerDTO);

        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO beerDTO) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.put(GET_BEERS_BY_ID_PATH, beerDTO, beerDTO.getId());

        return getBeerById(beerDTO.getId());
    }

    @Override
    public void deleteBeer(UUID id) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.delete(GET_BEERS_BY_ID_PATH, id);
    }


}
