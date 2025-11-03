# Practica 3 - Desarrolla un software con concurrencia para servidores locales.
### Concurrencia y Paralelismo
### Alumno: Moisés Abraham Sunza Vázquez

<hr>

### Características Añadidas al Chat Concurrente

#### Conexión Dinámica y Comandos
Se mejoró la flexibilidad del cliente y se implementó una capa de lógica de comandos en el servidor (ClientHandler).

1. Conexión Inicial
Comando start-conection: Permite al usuario introducir la dirección IP y el puerto deseado al inicio de la aplicación, haciendo la conexión dinámica.

2. Comandos de Usuario (Implementados en ClientHandler)

- /send-msg [usuario] [mensaje]: Envía un mensaje privado a un usuario específico.

- Mejora: El sistema notifica al remitente si el usuario objetivo no está conectado.

- /change-userName [nuevo_nombre]: Cambia el apodo del usuario.

-   Mejora: El servidor notifica el cambio de nombre a todos los usuarios en el chat.

- Mensaje Global por Defecto: Cualquier entrada del usuario que no sea un comando se trata y se distribuye automáticamente como un mensaje global a todos los clientes.

- /list-users: Muestra una lista de todos los nombres de usuario actualmente conectados al servidor.
