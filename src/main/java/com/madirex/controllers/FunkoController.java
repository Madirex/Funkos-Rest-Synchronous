package com.madirex.controllers;

import com.madirex.exceptions.FunkoNotFoundException;
import com.madirex.exceptions.FunkoNotSavedException;
import com.madirex.exceptions.FunkoNotValidException;
import com.madirex.models.Funko;
import com.madirex.services.crud.funko.FunkoService;
import com.madirex.validators.FunkoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FunkoController implements BaseController<Funko> {
    private final Logger logger = LoggerFactory.getLogger(FunkoController.class);

    private final FunkoService funkoService;

    public FunkoController(FunkoService funkoService) {
        this.funkoService = funkoService;
    }

    public List<Funko> findAll() throws SQLException {
        logger.debug("FindAll");
        return funkoService.findAll();
    }

    public Optional<Funko> findById(String id) throws SQLException, FunkoNotFoundException {
        String msg = "FindById " + id;
        logger.debug(msg);
        return Optional.of(funkoService.findById(id)).orElseThrow(() ->
                new FunkoNotFoundException("No se ha encontrado el Funko con id " + id));
    }

    public List<Funko> findByName(String name) throws SQLException, FunkoNotFoundException {
        String msg = "FindByName " + name;
        logger.debug(msg);

        List<Funko> list = funkoService.findByName(name);
        if (list.isEmpty()) {
            throw new FunkoNotFoundException("No se ha encontrado el Funko con nombre " + name);
        }
        return funkoService.findByName(name);
    }

    public Optional<Funko> save(Funko funko) throws SQLException, FunkoNotSavedException, FunkoNotValidException {
        String msg = "Save " + funko;
        logger.debug(msg);
        FunkoValidator.validate(funko);
        return Optional.of(funkoService.save(funko).orElseThrow(() ->
                new FunkoNotSavedException("No se ha podido guardar el Funko")));
    }

    public Optional<Funko> update(String id, Funko funko) throws FunkoNotValidException, SQLException {
        String msg = "Update " + funko;
        logger.debug(msg);
        FunkoValidator.validate(funko);
        return Optional.of(funkoService.update(id, funko).orElseThrow(() ->
                new FunkoNotValidException("No se ha actualizado el Funko con id " + id)));
    }

    public Optional<Funko> delete(String id) throws SQLException, FunkoNotFoundException {
        String msg = "Delete " + id;
        logger.debug(msg);
        var funko = funkoService.findById(id).orElseThrow(() ->
                new FunkoNotFoundException("No se ha encontrado el Funko con id " + id));
        funkoService.delete(id);
        return Optional.of(funko);
    }

    public void backup(String url, String fileName) throws SQLException, IOException {
        funkoService.backup(url, fileName);
    }
}