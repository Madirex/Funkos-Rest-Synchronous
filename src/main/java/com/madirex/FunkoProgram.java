package com.madirex;

import com.madirex.controllers.FunkoController;
import com.madirex.exceptions.*;
import com.madirex.models.Funko;
import com.madirex.models.Model;
import com.madirex.repositories.funko.FunkoRepositoryImpl;
import com.madirex.services.crud.funko.FunkoServiceImpl;
import com.madirex.services.database.DatabaseManager;
import com.madirex.services.io.CsvManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class FunkoProgram {

    private static FunkoProgram funkoProgramInstance;
    private final Logger logger = LoggerFactory.getLogger(FunkoProgram.class);
    private FunkoController controller;

    private FunkoProgram() {
        controller = new FunkoController(new FunkoServiceImpl(FunkoRepositoryImpl
                .getInstance(DatabaseManager.getInstance())));
    }

    public static FunkoProgram getInstance() {
        if (funkoProgramInstance == null) {
            funkoProgramInstance = new FunkoProgram();
        }
        return funkoProgramInstance;
    }

    public void init() {
        logger.info("Programa de Funkos iniciado.");
        loadFunkosFileAndInsertToDatabase("data" + File.separator + "funkos.csv");
        callAllServiceMethods();
    }

    private void callAllServiceMethods() {
        try {
            //Casos correctos
            printFindAll();
            printFindByName("Doctor Who Tardis");
            printFindById("3b6c6f58-7c6b-434b-82ab-01b2d6e4434a");
            printSave("MadiFunko");
            printUpdate("MadiFunko", "MadiFunkoModified");
            printDelete("MadiFunkoModified");

            doBackupAndPrint("data");
            DatabaseManager.getInstance().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void printDelete(String name) throws SQLException {
        logger.info("\nDelete:");
        try {
            controller.delete(controller.findByName(name).get(0).getCod().toString());
        } catch (FunkoException e) {
            throw new RuntimeException(e);
        }
    }

    private void printUpdate(String name, String newName) throws SQLException {
        logger.info("\nUpdate:");
        try {
            controller.update(controller.findByName(name).get(0).getCod().toString(), Funko.builder()
                    .name(newName)
                    .model(Model.DISNEY)
                    .price(42.42)
                    .releaseDate(LocalDate.now())
                    .build()).ifPresent(e -> logger.info(e.toString()));
        } catch (FunkoException e) {
            throw new RuntimeException(e);
        }
    }

    private void doBackupAndPrint(String rootFolderName) {
        logger.info("\nBackup:");
        try {
            controller.backup(System.getProperty("user.dir") + File.separator + rootFolderName, "backup.json");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printSave(String name) throws SQLException {
        //SAVE
        logger.info("\nSave:");
        try {
            controller.save(Funko.builder()
                    .name(name)
                    .model(Model.OTROS)
                    .price(42)
                    .releaseDate(LocalDate.now())
                    .build()).ifPresent(e -> logger.info(e.toString()));
        } catch (FunkoException e) {
            throw new RuntimeException(e);
        }
    }

    private void printFindById(String id) throws SQLException {
        logger.info("\nFind by Id:");
        try {
            controller.findById(id).ifPresent(e -> logger.info(e.toString()));
        } catch (FunkoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void printFindByName(String name) throws SQLException {
        logger.info("\nFind by Name:");
        try {
            controller.findByName(name).forEach(e -> logger.info(e.toString()));
        } catch (FunkoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void printFindAll() throws SQLException {
        logger.info("\nFind All:");
        controller.findAll().forEach(e -> logger.info(e.toString()));
    }

    public void loadFunkosFileAndInsertToDatabase(String path) {
        AtomicBoolean failed = new AtomicBoolean(false);
        CsvManager csvManager = CsvManager.getInstance();
        try {
            csvManager.fileToFunkoList(path)
                    .ifPresent(e -> {
                        e.forEach(funko -> {
                            try {
                                controller.save(funko);
                            } catch (SQLException throwables) {
                                failed.set(true);
                                String strError = "Error: " + throwables;
                                logger.error(strError);
                            } catch (FunkoNotValidException | FunkoNotSavedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });

                        if (failed.get()) {
                            logger.error("Error al insertar los datos en la base de datos");
                        }
                    });
        } catch (ReadCSVFailException e) {
            logger.error("Error al leer el CSV");
        }
    }
}
