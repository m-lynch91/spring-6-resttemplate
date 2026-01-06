package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClient;

    @Test
    void listBeersNoName() {
        beerClient.listBeers(null, null, null, null, null);
    }

    @Test
    void listBeersByName() {
        beerClient.listBeers("ALE", null, null, null, null);
    }

    @Test
    void listBeersByStyle() {
        beerClient.listBeers(null, BeerStyle.LAGER, null, null, null);
    }

    @Test
    void listBeersByInventory() {
        beerClient.listBeers(null, null, true, null, null);
    }
}