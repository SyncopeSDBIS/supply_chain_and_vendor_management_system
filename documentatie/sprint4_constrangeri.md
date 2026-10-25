# ADR-008: REST Web Services Architecture and Implementation

## Status

Accepted

---

# Context

Sprint 4 are ca obiectiv expunerea funcționalităților sistemului prin servicii REST construite peste Business Layer și Persistence Layer implementate în sprinturile anterioare.

Arhitectura trebuie să respecte principiile:

- Service Oriented Architecture (SOA)
- REST Architectural Style
- Spring MVC
- Spring REST Services
- DTO-Based Communication
- XML / JSON Data Exchange

【1-4bca1b】

---

# Architecture Decision

Sistemul va adopta o arhitectură REST bazată pe:

- HTTP Protocol
- Resource-Oriented Design
- Spring MVC REST Controllers
- DTO Serialization Layer
- Jackson JSON Processing
- JAXB XML Mapping

【1-4bca1b】

---

# 1. HTTP Communication Requirements

## Supported HTTP Methods

Serviciile REST trebuie să utilizeze următoarele metode HTTP:

### Resource Access

```http
GET
```

Pentru citirea resurselor.

### Resource Creation

```http
POST
```

Pentru crearea resurselor.

### Resource Update

```http
PUT
```

Pentru actualizarea resurselor.

### Partial Update

```http
PATCH
```

Pentru modificări parțiale.

### Resource Removal

```http
DELETE
```

Pentru ștergerea resurselor.

### Metadata Retrieval

```http
HEAD
```

Pentru obținerea metadatelor fără corpul răspunsului.

### Communication Negotiation

```http
OPTIONS
```

Pentru negocierea parametrilor de comunicare.

【1-4bca1b】

---

## HTTP Status Codes

Serviciile trebuie să utilizeze coduri HTTP standard.

### Success

```http
200 OK
204 No Content
```

### Errors

```http
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
408 Request Timeout
```

【1-4bca1b】

---

# 2. HTTP Header Requirements

## Request Headers

Trebuie suportate:

```http
Accept
Content-Type
Content-Length
Authorization
```

【1-4bca1b】

---

## Response Headers

Trebuie suportate:

```http
Access-Control-Allow-Origin
Content-Type
Content-Length
```

【1-4bca1b】

---

## Supported MIME Types

### Request / Response

```http
application/json
application/xml
```

### Additional Formats

```http
text/plain
text/html
text/xml
application/atom+xml
```

【1-4bca1b】

---

# 3. REST Technology Stack

## Spring Framework

Sistemul trebuie să utilizeze:

### Spring Core

- Beans
- Dependency Injection

### Spring AOP

- Aspects
- Cross-cutting concerns

### Spring Data

- Spring JDBC
- Spring JPA

### Spring Web

- Spring MVC
- Spring REST

### Spring Security

- Authentication
- Authorization

【1-4bca1b】

---

## Serialization Technologies

Sistemul trebuie să suporte:

### XML Processing

- JAXB
- Spring OXM

### JSON Processing

- Jackson

【1-4bca1b】

---

# 4. REST Application Architecture

## Data Components

Sistemul trebuie să conțină:

- Entity Classes
- Entity Repositories

【1-4bca1b】

---

## Service Components

Sistemul trebuie să conțină:

- Business Services
- REST Controllers

【1-4bca1b】

---

## Configuration Components

Sistemul trebuie să conțină:

### WebInitializer

Bazat pe:

```java
AbstractAnnotationConfigDispatcherServletInitializer
```

### WebConfig

Bazat pe:

```java
WebMvcConfigurer
```

### ApplicationConfiguration

Configurarea aplicației.

【1-4bca1b】

---

## Deployment Components

Implementarea trebuie să suporte:

- Apache Tomcat
- Maven Deploy Plugins

【1-4bca1b】

---

# 5. URL Design Constraints

## URL Structure

Endpoint-urile trebuie să respecte structura:

```text
http://host:port/application/servlet/resource
```

Exemplu:

```text
http://localhost:8080/scrum/data/projects
```

【1-4bca1b】

---

## URL Sources

### Application Context

Definit în:

```xml
pom.xml
```

### Servlet Mapping

Definit în:

```java
WebInitializer
```

### Resource Mapping

Definit în:

```java
@RequestMapping
```

【1-4bca1b】

---

# 6. REST Controller Requirements

## Controller Structure

Fiecare serviciu REST trebuie implementat folosind:

```java
@RestController
@RequestMapping("/resource")
```

【1-4bca1b】

---

## HTTP Mapping

### GET

```java
@GetMapping
```

sau

```java
@RequestMapping(method = RequestMethod.GET)
```

### POST

```java
@PostMapping
```

sau

```java
@RequestMapping(method = RequestMethod.POST)
```

### PUT

```java
@PutMapping
```

sau

```java
@RequestMapping(method = RequestMethod.PUT)
```

### DELETE

```java
@DeleteMapping
```

sau

```java
@RequestMapping(method = RequestMethod.DELETE)
```

### PATCH

```java
@PatchMapping
```

【1-4bca1b】

---

