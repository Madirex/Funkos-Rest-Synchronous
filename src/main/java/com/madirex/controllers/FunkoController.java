package com.madirex.controllers;

import com.madirex.exceptions.FunkoNotFoundException;
import com.madirex.exceptions.FunkoNotRemovedException;
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

    public List<Funko> findAll() throws SQLException, FunkoNotFoundException {
        logger.debug("FindAll");
        return funkoService.findAll();
    }

    public Optional<Funko> findById(String id) throws SQLException, FunkoNotFoundException {
        String msg = "FindById " + id;
        logger.debug(msg);
        return funkoService.findById(id);
    }

    public List<Funko> findByName(String name) throws SQLException, FunkoNotFoundException {
        String msg = "FindByName " + name;
        logger.debug(msg);
        return funkoService.findByName(name);
    }

    public Optional<Funko> save(Funko funko) throws SQLException, FunkoNotSavedException, FunkoNotValidException {
        String msg = "Save " + funko;
        logger.debug(msg);
        FunkoValidator.validate(funko);
        return funkoService.save(funko);
    }

    public Optional<Funko> update(String id, Funko funko) throws FunkoNotValidException, SQLException {
        String msg = "Update " + funko;
        logger.debug(msg);
        FunkoValidator.validate(funko);
        return funkoService.update(id, funko);
    }

    public Optional<Funko> delete(String id) throws SQLException, FunkoNotFoundException, FunkoNotRemovedException {
        String msg = "Delete " + id;
        boolean removed = false;
        logger.debug(msg);
        var funko = funkoService.findById(id);
        if (funko.isPresent()){
            removed = funkoService.delete(id);
        }
        if (!removed){
            return Optional.empty();
        }
        return funko;
    }

    public void backup(String url, String fileName) throws SQLException, IOException {
        funkoService.backup(url, fileName);
    }
}