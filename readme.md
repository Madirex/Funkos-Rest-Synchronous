## Funkos

### 📝 Descripción
API Rest de Funkos programada en Java con Gradle y base de datos en H2 (modificable desde el archivo properties). Se realizan tests con JUnit y Mockito, además de varias consultas en el Main de la aplicación para probar tanto los casos correctos como los incorrectos del programa.

### ⚙ Herramientas
- Java 11.
- Gradle.
- H2.
- JUnit.
- Mockito.
- DotEnv.
- Lombok.
- Logback.
- Gson.
- Mybatis.

### 🗂️ Organización
- Controllers: Se encargan de recibir las peticiones del usuario y devolver la respuesta correspondiente.
- Exceptions: Se encargan de definir las excepciones que se van a utilizar en la aplicación.
- Models: Se encargan de definir los objetos que se van a utilizar en la aplicación.
- Repositories: Se encargan de realizar las operaciones con la base de datos.
- Services: Se encargan de realizar las operaciones necesarias para que el controlador pueda devolver la respuesta.
- Utils: Se encargan de definir las clases útiles que se van a utilizar en la aplicación.
- Validators: Se encargan de validar los objetos que se van a utilizar en la aplicación.
- FunkoProgram: El programa que ejecutará todas las consultas necesarias para probar el funcionamiento de la aplicación.
- Main: El programa que ejecutará la aplicación.

### 📊 Consultas API Stream
Las consultas se realizan en la clase FunkoProgram. Cada consulta interacciona con la base de datos y devuelve el resultado de la consulta. Se prueban tanto los casos incorrectos como los incorrectos.
- Funko más caro.
- Media de precio de Funkos.
- Funkos agrupados por modelos.
- Número de Funkos por modelos.
- Funkos que han sido lanzados en 2023.
- Número de Funkos de Stitch.
- Listado de Funkos de Stitch.

### 🛠️ Utils
El paquete Utils incluye las siguientes utilidades:
- ApplicationProperties: Se encarga de leer el archivo properties y devolver los valores de las propiedades.
- LocalDateAdapter: Se encarga de convertir un LocalDate a un String y viceversa.
- LocalDateTimeAdapter: Se encarga de convertir un LocalDateTime a un String y viceversa.
- Utils: Se encarga de definir métodos útiles para la aplicación.
- UuidAdapter: Se encarga de convertir un UUID a un String y viceversa.

### 🔄 Services
Incluye tres paquetes:
- CRUD: Base del CRUD y Operaciones CRUD de FUNKOS.
- Database: Se almacena el Manager de la base de datos.
- IO: Se almacena la clase CsvManager para leer un archivo CSV.

### ⚠️ Exceptions
El programa incluye las siguientes excepciones personalizadas:
- FunkoException: Excepción base de la aplicación.
- CreateFolderException: Exception que se lanza cuando no se puede crear una carpeta.
- FunkoNotFoundException: Exception que se lanza cuando no se encuentra un Funko.
- FunkoNotSavedException: Exception que se lanza cuando no se puede guardar un Funko.
- FunkoNotValidException: Exception que se lanza cuando un Funko no es válido.
- ReadCSVFailException: Exception que se lanza cuando no se puede leer un archivo CSV.

### 🔍 Operaciones CRUD
- FindAll: Se encarga de devolver todos los Funkos.
- FindById: Se encarga de devolver un Funko por su id.
- FindByName: Se encarga de devolver un Funko por su nombre.
- Save: Se encarga de guardar un Funko.
- Update: Se encarga de actualizar un Funko.
- Delete: Se encarga de eliminar un Funko.
- Backup: Se encarga de realizar un backup de la base de datos.