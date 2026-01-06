package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClient;

    @Test
    void listBeersNoName() {
        beerClient.getBeers(null, null, null, null, null);
    }

    @Test
    void listBeersByName() {
        beerClient.getBeers("ALE", null, null, null, null);
    }

    @Test
    void listBeersByStyle() {
        beerClient.getBeers(null, BeerStyle.LAGER, null, null, null);
    }

    @Test
    void listBeersByInventory() {
        beerClient.getBeers(null, null, true, null, null);
    }

    @Test
    void getBeerById() {
        Page<BeerDTO> beerDTOS = beerClient.getBeers(null, null, null, null, null);
        BeerDTO beerDTO = beerDTOS.getContent().getFirst();
        beerClient.getBeerById(beerDTO.getId());

        assertNotNull(beerDTO);
    }
}