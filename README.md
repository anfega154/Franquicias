# Proyecto Base: API de Franquicias 🏪

Este proyecto implementa una **API reactiva** con **Spring Boot + WebFlux**, estructurada bajo el patrón **Clean Architecture**.  
La solución gestiona una red de **franquicias, sucursales y productos**, permitiendo operaciones de creación, actualización y consulta.

Repositorio en GitHub 👉 [anfega154/Franquicias](https://github.com/anfega154/Franquicias)

---

## 📖 Tabla de Contenido
- [Arquitectura](#arquitectura)
- [Módulos del Proyecto](#módulos-del-proyecto)
    - [Domain](#domain)
    - [Usecases](#usecases)
    - [Infrastructure](#infrastructure)
        - [Driven Adapters](#driven-adapters)
        - [Entry Points](#entry-points)
    - [Application](#application)
- [Configuración](#configuración)
    - [application.yml](#applicationyml)
    - [Variables de entorno](#variables-de-entorno)
- [Persistencia](#persistencia)
- [Ejecución del Proyecto](#ejecución-del-proyecto)
- [API REST](#api-rest)
    - [Documentación Swagger](#documentación-swagger)
- [Pruebas](#pruebas)

---

## 🏛️ Arquitectura

El proyecto está basado en **Clean Architecture**, siguiendo la plantilla de Bancolombia.  
Esto permite mantener el **dominio protegido**, separando la lógica de negocio de los detalles técnicos.

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

---

## 📂 Módulos del Proyecto

### Domain
- Contiene los **modelos del dominio** (`Franchise`, `Branch`, `Product`).
- Define las **interfaces de repositorio** (`FranchiseRepository`, `BranchRepository`, `ProductRepository`).
- Encapsula las **reglas de negocio**.
- Se exponen los casos de uso mediante puertos (interfaces). (`FranchiseInpuntPort`, `BranchInputPort`, `ProductInputPort`).
- No tiene dependencias hacia otros módulos.


### Usecases
- Implementa los **casos de uso** del sistema:
    - Crear franquicia.
    - Crear sucursal.
    - Crear producto.
    - Actualizar stock.
    - Eliminar producto.
    - Obtener producto con mayor stock por sucursal.

Los casos de uso **orquestan la lógica de aplicación** y son invocados por los entry points.

### Infrastructure

#### Driven Adapters
- Implementaciones técnicas:
    - **MongoDB (Atlas)** como base de datos en la nube.

#### Entry Points
- **Routers + Handlers** basados en **Spring WebFlux**.
- Documentados con **springdoc-openapi** para Swagger UI.

### Application
- Módulo más externo de la arquitectura.
- Configura el arranque de Spring Boot (`MainApplication`).
- Ensambla los módulos, resuelve dependencias y expone los casos de uso como beans.

---

## ⚙️ Configuración

### application.yml

```yaml
server:
  port: 8080

spring:
  data:
    mongodb:
      uri: "mongodb+srv://<user>:<password>@anfega.ybqhqud.mongodb.net/?retryWrites=true&w=majority&appName=anfega"
      database: "franchises"
  application:
    name: "Franquicias"
  devtools:
    add-properties: false
  h2:
    console:
      enabled: true
      path: "/h2"
  profiles:
    include: null

management:
  endpoints:
    web:
      exposure:
        include: "health,prometheus"
  endpoint:
    health:
      probes:
        enabled: true

cors:
  allowed-origins: "http://localhost:4200,http://localhost:8080"

routers:
  paths:
    franchises: "/api/v1/franquicias"
    branches: "/api/v1/sucursales"
    products: "/api/v1/productos"
    topProducts: "/api/v1/franchises/{franchiseName}/top-products-per-branch"

```

---

#### 🗄️ Persistencia
- El proyecto utiliza MongoDB Atlas como base de datos.
- Colecciones principales:
    - `franchises`
    - `branches`
    - `products`
  
-----

## 🚀 Ejecución del Proyecto
-Requisitos:
- Java 21+
- Maven 8+
- MongoDB Atlas (configurado en `application.yml`)

-Pasos:
# Clonar repositorio
git clone https://github.com/anfega154/Franquicias.git
cd Franquicias

# Compilar
./gradlew clean build

# Ejecutar
./gradlew bootRun

- La aplicación correrá en 👉 http://localhost:8080

---

## 🛠️ API REST
### Rutas principales

| Método  | Endpoint                                                        | Descripción                                         |
|---------|-----------------------------------------------------------------|-----------------------------------------------------|
| POST    | /api/v1/franquicias                                             | Crear una franquicia                                |
| POST    | /api/v1/sucursales                                              | Crear una sucursal en una franquicia                |
| POST    | /api/v1/productos                                               | Crear un producto en una sucursal                   |
| PUT     | /api/v1/productos?id={id}                                       | Actualizar stock de un producto                     |
| DELETE  | /api/v1/productos?id={id}                                       | Eliminar un producto                                |
| GET     | /api/v1/franchicias                                             | Listar todas las franquicias                        |
| GET     | /api/v1/franquicias/{id}                                        | Obtener detalles de una franquicia                  |
| GET     | /api/v1/sucursales                                              | Listar todas las sucursales                         |
| GET     | /api/v1/sucursales/{id}                                         | Obtener detalles de una sucursal                    |
| GET     | /api/v1/productos                                               | Listar todos los productos                          |
| GET     | /api/v1/productos/{id}                                          | Obtener detalles de un producto                     |
| GET     | /api/v1/franchises/{franchiseName}/top-products-per-branch      | Obtener el producto con mayor stock por sucursal    |

---

### Documentación Swagger
- La API está documentada con Swagger UI.
- Acceso en 👉 http://localhost:8080/swagger-ui.html
- OpenAPI JSON: 👉 http://localhost:8080/v3/api-docs

----

## 🧪 Pruebas
- El proyecto incluye pruebas unitarias
- Frameworks: JUnit 5, Mockito
- Ejecutar pruebas:
```bash
./gradlew test
```
- Reportes de cobertura con JaCoCo:
```bash
./gradlew jacocoTestReport
```
- Reportes en: `build/reports/jacoco/test/html/index.html` 