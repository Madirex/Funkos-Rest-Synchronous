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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
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
        callAllServiceExceptionMethods();
        loadFunkosFileAndInsertToDatabase("data" + File.separator + "funkos.csv");
        callAllServiceMethods();
    }

    private void callAllServiceExceptionMethods() {
        try {
            logger.info("🔴 Probando casos incorrectos 🔴");
            //TODO: Print Find All
            logger.info("Probando caso incorrecto de FindById...");
            printFindById("NoExiste"); //TODO: TEST
            logger.info("Probando caso incorrecto de FindByName...");
            printFindByName("NoExiste"); //TODO: TEST
            //TODO: Print Save
            //TODO: Print Update
            //logger.info("Probando caso incorrecto de Eliminación...");
            //printDelete("NoExiste");
            //TODO: EL delete no comprueba bien la exception
        } catch (SQLException e) {
            String strError = "Fallo SQL: " + e;
            logger.error(strError);
        }
    }

    private void callAllServiceMethods() {
        try {
            logger.info("🟢 Probando casos correctos 🟢");
            printFindAll();
            printFindById("3b6c6f58-7c6b-434b-82ab-01b2d6e4434a");
            printFindByName("Doctor Who Tardis");
            printSave("MadiFunko");
            printUpdate("MadiFunko", "MadiFunkoModified");
            printDelete("MadiFunkoModified");
            doBackupAndPrint("data");
            DatabaseManager.getInstance().close();
        } catch (SQLException e) {
            String strError = "Fallo SQL: " + e;
            logger.error(strError);
        }
    }

    private void printDelete(String name) throws SQLException {
        logger.info("\nDelete:");

        try {
            controller.delete(controller.findByName(name).get(0).getCod().toString());
        } catch (FunkoNotFoundException e) {
            String strError = "El Funko no se ha encontrado: " + e;
            logger.error(strError);
        } catch (FunkoNotRemovedException e) {
            String strError = "El Funko no ha sido eliminado: " + e;
            logger.error(strError);
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
        } catch (FunkoNotValidException e) {
            String strError = "El Funko no es válido: " + e;
            logger.error(strError);
        } catch (FunkoNotFoundException e) {
            String strError = "El Funko no se ha encontrado: " + e;
            logger.error(strError);
        }
    }

    private void doBackupAndPrint(String rootFolderName) {
        logger.info("\nBackup:");
        try {
            controller.backup(System.getProperty("user.dir") + File.separator + rootFolderName, "backup.json");
        } catch (SQLException e) {
            String strError = "Fallo SQL: " + e;
            logger.error(strError);
        } catch (IOException e) {
            String strError = "Error de Input/Output: " + e;
            logger.error(strError);
        }
    }

    private void printSave(String name) throws SQLException {
        //SAVE
        logger.info("\nSave:");
        try {
            try {
                controller.save(Funko.builder()
                        .name(name)
                        .model(Model.OTROS)
                        .price(42)
                        .releaseDate(LocalDate.now())
                        .build()).ifPresent(e -> logger.info(e.toString()));
            } catch (FunkoNotValidException e) {
                String strError = "El Funko no es válido: " + e;
                logger.error(strError);
            }
        } catch (FunkoNotSavedException e) {
            String strError = "No se ha podido guardar el Funko: " + e;
            logger.error(strError);
        }
    }

    private void printFindById(String id) throws SQLException {
        logger.info("\nFind by Id:");
        try {
            controller.findById(id).ifPresent(e -> logger.info(e.toString()));
        } catch (FunkoNotFoundException e) {
            String strError = "No se ha encontrado el Funko con id " + id + " -> " + e;
            logger.error(strError);
        }
    }

    private void printFindByName(String name) throws SQLException {
        logger.info("\nFind by Name:");
        try {
            controller.findByName(name).forEach(e -> logger.info(e.toString()));
        } catch (FunkoNotFoundException e) {
            String strError = "No se han encontrado Funkos: " + e;
            logger.error(strError);
        }
    }

    private void printFindAll() throws SQLException {
        logger.info("\nFind All:");
        try {
            controller.findAll().forEach(e -> logger.info(e.toString()));
        } catch (FunkoNotFoundException e) {
            String strError = "No se han encontrado Funkos: " + e;
            logger.error(strError);
        }
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
                            } catch (FunkoNotValidException ex1) {
                                String strError = "El Funko no es válido: " + ex1;
                                logger.error(strError);
                            } catch (FunkoNotSavedException ex2) {
                                String strError = "El Funko no se ha guardado: " + ex2;
                                logger.error(strError);
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
