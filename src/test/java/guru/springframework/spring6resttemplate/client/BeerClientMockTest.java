package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.BindErrorUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
public class BeerClientMockTest {

    static final String URL = "http://localhost:8080";

    BeerClient beerClient;

    MockRestServiceServer server;

    @Autowired
    RestTemplateBuilder restTemplateBuilderConfigured;

    @Autowired
    ObjectMapper objectMapper; // jackson object mapper

    @Mock
    RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    BeerDTO testDto;
    String dtoJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        RestTemplate restTemplate = restTemplateBuilderConfigured.build();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
        beerClient = new BeerClientImpl(mockRestTemplateBuilder);
        testDto = getBeerDto();
        dtoJson = objectMapper.writeValueAsString(testDto);
    }

    @Test
    void testGetBeers() throws JsonProcessingException {
        // arrange
        // create json payload that our mock server will return
        String payload = objectMapper.writeValueAsString(getPage());

        // act
        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEERS_PATH))
                .andRespond(withSuccess(payload, APPLICATION_JSON));
        Page<BeerDTO> dtos = beerClient.getBeers();

        // assert
        assertThat(dtos.getContent().size()).isGreaterThan(0);
    }

    @Test
    void testGetBeerById() {
        // act
        mockGetOperation();
        BeerDTO responseDto = beerClient.getBeerById(testDto.getId());

        // assert
        assertThat(responseDto.getId()).isEqualTo(testDto.getId());
    }



    @Test
    void testCreateBeer() {
        // arrange
        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEERS_BY_ID_PATH)
                .build(testDto.getId());

        // act
        server.expect(method(HttpMethod.POST))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEERS_PATH))
                .andRespond(withAccepted().location(uri));
        mockGetOperation();

        BeerDTO responseDto = beerClient.createBeer(testDto);

        // assert
        assertThat(responseDto.getId()).isEqualTo(testDto.getId());
    }

    @Test
    void testUpdateBeer() {
        // arrange

        // act
        server.expect(method(HttpMethod.PUT))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEERS_BY_ID_PATH, testDto.getId()))
                .andRespond((withNoContent()));

        mockGetOperation();

        BeerDTO responseDto = beerClient.updateBeer(testDto);

        // assert
        assertThat(responseDto.getId()).isEqualTo(testDto.getId());
    }

    @Test
    void testDeleteBeer() {
        // arrange

        // act
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEERS_BY_ID_PATH, testDto.getId()))
                .andRespond(withNoContent());

        beerClient.deleteBeer(testDto.getId());

        // assert
        server.verify();
    }

    @Test
    void testDeleteNotFound() {
        // arrange

        // act
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEERS_BY_ID_PATH, testDto.getId()))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class, () -> {
            beerClient.deleteBeer(testDto.getId());
        });

        // assert
        server.verify();
    }






    // ----------------- Helper Methods -----------------//
    private void mockGetOperation() {
        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEERS_BY_ID_PATH, testDto.getId()))
                .andRespond(withSuccess(dtoJson, APPLICATION_JSON));
    }

    BeerDTO getBeerDto() {
        return BeerDTO.builder()
                .id(UUID.randomUUID())
                .price(new BigDecimal("10.99"))
                .beerName("Test Beer")
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123456789012")
                .build();
    }

    BeerDTOPageImpl getPage() {
        return new BeerDTOPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
    }
}
