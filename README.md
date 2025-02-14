# **Sistema de Chat Cliente/Servidor** 💬🌐

---

## **Introducción** 🎬

Este proyecto tiene como objetivo el desarrollo de un sistema de chat con arquitectura cliente/servidor utilizando *
*sockets TCP/IP**. El servidor podrá gestionar múltiples conexiones de clientes de manera simultánea (hasta 10 usuarios)
y permitirá que todos los participantes se comuniquen en una única sala de chat. La interfaz gráfica será implementada
utilizando **JavaFX**, ofreciendo una experiencia fluida y sencilla para el usuario.

El sistema de chat permitirá a los usuarios interactuar en tiempo real, enviar mensajes, unirse y abandonar la sala de
chat, todo ello gestionado por el servidor que redistribuirá los mensajes a todos los clientes conectados.

### Tecnologías utilizadas e implementaciones:

[✅] **JavaFX**: Framework para la creación de interfaces gráficas de usuario 🎨.  
[❌] **Sockets TCP/IP**: Para la comunicación entre el cliente y el servidor 🌍.   
[❌]**Maven**: Herramienta para la gestión de dependencias y construcción del proyecto ⚙️.  
[❌] **IntelliJ IDEA**: Entorno de desarrollo integrado (IDE) utilizado para el desarrollo del proyecto 🧑‍💻.  
[❌]Integracion de consumo de API.  
[❌]Desarrollo de API propia.  
[❌]Otras funcionalidades.


---

## **Arquitectura** 🏛️

### **Cliente - Servidor** 🌐

1. **Servidor**:
    - Gestiona hasta 10 conexiones de clientes simultáneas.
    - Solicita un puerto al arrancar y se queda a la espera de clientes.
    - Muestra todos los mensajes de los usuarios con el formato "nickname: mensaje".
    - Reenvía los mensajes recibidos a todos los clientes conectados.
    - Gestiona la desconexión de clientes, actualizando el número de usuarios conectados.

2. **Cliente**:
    - Se conecta al servidor introduciendo la IP y el puerto.
    - Solicita el nickname al iniciar la conexión.
    - Envía los mensajes que el usuario escribe al servidor para que sean reenviados a todos los demás.
    - Muestra los mensajes recibidos de otros clientes en tiempo real.
    - Permite la desconexión del chat con el comando "/bye".

---

## **Flujo de Comunicación** 🔄

1. **INICIO** 🚀  
   El cliente se conecta al servidor ingresando la dirección IP y el puerto. A continuación, se le solicita un nickname.

2. **UNIÓN A LA SALA** 🧑‍💻  
   Una vez que el cliente se conecta al servidor, el servidor notifica a todos los participantes que un nuevo cliente se
   ha unido al chat.

3. **ENVIAR MENSAJES** ✉️  
   Los mensajes que el usuario escribe en su cliente son enviados al servidor, quien se encarga de distribuirlos a todos
   los clientes conectados.

4. **NOTIFICACIÓN DE DESCONEXIÓN** 🛑  
   Cuando un cliente se desconecta, el servidor notifica a todos los participantes que el cliente ha dejado el chat.

5. **CERRAR CONEXIÓN** 🔒  
   El cliente puede usar el comando `/bye` para cerrar su conexión con el servidor.

6. **FIN** 🛑  
   Si el servidor se cierra, todos los clientes son desconectados y se muestra un mensaje que informa de la desconexión.

---

## **Funcionalidades del Chat**

1. **Conexión del cliente**
    - El cliente se conecta al servidor proporcionando la IP y el puerto.[❌]
    - El cliente debe ingresar un nickname para ser identificado en el chat.[❌]

2. **Mensajes en tiempo real**
    - Los mensajes enviados por cualquier usuario son reenviados a todos los clientes conectados.[❌]
    - El formato de los mensajes es "nickname: mensaje".[❌]

3. **Notificación de nuevos usuarios**
    - Cuando un nuevo cliente se conecta, el servidor notifica a todos los participantes del chat.[❌]

4. **Desconexión controlada**
    - Cuando un cliente se desconecta, el servidor notifica a todos los participantes y actualiza la lista de usuarios
      conectados.[❌]

5. **Comando de salida**
    - Los clientes pueden cerrar la conexión con el comando `/bye`.[❌]

6. **Control de errores**
    - El sistema gestiona errores como fallos en la conexión o intentos de enviar mensajes vacíos.[❌]

---


# NOTAS
- Empaquetar jar   
```bash
jar cvf Paquete.jar .\dam\psp\cliente\model\Paquete.class .\dam\psp\cliente\model\TipoPaquete.class
```