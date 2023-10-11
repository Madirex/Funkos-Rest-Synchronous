package com.madirex;

import com.madirex.exceptions.FunkoException;
import com.madirex.exceptions.FunkoNotFoundException;
import com.madirex.exceptions.FunkoNotRemovedException;
import com.madirex.models.Funko;
import com.madirex.models.Model;
import com.madirex.repositories.funko.FunkoRepositoryImpl;
import com.madirex.services.crud.funko.FunkoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkosServiceImplTest {

    @Mock
    FunkoRepositoryImpl repository;

    @InjectMocks
    FunkoServiceImpl service;

    //TODO: EDIT, FIX AND FINISH
    @Test
    void testFindAll() throws SQLException {
        var funkos = List.of(
                Funko.builder().name("test1").price(42.0).build(),
                Funko.builder().name("test2").price(42.24).build()
        );
        when(repository.findAll()).thenReturn(funkos);
        var result = service.findAll();
        assertAll("findAll",
                () -> assertEquals(result.size(), 2, "No se han recuperado 2 Funkos"),
                () -> assertEquals(result.get(0).getName(), "test1", "El primer Funko no es el esperado"),
                () -> assertEquals(result.get(1).getName(), "test2", "El segundo Funko no es el esperado"),
                () -> assertEquals(result.get(0).getPrice(), 42.0, "La calificación del primer Funko no es la esperada"),
                () -> assertEquals(result.get(1).getPrice(), 42.24, "La calificación del segundo Funko no es la esperada")
        );
        verify(repository, times(1)).findAll();
    }

    //TODO: EDIT, FIX AND FINISH
    @Test
    void testFindByName() throws SQLException, FunkoNotFoundException {
        var funkos = List.of(Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build());
        when(repository.findByName("cuack")).thenReturn(funkos);
        var result = service.findByName("cuack");
        assertAll("update",
                () -> assertEquals(result.get(0).getName(), funkos.get(0).getName(), "El Funko no tiene el nombre esperado"), //TODO: get
                () -> assertEquals(result.get(0).getPrice(), funkos.get(0).getPrice(), "El precio del Funko no es el esperado"), //TODO: get
                () -> assertEquals(result.get(0).getReleaseDate(), funkos.get(0).getReleaseDate(), "La fecha de lanzamiento del Funko no es la esperada"), //TODO: get
                () -> assertEquals(result.get(0).getModel(), funkos.get(0).getModel(), "El modelo del Funko no es el esperado") //TODO: get
        );
        verify(repository, times(1)).findByName("cuack");
    }

    //TODO: EDIT, FIX AND FINISH
    @Test
    void testFindById() throws SQLException, FunkoNotFoundException {
        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build();
        String id = funko.getCod().toString();
        when(repository.findById(id)).thenReturn(Optional.of(funko));
        var result = service.findById(id);
        assertAll("update",
                () -> assertEquals(result.get().getName(), funko.getName(), "El Funko no tiene el nombre esperado"), //TODO: get
                () -> assertEquals(result.get().getPrice(), funko.getPrice(), "El precio del Funko no es el esperado"), //TODO: get
                () -> assertEquals(result.get().getReleaseDate(), funko.getReleaseDate(), "La fecha de lanzamiento del Funko no es la esperada"), //TODO: get
                () -> assertEquals(result.get().getModel(), funko.getModel(), "El modelo del Funko no es el esperado") //TODO: get
        );
        verify(repository, times(1)).findById(id);
    }

    //TODO: EDIT, FIX AND FINISH
    @Test
    void testFindByIdNotExists() throws SQLException, FunkoNotFoundException {
        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build();
        String id = funko.getCod().toString();
        when(repository.findById("d23574dc-d8b0-42c0-ad11-01db6aaca605")).thenReturn(Optional.empty());
        var result = service.findById("d23574dc-d8b0-42c0-ad11-01db6aaca605");
    }

    //TODO: EDIT, FIX AND FINISH
    @Test
    void testSave() throws SQLException, FunkoException {
        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build();
        when(repository.save(funko)).thenReturn(Optional.of(funko));
        var result = service.save(funko);
        assertAll("update",
                () -> assertEquals(result.get().getName(), funko.getName(), "El Funko no tiene el nombre esperado"), //TODO: get
                () -> assertEquals(result.get().getPrice(), funko.getPrice(), "El precio del Funko no es el esperado"), //TODO: get
                () -> assertEquals(result.get().getReleaseDate(), funko.getReleaseDate(), "La fecha de lanzamiento del Funko no es la esperada"), //TODO: get
                () -> assertEquals(result.get().getModel(), funko.getModel(), "El modelo del Funko no es el esperado") //TODO: get
        );
        verify(repository, times(1)).save(funko);
    }

    //TODO: EDIT, FIX AND FINISH
    @Test
    void testUpdate() throws SQLException, FunkoException {
        LocalDate date = LocalDate.now();
        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(date).model(Model.DISNEY).build();
        String id = funko.getCod().toString();
        when(repository.update(id, funko)).thenReturn(Optional.of(funko));
        var result = service.update(id, funko);
        assertAll("update",
                () -> assertEquals(result.get().getName(), funko.getName(), "El Funko no tiene el nombre esperado"), //TODO: get
                () -> assertEquals(result.get().getPrice(), funko.getPrice(), "El precio del Funko no es el esperado"), //TODO: get
                () -> assertEquals(result.get().getReleaseDate(), funko.getReleaseDate(), "La fecha de lanzamiento del Funko no es la esperada"), //TODO: get
                () -> assertEquals(result.get().getModel(), funko.getModel(), "El modelo del Funko no es el esperado") //TODO: get
        );
        verify(repository, times(1)).update(id, funko);
    }

    //TODO: EDIT, FIX AND FINISH
//    @Test //TODO: DO
//    void testUpdateNotExists() throws SQLException, FunkoNotValidException {
//        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build();
//        String id = funko.getCod().toString();
//        when(repository.update(id,funko)).thenThrow(new FunkoNotFoundException(id));
//        try {
//            var result = service.update(id, funko);
//        } catch (FunkoNotValidException ex) {
//            assertEquals(ex.getMessage(), "El Funko no es válido", "Msg");
//        }
//        verify(repository, times(1)).update(funko);
//    }

    //TODO: EDIT, FIX AND FINISH
    @Test
    void testDelete() throws SQLException, FunkoNotRemovedException {
        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build();
        String id = funko.getCod().toString();
        when(repository.delete(id)).thenReturn(true);
        var result = service.delete(id);
        assertTrue(result, "No se ha borrado el funko");
        verify(repository, times(1)).delete(id);
    }

    //TODO: EDIT, FIX AND FINISH
    @Test
    void testDeleteNotExists() throws SQLException, FunkoNotRemovedException {
        when(repository.delete("63161c2e-1602-44b5-bd8b-3b424f7b2b4c")).thenReturn(false);
        var result = service.delete("63161c2e-1602-44b5-bd8b-3b424f7b2b4c");
    }
}