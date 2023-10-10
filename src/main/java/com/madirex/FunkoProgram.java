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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
        callAllServiceExceptionMethods();
        callAllServiceMethods();
        databaseQueries();
        DatabaseManager.getInstance().close();
    }

    private void databaseQueries() {
        logger.info("🔵 Funko más caro:");
        printExpensiveFunko();
        logger.info("🔵 Media de precio de Funkos:");
        printAvgPriceOfFunkos();
        logger.info("🔵 Funkos agrupados por modelos:");
        printFunkosGroupedByModels();
        logger.info("🔵 Número de Funkos por modelos:");
        printNumberOfFunkosByModels();
        logger.info("🔵 Funkos que han sido lanzados en 2023:");
        printFunkosReleasedIn(2023);
        logger.info("🔵 Número de Funkos de Stitch:");
        printNumberOfFunkosOfName("Stitch");
        logger.info("🔵 Listado de Funkos de Stitch:");
        printListOfFunkosOfName("Stitch");
    }

    private void printListOfFunkosOfName(String name) {
        try {
            controller.findAll().stream().filter(e -> e.getName().startsWith(name))
                    .forEach(e -> logger.info(e.toString()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FunkoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void printNumberOfFunkosOfName(String name) {
        try {
            logger.info(String.valueOf(controller.findAll().stream().filter(e -> e.getName().startsWith(name)).count()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FunkoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void printFunkosReleasedIn(int i) {
        try {
            controller.findAll().stream().filter(e -> e.getReleaseDate().getYear() == i)
                    .forEach(e -> logger.info(e.toString()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FunkoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void printNumberOfFunkosByModels() {
        try {
            controller.findAll().stream().collect(Collectors.groupingBy(Funko::getModel, Collectors.counting()))
                    .forEach((model, count) -> logger.info("🔵 " + model + " -> " + count));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FunkoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void printFunkosGroupedByModels() {
        try {
            Map<Model, List<Funko>> s = controller.findAll().stream().collect(Collectors.groupingBy(Funko::getModel));
            s.forEach((model, funkoList) -> {
                logger.info("\n🔵 Modelo: " + model);
                funkoList.forEach(funko -> logger.info(funko.toString()));
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FunkoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void printAvgPriceOfFunkos() {
        try {
            controller.findAll().stream().mapToDouble(Funko::getPrice).average()
                    .ifPresent(e -> logger.info(String.format("%.2f", e)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FunkoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void printExpensiveFunko() {
        try {
            controller.findAll().stream().max(Comparator.comparingDouble(Funko::getPrice))
                    .ifPresent(e -> logger.info(e.toString()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FunkoNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void callAllServiceExceptionMethods() {
        try {
            logger.info("🔴 Probando casos incorrectos 🔴");
            logger.info("🔴 Probando caso incorrecto de FindById:");
            printFindById("569689dd-b76b-465b-aa32-a6c46acd38fd");
            logger.info("🔴 Probando caso incorrecto de FindByName:");
            printFindByName("NoExiste");
            logger.info("🔴 Probando caso incorrecto de Save:");
            printSave(Funko.builder()
                    .name("MadiFunko2")
                    .model(Model.OTROS)
                    .price(-42)
                    .releaseDate(LocalDate.now())
                    .build());
            logger.info("🔴 Probando caso incorrecto de Update:");
            printUpdate("One Piece Luffy", "");
            logger.info("🔴 Probando caso incorrecto de Delete:");
            printDelete("NoExiste");
        } catch (SQLException e) {
            String strError = "Fallo SQL: " + e;
            logger.error(strError);
        }
    }

    private void callAllServiceMethods() {
        try {
            logger.info("🟢 Probando casos correctos 🟢");
            logger.info("🟢 Probando caso correcto de FindAll:");
            printFindAll();
            logger.info("🟢 Probando caso correcto de FindById:");
            printFindById("3b6c6f58-7c6b-434b-82ab-01b2d6e4434a");
            logger.info("🟢 Probando caso correcto de FindByName:");
            printFindByName("Doctor Who Tardis");
            logger.info("🟢 Probando caso correcto de Save:");
            printSave(Funko.builder()
                    .name("MadiFunko")
                    .model(Model.OTROS)
                    .price(42)
                    .releaseDate(LocalDate.now())
                    .build());
            logger.info("🟢 Probando caso correcto de Update:");
            printUpdate("MadiFunko", "MadiFunkoModified");
            logger.info("🟢 Probando caso correcto de Delete:");
            printDelete("MadiFunkoModified");
            logger.info("🟢 Copia de seguridad:");
            doBackupAndPrint("data");
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

    private void printSave(Funko funko) throws SQLException {
        //SAVE
        logger.info("\nSave:");
        try {
            try {
                controller.save(funko).ifPresent(e -> logger.info(e.toString()));
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
