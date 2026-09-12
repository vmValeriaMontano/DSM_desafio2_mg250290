# Desafío 2 - DSM 
## Valeria Montano MG250290 

## Aplicación de Destinos Turísticos

Aplicación móvil desarrollada en **Android Studio utilizando Kotlin**, orientada a la gestión de destinos turísticos. La aplicación utiliza **Firebase** para la autenticación de usuarios y el almacenamiento de información.

---

## Tecnologías utilizadas 💻

* **Kotlin**
* **Android Studio**
* **XML**
* **Firebase Authentication**
* **Firebase Realtime Database**
* **Glide**
* **Base64**
* **AndroidX**
* **Material Components**

---

## Funcionalidades implementadas

### Autenticación ✨

* Registro de nuevos usuarios.
* Inicio de sesión.
* Validación de campos.
* Cierre de sesión.

### Gestión de destinos 

* Visualización de destinos registrados.
* Agregar nuevos destinos.
* Editar destinos.
* Eliminar destinos.
* Validación de los datos ingresados.
* Selección de país mediante Spinner.
* Registro de nombre, país, precio y descripción.

### Imágenes 🖼️

* Selección de imágenes desde la galería.
* Solicitud de permiso para acceder a las imágenes.
* Vista previa de la imagen seleccionada.
* Conversión de imágenes a **Base64**.
* Almacenamiento de la imagen junto con el destino.
* Conversión de Base64 nuevamente a imagen para mostrarla.

Para realizar estas funciones se creó la clase:

```text
ImageUtils
```

Esta clase contiene las funciones necesarias para convertir imágenes entre **URI, Bitmap y Base64**.

### Permisos ✅

La aplicación solicita los permisos necesarios para acceder a las imágenes de la galería, dependiendo de la versión de Android utilizada.

### Interfaz

La aplicación cuenta con:

* Botones con iconos.
* Iconos vectoriales personalizados.
* Cards para mostrar los destinos.
* RecyclerView para listar los destinos.
* Imágenes de vista previa.
* Mensajes de éxito y error mediante `AlertDialog`.
* Validaciones para evitar datos incorrectos.

---

# Instalación

## Requisitos

Para ejecutar el proyecto se necesita:

* Android Studio.
* JDK compatible con el proyecto.
* Un dispositivo Android o emulador.
* Conexión a Internet.
* Una cuenta de Firebase.

## Pasos 🔍

1. Descargar o clonar el proyecto.
2. Abrir el proyecto desde **Android Studio**.
3. Esperar a que Android Studio sincronice las dependencias de Gradle.
4. Configurar Firebase siguiendo los pasos indicados en la sección de configuración.
5. Conectar un dispositivo Android o iniciar un emulador.
6. Ejecutar el proyecto desde Android Studio utilizando:

```text
Run ▶
```

---

# Configuración de Firebase

La aplicación utiliza Firebase para almacenar y administrar la información.

## 1. Crear proyecto en Firebase

Crear un proyecto desde la consola de Firebase.

## 2. Agregar la aplicación Android

Registrar la aplicación utilizando el mismo paquete del proyecto:

```text
com.example.desafio2_dsm
```

## 3. Agregar `google-services.json`

Descargar el archivo:

```text
google-services.json
```

y colocarlo dentro de:

```text
app/
```

La estructura debe quedar aproximadamente así:

```text
Proyecto/
└── app/
    ├── google-services.json
    ├── src/
    └── build.gradle.kts
```

## 4. Authentication

En Firebase Authentication se debe habilitar el método de autenticación utilizado por la aplicación.

La aplicación utiliza Firebase Authentication para:

* Registrar usuarios.
* Iniciar sesión.
* Mantener la autenticación del usuario.
* Cerrar sesión.

## 5. Realtime Database

Crear una base de datos de **Firebase Realtime Database**.

La información de los destinos se almacena principalmente en:

```text
destinations
```

Cada destino contiene información como:

```text
id
name
country
price
description
imageUrl
imageBase64
```

---

# Estructura principal de la aplicación

### `AuthActivity`

Se encarga del:

* Registro.
* Inicio de sesión.
* Validación de usuarios.

### `HomeActivity`

Se encarga de:

* Mostrar los destinos.
* Permitir agregar destinos.
* Permitir editar destinos.
* Permitir eliminar destinos.
* Cerrar sesión.

### `AddDestinationActivity`

Permite:

* Crear un nuevo destino.
* Seleccionar una imagen.
* Validar la información.
* Convertir la imagen a Base64.
* Guardar el destino en Firebase.

### `EditDestinationActivity`

Permite:

* Cargar un destino existente.
* Modificar sus datos.
* Cambiar la imagen.
* Actualizar la información en Firebase.

---

# Clases y elementos adicionales

## `ImageUtils`

Clase creada para trabajar con imágenes.

Permite:

* Convertir una imagen seleccionada desde la galería a Base64.
* Convertir una imagen Base64 a Bitmap.
* Facilitar el almacenamiento y visualización de imágenes.

## `Destination`

Modelo utilizado para representar la información de cada destino turístico.

## Adaptador de destinos

Se utiliza para mostrar los destinos dentro del `RecyclerView`, incluyendo sus datos, imagen y botones de editar y eliminar.

## Iconos vectoriales

La aplicación utiliza diferentes archivos vectoriales para mejorar la interfaz, por ejemplo:

```text
ic_add_destination.xml
ic_delete.xml
ic_edit.xml
ic_image.xml
ic_logout.xml
ic_save.xml
ic_update.xml
ic_login.xml
ic_register.xml
```

Estos iconos se utilizan principalmente en los botones de la aplicación.

---

# Permisos

La aplicación solicita permisos para acceder a las imágenes de la galería cuando es necesario.

El permiso utilizado depende de la versión de Android:

* `READ_MEDIA_IMAGES` para versiones modernas de Android.
* `READ_EXTERNAL_STORAGE` para versiones anteriores.

---

# Flujo general de la aplicación

```text
Inicio
   ↓
Registro / Inicio de sesión
   ↓
Pantalla principal
   ↓
Ver destinos
   ↓
Agregar / Editar / Eliminar
   ↓
Firebase Realtime Database
```

Para agregar una imagen:

```text
Seleccionar imagen
       ↓
Permiso de galería
       ↓
Seleccionar imagen
       ↓
Vista previa
       ↓
ImageUtils
       ↓
Convertir a Base64
       ↓
Guardar en Firebase
```

---

# Objetivo del proyecto

El objetivo del proyecto es desarrollar una aplicación móvil funcional para la gestión de destinos turísticos, aplicando conocimientos de:

* Desarrollo Android.
* Kotlin.
* Interfaces XML.
* Firebase Authentication.
* Firebase Realtime Database.
* CRUD.
* Manejo de imágenes.
* Conversión Base64.
* Permisos de Android.
* RecyclerView.
* Validación de información.

---

# ✨ Resultado ✨

La aplicación permite al usuario autenticarse, consultar destinos turísticos y administrar su información, incluyendo imágenes, utilizando una interfaz sencilla y Firebase como servicio de backend.
