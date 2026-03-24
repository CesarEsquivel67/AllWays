# AllWays

**Accesibilidad para todos.**

AllWays es una aplicación Android desarrollada para el Hackathon de Solana que ayuda a las personas, especialmente a quienes tienen discapacidad visual u otras discapacidades, a encontrar y compartir información de accesibilidad sobre lugares en su comunidad. Los usuarios son recompensados con tokens SOL en Solana Devnet por contribuir con reseñas de accesibilidad verificadas.

---

## Funcionalidades

- **Autenticación** — Inicio de sesión con correo/contraseña mediante Firebase Auth, o continuar como invitado
- **Cartera Solana** — Conecta una cartera Solana para recibir recompensas en tokens (requiere Phantom en un dispositivo físico)
- **Buscar Lugares** — Explora y busca lugares accesibles con fotos
- **Detalles del Lugar** — Consulta descripciones, direcciones e indicaciones de referencia
- **Reseñas de Accesibilidad** — Envía respuestas estructuradas de sí/no para características de accesibilidad como:
  - Señalización en Braille
  - Franjas táctiles para bastón
  - Botones de asistencia
  - Rampas para silla de ruedas
  - Estacionamiento accesible
  - Baños accesibles
  - Señales de audio
- **Subida de Fotos** — Adjunta fotos a lugares y reseñas mediante Cloudinary
- **Sistema de Votos** — Vota por reseñas útiles; los autores ganan tokens SOL en Devnet por publicar reseñas
- **Resumen Comunitario** — Consulta los porcentajes de sí/no de cada característica de accesibilidad por lugar

---

## Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose + Material 3 |
| Navegación | Jetpack Navigation Compose |
| Arquitectura | MVVM + StateFlow |
| Autenticación | Firebase Auth |
| Base de datos | Cloud Firestore |
| Almacenamiento de imágenes | Cloudinary (plan gratuito) |
| Blockchain | Solana Devnet (RPC mediante OkHttp) |
| Carga de imágenes | Coil |
| Red | OkHttp + Gson |

---

## Integración con Solana

-Backend python con Flask que actua como intermediario entre la App Android y Solana Blockchain. Recibe solicitudes HTTP de recompensa, las valida, las guarda en JSON local, y responde con confirmacion. Sin el, la app no puede enviar tokens de Solana

---

## Equipo

Cesar Alejandro Esquivel Calzada

Alonso Gamino Nuñez

Alvaro Obed Mejia Pimentel

---

## Licencia

Licencia MIT. Siéntete libre de usar y construir sobre este proyecto.
