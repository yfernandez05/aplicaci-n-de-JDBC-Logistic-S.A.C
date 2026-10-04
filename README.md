# Sistema de Control de Salida de Vehículos - Logistic S.A.C.
### Grupo 8

El sistema busca mejorar el control del proceso de traslado, permitiendo gestionar vehículos, conductores, productos, documentación, inspecciones, evidencias, precintos y recepción de los traslados.

---

## Descripción del proyecto

El sistema permite gestionar el proceso de traslado de mercadería desde un almacén de origen hacia un almacén de destino.


## Objetivo

Desarrollar una aplicación de escritorio que permita controlar y registrar el proceso de salida de vehículos en los traslados entre almacenes de Logistic S.A.C., manteniendo la trazabilidad de las operaciones y aplicando reglas de negocio para evitar salidas no autorizadas.

---

## Tecnologías utilizadas

- **Java**
- **Maven**
- **MySQL**
- **JDBC**
- **MySQL Connector/J**
- **Git**

---

## Arquitectura

Se desarrolla en base a la arquitectura por capas, buscando separar responsabilidades y facilitar el mantenimiento y evolución del sistema.

La estructura principal:

```text
Modelo
   ↓
Service
   ↓
DAO
   ↓
JDBC
   ↓
MySQL