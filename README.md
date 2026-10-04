# Sistema de Control de Salida de Vehículos - Logistic S.A.C.
### Grupo 8

El sistema permite controlar el proceso de traslado de mercadería entre los almacenes de Logistic S.A.C., desde la programación del traslado hasta su recepción.

---

## Descripción del proyecto

La aplicación permite registrar y controlar vehículos, conductores, productos, documentos, inspecciones, salidas y recepción de los traslados.

## Objetivo

Mejorar el control de las salidas de vehículos y mantener un registro de cada traslado realizado entre los almacenes.

---

## Acceso al sistema

El sistema cuenta con diferentes usuarios según sus funciones:

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | Administrador |
| despachador | desp123 | Despachador |
| vigilante | vig123 | Vigilante |
| jefe | jefe123 | Jefe de Seguridad |

### Funciones según el rol

- **Administrador:** consulta usuarios y gestiona almacenes, vehículos, conductores, productos y traslados.
- **Despachador:** gestiona traslados y registra la recepción.
- **Vigilante:** realiza la inspección del vehículo y verifica la salida.
- **Jefe de Seguridad:** autoriza o rechaza la salida de los vehículos.

---

## Tecnologías utilizadas

- **Java**
- **Maven**
- **MySQL**
- **JDBC**
- **Git**

---

## Base de datos

Antes de ejecutar el sistema, se debe crear la base de datos en MySQL.

El script se encuentra en:

```text
database/BD_Logistic_SAC.sql
```
---

## Ejecución del proyecto
Abrir una terminal en la carpeta principal del proyecto y ejecutar los siguientes comandos.

### Compilar

```bash
mvn clean compile
```

### Ejecutar el poryecto local

```bash
mvn clean javafx:run
```