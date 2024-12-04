# Happyland App 🎢

**Happyland App** es una aplicación móvil diseñada para mejorar la experiencia de los usuarios en las instalaciones de Happyland, permitiendo la gestión de tarjetas, compra de tickets, reservas de cumpleaños y el canje de premios directamente desde el dispositivo móvil.

---

## Características principales 📱

- **Gestión de Tarjetas**: Agrega tarjetas NFC o manualmente, consulta saldos y detalles.
- **Canje de Premios**: Explora un catálogo de premios y canjéalos según tus tickets disponibles.
- **Reservación de Cumpleaños**: Selecciona paquetes, reserva fechas, y gestiona observaciones y pagos.
- **Compra de Tickets**: Ofrece opciones para adquirir tickets directamente desde la app.
- **NFC Compatible**: Integración con tecnología NFC para lectura de tarjetas.
- **Autenticación de Usuarios**: Registro, inicio de sesión y manejo de sesiones activas mediante Firebase Authentication.
- **Soporte Offline (Pendiente)**: Optimización para funcionar parcialmente sin conexión.

---

## Tecnologías utilizadas 🔧

- **Lenguaje**: Kotlin
- **Framework de UI**: Jetpack Compose
- **Base de datos**: Firebase Firestore
- **Autenticación**: Firebase Authentication
- **NFC**: Integración con el sistema NFC del dispositivo
- **Gestión de Estados**: Jetpack Compose State Management
- **Diseño UI/UX**: Material Design 3

---

## Estructura del Proyecto 📂

### Screens
- **WelcomeScreen**: Pantalla de bienvenida con opciones para iniciar sesión o registrarse.
- **RegisterScreen**: Registro de nuevos usuarios con validación de contraseñas.
- **LoginScreen**: Inicio de sesión con manejo de errores comunes.
- **HomeScreen**: Panel principal con opciones de navegación, detalles de la tarjeta, y acceso a funcionalidades clave.
- **PrizesScreen**: Catálogo de premios con opciones de filtrado y canje.
- **BirthdayReservationScreen**: Selección de paquetes y reserva de cumpleaños.
- **CalendarReservationScreen**: Gestión de reservas en un calendario interactivo.
- **AddCardScreen**: Agregar tarjeta mediante entrada manual o NFC.

### Models
- **Premio**: Representación de un premio con propiedades como nombre, descripción, imagen, tickets requeridos y stock.

### Utils
- Métodos auxiliares para:
  - Validar números de tarjetas.
  - Formatear entradas.
  - Manejar estados de inicio de sesión.

---

## Instalación y configuración 🚀

1. **Requisitos previos**:
   - Android Studio (versión 2022 o superior).
   - Dispositivo Android con NFC (opcional).
   - Conexión a Internet.

2. **Configuración de Firebase**:
   - Crear un proyecto en [Firebase Console](https://console.firebase.google.com/).
   - Configurar Authentication (Email/Password).
   - Crear las colecciones necesarias en Firestore:
     - `tarjetas`, `premios`, `paquetes`, `reservas`, `historiales`, `usuarios`, `recargas`, `tickets`.

3. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/tuusuario/Happyland-App.git
   cd Happyland-App
   
### 4. Configurar archivo `google-services.json`:
- Descargar el archivo desde Firebase Console.
- Colocarlo en la carpeta `app/`.

### 5. Ejecutar el proyecto:
- Abrir el proyecto en Android Studio.
- Sincronizar dependencias.
- Ejecutar en un emulador o dispositivo físico.

---

## Vista Previa 📸

### Screenshots de la App

| ![WelcomeScreen](https://github.com/user-attachments/assets/546fe716-7bb0-4fa4-8db1-7b45135c1a05) | ![RegisterScreen](https://github.com/user-attachments/assets/ab38be86-5fed-4675-98f7-ad7dbf4a980d) | ![LoginScreen](https://github.com/user-attachments/assets/48720496-9f27-4938-871e-6de691a88bc3) |
|-----------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| WelcomeScreen                                                                                      | RegisterScreen                                                                                      | LoginScreen                                                                                        |

| ![HomeScreen](https://github.com/user-attachments/assets/bf217e99-3097-468e-8f25-294374bd2d63) | ![BirthdayReservationScreen](https://github.com/user-attachments/assets/44c200f8-a6ed-4282-be0e-5e06994cf8ac) | ![CalendarReservationScreen](https://github.com/user-attachments/assets/e64bedab-f06c-403f-9443-a819c8193b6f) |
|--------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| HomeScreen                                                                                       | BirthdayReservationScreen                                                                                    | CalendarReservationScreen                                                                                   |

| ![PrizesScreen](https://github.com/user-attachments/assets/c8300f84-23b2-448b-9dd4-c47090dbd0e5) |
|--------------------------------------------------------------------------------------------------|
| PrizesScreen                                                                                     |

---

## Contribuciones 🤝

Las contribuciones son bienvenidas. Si deseas contribuir, por favor:
1. Haz un fork del repositorio.
2. Crea una nueva rama para tu funcionalidad (`feature/nueva-funcionalidad`).
3. Realiza tus cambios y realiza un commit.
4. Envía un pull request para revisión.

---

## Licencia 📄

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.


