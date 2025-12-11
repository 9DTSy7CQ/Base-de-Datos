
# Base de Datos - Acceso

## Descripción
Proyecto de gestión de bases de datos con acceso a través de un conjunto de clases especializadas.

## Características
- Conexión a bases de datos
- Operaciones CRUD (Create, Read, Update, Delete)
- Gestión de transacciones
- Validación de datos

## Estructura del Proyecto
```
Base de datos Acceso/
├── README.md
├── src/
│   └── clases/
│       ├── Conexion.java
│       ├── DAO.java
│       └── Modelo.java
└── tests/
```

## Clases Principales

### Conexion
Gestiona la conexión con la base de datos.

### DAO (Data Access Object)
Patrón de acceso a datos para las operaciones básicas.

### Modelo
Representa las entidades de la base de datos.

## Uso
```java
Conexion conn = new Conexion();
DAO dao = new DAO(conn);
// Operaciones CRUD
```

## Requisitos
- Java 8+
- Driver JDBC
