package com.madirex;

import com.madirex.exceptions.FunkoException;
import com.madirex.exceptions.FunkoNotFoundException;
import com.madirex.models.Funko;
import com.madirex.models.Model;
import com.madirex.repositories.funko.FunkoRepository;
import com.madirex.repositories.funko.FunkoRepositoryImpl;
import com.madirex.services.database.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FunkosRepositoryTestDB {

    private FunkoRepository funkoRepository;

    @BeforeEach
    void setUp() throws SQLException {
        funkoRepository = FunkoRepositoryImpl.getInstance(DatabaseManager.getInstance());
        //funkoRepository.deleteAll(); //TODO: DO
    }

    @AfterEach
    void tearDown() throws SQLException {
        //funkoRepository.deleteAll(); //TODO: DO
    }

    @Test
    void testSaveFunko() throws SQLException, FunkoException {
        LocalDate date = LocalDate.now();
        Funko funko = Funko.builder()
                .name("Test")
                .model(Model.OTROS)
                .price(23.13)
                .releaseDate(date)
                .build();
        Optional<Funko> savedFunko = funkoRepository.save(funko);
        assertAll(() -> assertNotNull(savedFunko),
                () -> assertNotNull(savedFunko.get().getCod()), //TODO: check get
                () -> assertEquals(funko.getName(), savedFunko.get().getName()), //TODO: check get
                () -> assertEquals(funko.getModel(), savedFunko.get().getModel()), //TODO: check get
                () -> assertEquals(funko.getPrice(), savedFunko.get().getPrice()), //TODO: check get
                () -> assertEquals(funko.getReleaseDate(), savedFunko.get().getReleaseDate()) //TODO: check get
        );
    }

    @Test
    void testFindFunkoById() throws SQLException, FunkoException {
        LocalDate date = LocalDate.now();
        Funko funko = Funko.builder()
                .name("Test")
                .model(Model.OTROS)
                .price(23.12)
                .releaseDate(date)
                .build();
        Optional<Funko> savedFunko = funkoRepository.save(funko);
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.get().getCod().toString()); //TODO: check get
        assertAll(() -> assertTrue(foundFunko.isPresent()),
                () -> assertEquals(funko.getName(), foundFunko.get().getName()), //TODO: check get
                () -> assertEquals(funko.getModel(), foundFunko.get().getModel()), //TODO: check get
                () -> assertEquals(funko.getPrice(), foundFunko.get().getPrice()), //TODO: check get
                () -> assertEquals(funko.getReleaseDate(), foundFunko.get().getReleaseDate()), //TODO: check get
                () -> assertNotNull(foundFunko.get().getCod()) //TODO: check get
        );
    }

    @Test
    void testFindAllFunkos() throws SQLException, FunkoException {
        funkoRepository.save(Funko.builder()
                .name("test1")
                .model(Model.ANIME)
                .price(12.52)
                .releaseDate(LocalDate.now())
                .build());
        funkoRepository.save(Funko.builder()
                .name("test2")
                .model(Model.ANIME)
                .price(28.52)
                .releaseDate(LocalDate.now())
                .build());
        assertEquals(2, funkoRepository.findAll().size());
    }

    @Test
    void testFindFunkosByName() throws SQLException, FunkoException {
        LocalDate date = LocalDate.now();
        Funko funko1 = Funko.builder()
                .name("test1")
                .model(Model.ANIME)
                .price(42.23)
                .releaseDate(date)
                .build();
        Funko funko2 = Funko.builder()
                .name("test2")
                .model(Model.OTROS)
                .price(81.23)
                .releaseDate(date)
                .build();
        funkoRepository.save(funko1);
        funkoRepository.save(funko2);
        List<Funko> foundFunkos = funkoRepository.findByName("test1");
        assertAll(() -> assertNotNull(foundFunkos),
                () -> assertEquals(2, foundFunkos.size()),
                () -> assertEquals(foundFunkos.get(0).getName(), funko1.getName()),
                () -> assertEquals(foundFunkos.get(0).getPrice(), funko1.getPrice()),
                () -> assertEquals(foundFunkos.get(0).getReleaseDate(), funko1.getReleaseDate()),
                () -> assertEquals(foundFunkos.get(0).getModel(), funko1.getModel()),
                () -> assertEquals(foundFunkos.get(1).getName(), funko2.getName()),
                () -> assertEquals(foundFunkos.get(1).getPrice(), funko2.getPrice()),
                () -> assertEquals(foundFunkos.get(1).getReleaseDate(), funko2.getReleaseDate()),
                () -> assertEquals(foundFunkos.get(1).getModel(), funko2.getModel())
        );
    }

    @Test
    void testUpdateFunko() throws SQLException, FunkoException {
        Funko funko = Funko.builder()
                .name("Test")
                .model(Model.ANIME)
                .price(4.42)
                .releaseDate(LocalDate.now())
                .build();
        Optional<Funko> savedFunko = funkoRepository.save(funko);
        savedFunko.get().setName("Updated"); //TODO: check get
        savedFunko.get().setPrice(42.43); //TODO: check get
        funkoRepository.update(funko.getCod().toString(), savedFunko.get()); //TODO: check get
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.get().getCod().toString()); //TODO: check get
        assertAll(() -> assertTrue(foundFunko.isPresent()),
                () -> assertEquals(savedFunko.get().getName(), foundFunko.get().getName()), //TODO: check get
                () -> assertEquals(savedFunko.get().getPrice(), foundFunko.get().getPrice()) //TODO: check get
        );
    }

    @Test
    void testUpdateFunkoNotExists() throws SQLException {
        Funko funko = Funko.builder()
                .name("Test")
                .model(Model.ANIME)
                .price(4.42)
                .releaseDate(LocalDate.now())
                .build();
        Exception exception = assertThrows(FunkoNotFoundException.class, () -> {
            funkoRepository.update("5aec845c-2e06-4b7d-8d5a-4e0a0e23fb16", funko);
        });
        String expectedMessage = "Funko/a no encontrado con id: " + "5aec845c-2e06-4b7d-8d5a-4e0a0e23fb16";

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testDeleteFunko() throws SQLException, FunkoException {
        Funko funko = Funko.builder()
                .name("Test")
                .model(Model.ANIME)
                .price(4.42)
                .releaseDate(LocalDate.now())
                .build();
        Optional<Funko> savedFunko = funkoRepository.save(funko);
        funkoRepository.delete(savedFunko.get().getCod().toString()); //TODO: CHEQUEAR GET
        Optional<Funko> foundFunko = funkoRepository.findById(savedFunko.get().getCod().toString()); //TODO: CHEQUEAR GET
        assertAll(() -> assertFalse(foundFunko.isPresent())
        );
    }

    @Test
    void testDeleteFunkoNotExists() throws SQLException {
        boolean deleted = funkoRepository.delete("cac4c061-20ec-4e87-ad3a-b1a7ea12facc");
        assertFalse(deleted);
    }
}