# **Sistema de Chat Cliente/Servidor** 💬🌐

---

## **Introducción** 🎬

Este proyecto es un sistema de chat con arquitectura cliente/servidor basado en **sockets TCP/IP**. Permite hasta **10 usuarios simultáneos**, todos conectados en una única sala de chat. La interfaz gráfica está desarrollada con **JavaFX**..

Los usuarios pueden interactuar entere sí en tiempo real. El servidor se encarga de distribuir los mensajes y gestionar las conexiones.

### **Tecnologías utilizadas** 🚀

✅ **JavaFX** - Creación de interfaces gráficas 🎨  
✅ **Sockets TCP/IP** - Comunicación entre cliente y servidor 🌍  
✅ **Maven** - Gestión de dependencias y construcción ⚙️  
✅ **IntelliJ IDEA** - Entorno de desarrollo 🧑‍💻  
✅ **CSS** - Cascade Style Sheets  🖌️   
✅ **CSS** - Cascade Style Sheets  🖌️   
✅ **Consumo de API** - Integración con https://open-meteo.com/en/docs ☁️   
✅ **PostgreSQL** - Base de datos relacional para almacenamiento persistente de datos  
✅ **Railway** - Plataforma en la nube para desplegar y gestionar PostgreSQL 🚄  
✅ Bcrypt - Algoritmo de hashing para el almacenamiento seguro de contraseñas 🔒  
❌ **Desarrollo de API propia**   
✅ **Otras funcionalidades** 🛠️  

<br/>

## **Funcionalidades principales** 🏛️

1. **Conexión del cliente** 🔌
   - Se conecta proporcionando IP y puerto 
   - Debe ingresar un nickname para identificarse 

2. **Mensajes en tiempo real** 💬
   - Todos los mensajes son reenviados a los clientes conectados 
   - Formato: `nickname: mensaje <hora actual>` 

3. **Notificación de nuevos usuarios** 🔔
   - El servidor avisa cuando alguien se une 

4. **Desconexión controlada** ❌
   - El servidor notifica a los usuarios cuando alguien se desconecta 

5. **Comando de salida** 🚪
   - Los clientes pueden salir con `/bye` 

6. **Gestión de errores** ⚠️
   - Manejo de fallos en la conexión y mensajes vacíos 


<br/>

## **Comunicación con paquetes** 📦

Se ha implementado una clase `Paquete` que encapsula los datos enviados entre cliente y servidor. Esto evita inconsistencias en la comunicación y solidifica la estructura del sistema.

### **Clase `Paquete` y su Factory** 🏗️

🔹 **Inmutabilidad** - No se pueden modificar tras su creación 🔒  
🔹 **Polimorfismo** - Diferentes tipos de paquetes pueden manejarse de forma genérica 🏷️  
🔹 **Encapsulación de datos** - Cada paquete contiene la IP del emisor y su tipo 📜  
🔹 **Extensibilidad** - Permite agregar nuevos tipos sin modificar el código existente 🛠️  

Ejemplo de **`PaqueteFactory`**:
```java
public class PaqueteFactory {
    public static Paquete crearPaquete(TipoPaquete tipo, Object... parametros){
        return switch (tipo) {
            case AUTENTICACION -> crearPaqueteAutenticacion(parametros);
            case CONECTAR -> crearPaqueteConectar(parametros);
            case PING -> crearPaquetePING(parametros);
            case DESCONECTAR -> crearPaqueteDesconectar(parametros);
            case NOTIFICACION -> crearPaqueteNotificacion(parametros);
            case MENSAJE -> crearPaqueteMensaje(parametros);
            case ARCHIVO -> crearPaqueteArchivo(parametros);
            case ERROR -> crearPaqueteError(parametros);
            default -> throw new IllegalArgumentException("Tipo de paquete no válido: " + tipo);
        };
    }
}
```

🔹 **JAR Compartido** 📦
Se ha generado un **JAR** con las clases de `Paquete`, para garantizar que tanto cliente como servidor operen con la misma estructura de datos.

<br/>

## **Flujo de comunicación** 🔄

1. **Autenticación** 🔐
   - Cliente envía un `PaqueteAutenticacion` con sus credenciales
   - Servidor consulta en la base datos las creendenciale s y notifica.

2. **Unión a la sala** 👥
   - Cliente envía un `PaqueteConectar` con su nickname
   - Servidor lo agrega y notifica a todos

3. **Mensajería en tiempo real** 💬
   - Los clientes envían mensajes y el servidor los distribuye

4. **Desconexión** 🚪
   - Cliente envía `PaqueteDesconectar`, y el servidor avisa al resto

5. **Cierre del servidor** ⛔
   - Todos los clientes reciben un `PaqueteDesconectar`

<br/>

## **Extras y funcionalidades adicionales** 🌟

🔹 **Comandos especiales** ⚡
   - `/tiempo` - Muestra la temperatura actual usando la API del clima 🌡️
   - `/ping` - Muestra la latencia entre cliente y servidor 📶

🔹 **Envío de archivos** 📎
   - Permite compartir imágenes en el chat, descargables con doble clic 🖼️

🔹 **Links detectados** 🔗
   - Los enlaces enviados en el chat cambian de estilo visualmente para diferenciarlos

🔹 **Temas personalizables** 🎨
   - **Dark mode** (por defecto) 🌑
   - **Light mode** ☀️

<br/>

## **Conclusión** 🔮
La implementación de paquetes me ha lastrado mucho ya que podía implementar `Json` y simplifacar muchos porblemas que me he encontrado mientras el proyecto crecía. No obstante nunca había utilizado el patrón de diseño `Factory` y me pareció una buena idea para aplicarlo.

Si la entrega fuera dos semanas más tarde seguiría trabajando en este proyecto (los commits lo reflejan). A medida que avanzo, siempre quiero probar algo nuevo o mejorar lo anterior. Algo que no me gusta es que noto que he sobrecargado mucho el `controller` ademas de que tengo metodos que muy bien se pueden refactorizar. Es lo que tiene ir aprendiendo sobre la marcha, que te modifica constantemente el esquema inicial.

<br/>

## 🔧 **Posibles mejoras**
✅ Implementación de una API REST para mejorar la gestión de usuarios y mensajes.  
✅ Soporte para mensajes privados entre usuarios.  
✅ Encriptación de mensajes para mayor seguridad 🔐.  
✅ Integración con WebSockets para optimizar la comunicación.  
✅ Migrarlo a Aplicación web o móvil y extender su uso a dispositivos Android/iOS 📱.  




