package com.madirex;

import com.madirex.exceptions.*;
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

    @Test
    void testFindByName() throws SQLException, FunkoNotFoundException {
        var funkos = List.of(Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build());
        when(repository.findByName("cuack")).thenReturn(funkos);
        var result = service.findByName("cuack");
        assertAll("findByName",
                () -> assertEquals(result.get(0).getName(), funkos.get(0).getName(), "El Funko no tiene el nombre esperado"),
                () -> assertEquals(result.get(0).getPrice(), funkos.get(0).getPrice(), "El precio del Funko no es el esperado"),
                () -> assertEquals(result.get(0).getReleaseDate(), funkos.get(0).getReleaseDate(), "La fecha de lanzamiento del Funko no es la esperada"),
                () -> assertEquals(result.get(0).getModel(), funkos.get(0).getModel(), "El modelo del Funko no es el esperado")
        );
        verify(repository, times(2)).findByName("cuack");
    }

    @Test
    void testFindByNameNotFound() throws SQLException {
        when(repository.findByName("name")).thenReturn(List.of());
        assertThrows(FunkoNotFoundException.class, () -> service.findByName("name"));
    }

    @Test
    void testBackupDirectoryExists() {
        String path = "data";
        String fileName = "backup.json";
        service = new FunkoServiceImpl(repository);
        assertDoesNotThrow(() -> service.backup(path, fileName));
    }

    @Test
    void testBackupDirectoryNotExists() {
        String path = "ruta/inexistente";
        String fileName = "backup.json";
        service = new FunkoServiceImpl(repository);
        assertDoesNotThrow(() -> service.backup(path, fileName));
    }

    @Test
    void testFindById() throws SQLException {
        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build();
        String id = funko.getCod().toString();
        when(repository.findById(id)).thenReturn(Optional.of(funko));
        var result = service.findById(id);
        assertTrue(result.isPresent());
        assertAll("findById",
                () -> assertEquals(result.get().getName(), funko.getName(), "El Funko no tiene el nombre esperado"),
                () -> assertEquals(result.get().getPrice(), funko.getPrice(), "El precio del Funko no es el esperado"),
                () -> assertEquals(result.get().getReleaseDate(), funko.getReleaseDate(), "La fecha de lanzamiento del Funko no es la esperada"),
                () -> assertEquals(result.get().getModel(), funko.getModel(), "El modelo del Funko no es el esperado")
        );
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testFindByIdCached() throws SQLException, FunkoNotFoundException {
        Funko cachedFunko = Funko.builder()
                .name("Funko en caché")
                .price(20.0)
                .build();
        service.getCache().put(cachedFunko.getCod().toString(), cachedFunko);
        Optional<Funko> result = service.findById(cachedFunko.getCod().toString());
        assertTrue(result.isPresent());
        assertEquals(cachedFunko, result.get());
        verify(repository, never()).findById(anyString());
    }

    @Test
    void testSave() throws SQLException, FunkoException {
        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build();
        when(repository.save(funko)).thenReturn(Optional.of(funko));
        var result = service.save(funko);
        assertTrue(result.isPresent());
        assertAll("save",
                () -> assertEquals(result.get().getName(), funko.getName(), "El Funko no tiene el nombre esperado"),
                () -> assertEquals(result.get().getPrice(), funko.getPrice(), "El precio del Funko no es el esperado"),
                () -> assertEquals(result.get().getReleaseDate(), funko.getReleaseDate(), "La fecha de lanzamiento del Funko no es la esperada"),
                () -> assertEquals(result.get().getModel(), funko.getModel(), "El modelo del Funko no es el esperado")
        );
        verify(repository, times(1)).save(funko);
    }

    @Test
    void testSaveNotValid() throws SQLException {
        Funko funkoToSave = Funko.builder()
                .name("cuack")
                .price(-12.42)
                .releaseDate(LocalDate.now())
                .model(Model.DISNEY)
                .build();
        when(repository.save(funkoToSave)).thenReturn(Optional.empty());
        assertThrows(FunkoNotSavedException.class, () -> service.save(funkoToSave));
    }

    @Test
    void testUpdate() throws SQLException, FunkoException {
        LocalDate date = LocalDate.now();
        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(date).model(Model.DISNEY).build();
        String id = funko.getCod().toString();
        when(repository.update(id, funko)).thenReturn(Optional.of(funko));
        var result = service.update(id, funko);
        assertTrue(result.isPresent());
        assertAll("update",
                () -> assertEquals(result.get().getName(), funko.getName(), "El Funko no tiene el nombre esperado"),
                () -> assertEquals(result.get().getPrice(), funko.getPrice(), "El precio del Funko no es el esperado"),
                () -> assertEquals(result.get().getReleaseDate(), funko.getReleaseDate(), "La fecha de lanzamiento del Funko no es la esperada"),
                () -> assertEquals(result.get().getModel(), funko.getModel(), "El modelo del Funko no es el esperado")
        );
        verify(repository, times(1)).update(id, funko);
    }

    @Test
    void testUpdateNotValid() throws SQLException {
        Funko funkoToUpdate = Funko.builder()
                .name("cuack")
                .price(-12.42)
                .releaseDate(LocalDate.now())
                .model(Model.DISNEY)
                .build();
        when(repository.update(funkoToUpdate.getCod().toString(),funkoToUpdate)).thenReturn(Optional.empty());
        assertThrows(FunkoNotValidException.class, () -> service.update(funkoToUpdate.getCod().toString(),funkoToUpdate));
    }

    @Test
    void testDelete() throws SQLException, FunkoNotRemovedException {
        var funko = Funko.builder().name("cuack").price(12.42).releaseDate(LocalDate.now()).model(Model.DISNEY).build();
        String id = funko.getCod().toString();
        when(repository.delete(id)).thenReturn(true);
        var result = service.delete(id);
        assertTrue(result);
        verify(repository, times(1)).delete(id);
    }

    @Test
    void testDeleteNotExists() throws SQLException {
        when(repository.delete("63161c2e-1602-44b5-bd8b-3b424f7b2b4c")).thenReturn(false);
        assertThrows(FunkoNotRemovedException.class, () -> service.delete("63161c2e-1602-44b5-bd8b-3b424f7b2b4c"));
    }
}