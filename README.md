🏆 Sistema de Gestión de Actividades en Clubes Deportivos

Proyecto SIA (Sistema de Información) de la asignatura INF2236 - Programación Avanzada, Pontificia Universidad Católica de Valparaíso — Periodo 2026-1.

Sistema de escritorio en Java que permite a un club deportivo administrar su catálogo de actividades (fútbol, natación, crossfit, básquetbol, vóleibol, tenis, atletismo, powerlifting y boxeo), controlar el cupo disponible de cada una, gestionar a sus instructores y llevar la inscripción de sus socios.

📋 Descripción del problema

La administración de actividades deportivas dentro de un club suele hacerse de forma manual (planillas o cuadernos), lo que provoca:

Sobreinscripciones o rechazos innecesarios por no saber el cupo real de una actividad.
Falta de trazabilidad de qué socios están inscritos en cada actividad y quién la dicta.
Dificultad para saber rápidamente qué actividades todavía tienen cupos que se puedan promocionar.

Este sistema centraliza esa gestión, valida automáticamente el cupo máximo al inscribir a un socio y permite filtrar en cualquier momento las actividades con cupos disponibles.

✨ Funcionalidades principales
CRUD completo de Actividades: agregar, listar, editar, eliminar y buscar.
CRUD de Socios inscritos por actividad: inscribir, listar, editar, eliminar y buscar.
Validación de cupo máximo al inscribir un socio (CupoExcedidoException).
Validación de existencia al buscar/editar/eliminar actividades o socios (ElementoNoEncontradoException).
Filtro de cupos disponibles por deporte — funcionalidad propia orientada al negocio.
Gestión de instructores con cálculo de pago (sueldo base y pago con bono por alumno a cargo).
Persistencia batch en CSV: carga los datos al iniciar y los guarda al salir.
Exportación de reportes en CSV con el detalle de ocupación de cada actividad.
Doble interfaz de usuario: consola y ventana (Swing), con la misma lógica de negocio.



🧩 Diseño y buenas prácticas
Encapsulamiento: todos los atributos son privados con sus respectivos getters/setters.
Herencia: Persona es una clase abstracta de la que heredan Socio e Instructor.
Colecciones (JCF): GestorClub administra un Map<String, Actividad> (y un Map<String, Instructor>); cada Actividad mantiene, de forma anidada, una List<Socio> con sus inscritos.
Programación defensiva: ningún método público retorna una colección (List/Map) directamente; siempre se entrega un arreglo (Actividad[], Socio[], Instructor[]), evitando que otras capas modifiquen el estado interno sin pasar por las reglas de negocio.
Sobrecarga de métodos: Socio.generarComprobante() / generarComprobante(String) e Instructor.calcularPago() / calcularPago(double, int).
Sobreescritura de métodos: mostrarInfo() en Socio e Instructor, y toString() en Persona y Actividad.
Excepciones propias: ElementoNoEncontradoException y CupoExcedidoException, manejadas con try-catch en las vistas.
Arquitectura en capas: modelo → gestión/negocio → controlador → persistencia / vista, de modo que tanto la consola como la ventana consumen únicamente ControladorClub.
🚀 Cómo ejecutar el proyecto
Requisitos
Java JDK 8 u 11.
NetBeans 21 (o inferior) o Eclipse.
Pasos en NetBeans
File → New Project → Java with Ant → Java Application.
Nómbralo ClubDeportivoSIA y desmarca "Create Main Class".
Copia el contenido de src/clubdeportivo de este repositorio dentro de la carpeta src del proyecto creado.
Copia la carpeta data/ (con los CSV ya poblados) a la raíz del proyecto NetBeans, al mismo nivel que src y build.xml.
Click derecho sobre el proyecto → Properties → Sources y verifica que el "Source/Binary Format" corresponda a tu JDK (1.8 u 11).
Click derecho en Main.java → Set as Main Class (o configúralo en Properties → Run → Main Class: clubdeportivo.Main).
Ejecuta el proyecto (F6).

Al iniciar, el sistema pregunta el modo de uso:

=== Sistema de Gestión de Actividades en Clubes Deportivos ===
¿Cómo desea utilizar el sistema?
1. Consola
2. Ventana (interfaz gráfica)
Datos de ejemplo

El proyecto incluye una carpeta data/ con datos ya poblados (9 instructores, 10 actividades —una por cada deporte— y cerca de 30 socios inscritos). 
Si se elimina esa carpeta, el sistema genera automáticamente ese mismo conjunto de datos de ejemplo en la primera ejecución, por lo que el programa 
siempre puede probarse sin configuración adicional.
