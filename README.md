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
[✅] **Sockets TCP/IP**: Para la comunicación entre el cliente y el servidor 🌍.   
[✅]**Maven**: Herramienta para la gestión de dependencias y construcción del proyecto ⚙️.  
[✅] **IntelliJ IDEA**: Entorno de desarrollo integrado (IDE) utilizado para el desarrollo del proyecto 🧑‍💻.  
[✅]Integracion de consumo de API https://open-meteo.com/en/docs.  
[❌]Desarrollo de API propia.  
[✅]Otras funcionalidades.
  <br/>

## **Funcionalidades requeridas** 🏛️


1. **Conexión del cliente**
    - El cliente se conecta al servidor proporcionando la IP y el puerto.[✅]
    - El cliente debe ingresar un nickname para ser identificado en el chat.[✅]

2. **Mensajes en tiempo real**
    - Los mensajes enviados por cualquier usuario son reenviados a todos los clientes conectados.[✅]
    - El formato de los mensajes es "nickname: mensaje".[✅]

3. **Notificación de nuevos usuarios**
    - Cuando un nuevo cliente se conecta, el servidor notifica a todos los participantes del chat.[✅]

4. **Desconexión controlada**
    - Cuando un cliente se desconecta, el servidor notifica a todos los participantes y actualiza la lista de usuarios
      conectados.[✅]

5. **Comando de salida**
    - Los clientes pueden cerrar la conexión con el comando `/bye`.[✅]

6. **Control de errores**
    - El sistema gestiona errores como fallos en la conexión o intentos de enviar mensajes vacíos.[✅]

  <br/>
  
## **Comunicación de Datos a través de Paquetes** 📦

En este sistema de chat, se ha implementado una clase que suplanta la utilización de un objeto como JSON para la comunicación entre el cliente y el servidor. Esta clase se encarga de encapsular los datos necesarios para la comunicación según su tipo, garantizando una estructura robsuta pero flexible para el intercambio de información.

### **Clase `Paquete` y su Factory** 🛠️

La clase abstracta `Paquete` se utiliza como base para los diferentes tipos de paquetes que se envían entre el cliente y el servidor. Los paquetes encapsulan la dirección IP del emisor y el tipo de paquete (por ejemplo, autenticación, conexión, mensaje, archivo, etc.).

- **`Paquete`**: Es la clase base que define la estructura común de todos los paquetes.
- **`PaqueteFactory`**: Una clase de fábrica que crea instancias de los diferentes tipos de paquetes, como `PaqueteAutenticacion`, `PaqueteConectar`, `PaqueteMensaje`, etc., dependiendo del tipo especificado.

```java
public class PaqueteFactory {

    public static Paquete crearPaquete(TipoPaquete tipo, Object... parametros){
        switch (tipo){
            case AUTENTICACION -> {return crearPaqueteAutenticacion(parametros);}
            case CONECTAR -> {return crearPaqueteConectar(parametros);}
            case PING -> {return crearPaquetePING(parametros);}
            case DESCONECTAR -> {return crearPaqueteDesconectar(parametros);}
            case NOTIFICACION -> {return crearPaqueteNotificacion(parametros);}
            case MENSAJE -> {return crearPaqueteMensaje(parametros);}
            case ARCHIVO -> {return  crearPaqueteArchivo(parametros);}
            default -> throw new IllegalArgumentException("Tipo de paquete no válido: " + tipo);
        }
        ....................................................
    }

   ```

### **Creación de un JAR Compartido** 🛠️

Para conseguir la operabilidad entre el cliente y el servidor, se ha creado un archivo JAR que contiene las clases necesarias para manejar los paquetes. Este JAR actúa como una biblioteca compartida, lo que permite que tanto el cliente como el servidor operen con el mismo tipo de objeto `Paquete` independientemente de quién lo haya creado.

De esta manera, el cliente y el servidor pueden compartir la misma estructura de datos y asegurarse de que ambos lados entienden el formato de los paquetes, evitando inconsistencias en la comunicación.

El archivo JAR generado contiene las clases que permiten la creación, el envío y la recepción de paquetes, y puede ser importado tanto por el cliente como por el servidor para operar con los mismos tipos de datos. Gracias a eso se obtiene una comunicación segura entre ambos componentes del sistema.

## **Flujo de Comunicación** 🔄

### 1. **Inicio** 🚀  
El cliente inicia la conexión con el servidor utilizando la dirección IP y el puerto configurados. El servidor escucha en el puerto especificado y acepta la conexión del cliente.

- **Cliente**: Envía un paquete de tipo `CONECTAR` al servidor.
- **Servidor**: Recibe el paquete y procesa la conexión, creando un nuevo `ClienteHandler` para manejar la comunicación con ese cliente.



### 2. **Autenticación** 🔐  
El cliente debe autenticarse antes de unirse al chat. El servidor verifica las credenciales del usuario (nombre de usuario y contraseña hasheada) en la base de datos.

- **Cliente**: Envía un paquete de tipo `AUTENTICACION` con las credenciales.
- **Servidor**: Verifica las credenciales y responde con un booleano (`true` si la autenticación es exitosa, `false` en caso contrario).



### 3. **Unión a la sala** 🧑‍💻  
Una vez autenticado, el cliente se une a la sala de chat. El servidor notifica a todos los clientes conectados que un nuevo usuario se ha unido.

- **Cliente**: Envía un paquete de tipo `CONECTAR` con su nickname.
- **Servidor**: Agrega al cliente a la sala y difunde una notificación a todos los clientes conectados.



### 4. **Enviar mensajes** ✉️  
Los clientes pueden enviar mensajes de texto al servidor, que los retransmite a todos los usuarios conectados.

- **Cliente**: Envía un paquete de tipo `MENSAJE` con el contenido del mensaje.
- **Servidor**: Recibe el mensaje y lo difunde a todos los clientes en la sala.



### 5. **Enviar Imágenes** 📁  
Los clientes pueden enviar archivos al servidor, que los retransmite a todos los usuarios conectados.

- **Cliente**: Envía un paquete de tipo `ARCHIVO` con la imagen adjunta.
- **Servidor**: Recibe el archivo y lo difunde a todos los clientes en la sala.



### 6. **PING/PONG** 🏓  
El servidor y los clientes pueden intercambiar paquetes `PING` y `PONG` para verificar la conexión.

- **Cliente**: Envía un paquete de tipo `PING` al servidor.
- **Servidor**: Responde con un paquete de tipo `PONG`.


### 7. **Notificación de desconexión** 🛑  
Cuando un cliente se desconecta, el servidor notifica a todos los usuarios en la sala.

- **Cliente**: Envía un paquete de tipo `DESCONECTAR` al servidor.
- **Servidor**: Elimina al cliente de la sala y difunde una notificación de desconexión.



### 8. **Cerrar conexión** 🔒  
El cliente puede cerrar su conexión con el servidor de manera segura.

- **Cliente**: Envía un paquete de tipo `DESCONECTAR`.
- **Servidor**: Cierra los recursos asociados al cliente (socket, flujos de entrada/salida) y notifica a los demás usuarios.



### 9. **Detener el servidor** ⛔  
Si el servidor se detiene, todos los clientes son desconectados y se les notifica.

- **Servidor**: Envía un paquete de tipo `DESCONECTAR` a todos los clientes y cierra sus conexiones.
- **Cliente**: Recibe la notificación y cierra su conexión

---



# NOTAS
- Empaquetar jar   
```bash
jar cvf Paquete.jar .\dam\psp\cliente\model\Paquete.class .\dam\psp\cliente\model\TipoPaquete.class
```