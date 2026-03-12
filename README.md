# Hotel Donde Juana (Spring Boot + Maven)

Proyecto Spring Boot con Thymeleaf que muestra en `/` la tabla **Inicio de la aplicacion** con datos codificados en Java.

## Requisitos

- Java 17+
- Maven (o usar el Maven Wrapper incluido)

## Ejecutar

### En Linux / Mac

```bash
./mvnw spring-boot:run
```

### En Windows

```bash
.\mvnw.cmd spring-boot:run
```

Luego abrir en el navegador:

```
http://localhost:8080/
```

## Compilar el proyecto

### En Linux / Mac

```bash
./mvnw clean package
```

### En Windows

```bash
.\mvnw.cmd clean package
```

## Estructura principal

```
src/
└── main/
    ├── java/com/project/hotel/
    │   ├── MvcpersonasApplication.java   ← Clase principal
    │   ├── controller/
    │   │   └── PersonaController.java
    │   └── entities/
    │       └── Persona.java
    └── resources/
        ├── application.properties
        └── templates/
            └── index.html
```