## Request Body Processing

Pentru extragerea datelor din request:

```java
@RequestBody
```

【1-4bca1b】

---

## Response Body Processing

Pentru serializarea răspunsului:

```java
@ResponseBody
```

【1-4bca1b】

---

## Path Parameters

Trebuie utilizat:

```java
@PathVariable
```

pentru identificatori expuși în URL.

Exemplu:

```java
/projects/{id}
```

【1-4bca1b】

---

# 7. Content Negotiation

## Supported Formats

Serviciile trebuie să suporte:

- JSON
- XML

【1-4bca1b】

---

## Request Configuration

### JSON

```http
Content-Type: application/json
Accept: application/json
```

### XML

```http
Content-Type: application/xml
Accept: application/xml
```

【1-4bca1b】

---

## Request Mapping Configuration

Controller-ele trebuie să utilizeze:

```java
consumes
produces
```

în cadrul adnotării:

```java
@RequestMapping
```

【1-4bca1b】

---

# 8. DTO Architecture

## DTO Requirement

Entitățile nu trebuie expuse direct.

Comunicarea REST trebuie realizată prin DTO-uri.

【1-4bca1b】

---

## XML DTO Mapping

DTO-urile XML trebuie să utilizeze:

### Root Mapping

```java
@XmlRootElement
```

### Element Mapping

```java
@XmlElement
```

### Attribute Mapping

```java
@XmlAttribute
```

### Collection Mapping

```java
@XmlElements
@XmlElementWrapper
```

### Ignore Fields

```java
@XmlTransient
```

【1-4bca1b】

---

# 9. JSON DTO Architecture

## JSON Root Object

```java
@JsonRootName
```

【1-4bca1b】

---

## Attribute Mapping

```java
@JsonProperty
@JsonGetter
```

【1-4bca1b】

---

## Circular Reference Handling

Pentru relații bidirecționale trebuie utilizate:

```java
@JsonManagedReference
@JsonBackReference
```

【1-4bca1b】

---

## Ignored Fields

```java
@JsonIgnore
@JsonIgnoreProperties
```

【1-4bca1b】

---

# 10. Aggregate Serialization Rules

## Circular Dependency Constraint

Referințele circulare între:

```text
Aggregate Root
 ↕
Child Entity
```

nu trebuie serializate direct.

Trebuie utilizat:

- DTO Mapping
- Json Managed/Back Reference

pentru evitarea ciclurilor infinite.

【1-4bca1b】

---

# 11. XML/JSON Converter Configuration

## Maven Dependencies

Trebuie incluse:

```xml
jaxb-api
jackson-databind
jackson-dataformat-xml
jackson-module-jaxb-annotations
```

【1-4bca1b】

---

## Spring Configuration

WebConfig trebuie să înregistreze:

```java
MappingJackson2XmlHttpMessageConverter
MappingJackson2HttpMessageConverter
```

【1-4bca1b】

---

## JAXB Integration

Trebuie utilizat:

```java
JaxbAnnotationModule
```

pentru reutilizarea adnotărilor JAXB.

【1-4bca1b】

---

# 12. REST API Contract

## Collection Resource

```http
GET    /resource
POST   /resource
```

---

## Single Resource

```http
GET    /resource/{id}
PUT    /resource/{id}
DELETE /resource/{id}
```

【1-4bca1b】

---

# 13. REST Repository Exposure

## REST Ready Repository

Repository-urile pot fi expuse automat folosind:

```java
@RepositoryRestResource
```

【1-4bca1b】

---

# 14. Testing Requirements

## Deployment Testing

Aplicația trebuie să ruleze pe:

```text
Apache Tomcat 9+
```

【1-4bca1b】

---

## API Testing Tools

Trebuie validate endpoint-urile folosind:

- Browser
- Postman
- Apache HTTP Client
- JUnit Tests

【1-4bca1b】

---

## HTTP Client Testing

Testele trebuie să utilizeze:

```java
HttpGet
HttpPost
HttpPut
HttpDelete
```

împreună cu:

```java
HttpClientBuilder
HttpResponse
ObjectMapper
```

【1-4bca1b】

---

# Deliverables

## Mandatory

- [ ] Spring MVC REST Configuration
- [ ] WebInitializer
- [ ] WebConfig
- [ ] REST Controllers
- [ ] CRUD Endpoints
- [ ] DTO Classes
- [ ] XML Serialization
- [ ] JSON Serialization
- [ ] Content Negotiation
- [ ] JAXB Configuration
- [ ] Jackson Configuration
- [ ] Repository Integration
- [ ] REST Tests
- [ ] Postman Test Collection

---

# Definition of Done

Sprint 4 este finalizat dacă:

- [ ] toate Aggregate Root-urile sunt expuse prin REST;
- [ ] endpoint-urile CRUD funcționează;
- [ ] XML și JSON sunt suportate;
- [ ] DTO-urile sunt serializate corect;
- [ ] nu există cicluri de serializare;
- [ ] Content Negotiation funcționează;
- [ ] serviciile sunt testate prin HTTP Client și Postman;
- [ ] aplicația este deployată cu succes pe Tomcat.

---