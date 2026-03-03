# Proyecto MVC Personas (Spring Boot + Maven)

Proyecto Spring Boot con Thymeleaf que muestra en `/` la tabla **Inicio de la aplicacion** con datos codificados en Java.

## Requisitos

- Java 17+

## Ejecutar

1. Compilar:

   ```bash
   .\mvnw.cmd clean package
   ```

2. Ejecutar:

   ```bash
   .\mvnw.cmd spring-boot:run
   ```

3. Abrir en el navegador:

   ```
   http://localhost:8080/
   ```

## Estructura principal

- `src/main/java/com/project/hotel/MvcpersonasApplication.java`
- `src/main/java/com/project/hotel/controller/PersonaController.java`
- `src/main/java/com/project/hotel/entities/Persona.java`
- `src/main/resources/templates/index.html`