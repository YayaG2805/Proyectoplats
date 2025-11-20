PiggyMobile

PiggyMobile es una aplicación móvil de ahorro dirigida principalmente a jóvenes que buscan organizar sus metas financieras de manera sencilla. Ofrece tres modalidades de ahorro:

* Medio: metas a corto o mediano plazo con aportes moderados
* Alto: metas agresivas con aportes más frecuentes o mayores
* Imprevisto: fondo de emergencia con aportes pequeños y constantes

El proyecto pertenece al curso Programación de Plataformas Móviles (Plats) y tiene como objetivo promover hábitos de ahorro, registrar movimientos y mostrar el progreso de las metas del usuario.

Servicios:
Autenticación – Firebase Authentication
Rol: permitir el registro e inicio de sesión con correo y contraseña, personalizando las metas y protegiendo secciones privadas de la aplicación.

Base de datos local – Room Database
Rol: almacenar localmente las metas, aportes, modos de ahorro y progreso.
Uso: permite acceder a la información sin conexión y mejora el rendimiento de la aplicación.

Librerías

Retrofit y OkHttp: Para realizar peticiones HTTP y comunicarse con servicios web externos.
Moshi o Gson: Para convertir datos JSON en objetos Kotlin y viceversa.
Room: Para manejar la base de datos local del dispositivo.
Kotlin Coroutines y Flow: Para realizar operaciones asíncronas y manejar flujos de datos sin bloquear la interfaz.
ViewModel y Lifecycle: Para separar la lógica de la interfaz y mantener los datos durante los cambios de configuración.
Jetpack Compose: Para construir la interfaz de usuario de forma declarativa y moderna.
Hilt: Para gestionar dependencias y simplificar la creación de ViewModels y repositorios.
Coil: Para cargar imágenes o íconos remotos de manera eficiente.

Notas de arquitectura
El proyecto sigue el patrón MVVM, con repositorios que conectan las fuentes de datos (API y Room) y ViewModels que gestionan los estados de la interfaz (carga, datos y errores).

Roadmap corto
Agregar validaciones según modalidad de ahorro.
Mostrar gráficas del progreso de metas y acumulado mensual.
Sincronizar datos con la nube mediante autenticación.
Incluir consejos de ahorro dinámicos a través de una API local o externa.

Integrantes:
Norman Aguirre - 24479
Miguel Carranza - 24458
Diego Guevara - 24128
Curso: Programación de Plataformas Móviles
Período: 2025

