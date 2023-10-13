<div align="center">

# Coffee
### A powerful universal command handler

</div>

# Introduction
Coffee is an adaptable command handler meant to be able to run anywhere. 
This is built mainly for personal use and isn't the cleanest around, but it gets the job done 
and allows for rapid development of commands.

## Implementations
Coffee currently supports the following implementations:

| Implementation | Supported |
|----------------|:---------:|
| Bukkit         |     ✅     |
| JDA            |     ❌     |

## Installation

### Maven
```xml
<!-- Repository -->
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<!-- Dependency -->
<dependency>
    <groupId>com.github.SantioMC</groupId>
    <artifactId>Coffee</artifactId>
    <version>VERSION</version>
</dependency>
```

### Gradle (groovy)
````groovy
// Repository
repositories {
    maven { url 'https://jitpack.io' }
}
````

````groovy
// Dependency
dependencies {
    implementation 'com.github.SantioMC:Coffee:VERSION'
}
````

### Gradle (kotlin)
````kotlin
// Repository
repositories {
    maven("https://jitpack.io")
}
````

````kotlin
// Dependency
dependencies {
    implementation("com.github.SantioMC:Coffee:VERSION")
}
````

## Quick Start

### Bukkit
Getting started with Coffee is pretty simple and a lot of the work is done for you.
For this example, we'll be using the Bukkit implementation, but the same principles apply to any other implementation.

> While Coffee is built to run on any platform, it's recommended to use Kotlin for the best experience.

#### Registering a command
Before getting into making the commands, let's go over how you would register them after you create them.
Coffee has two ways of doing this:

```kotlin

// Load the implementation
Coffee.bundle(CoffeeBukkit(pluginInstance))

// Register commands
Coffee.brew(PrintCommand::class.java) // Register one or more classes
Coffee.brew("com.example.commands") // Register all classes in a package

```

#### Creating a command

```kotlin
@Command
object PrintCommand {
    fun main() { // Command: /print
        println("Hello world!")
    }
}
```

As you can see, creating a command is as simple as creating a class or object, and annotating it with `@Command`.
Coffee is doing a lot of the work for you to infer the command name, and to infer the proper function to run, 
but you can also specify it manually inside the command annotation.

```kotlin
@Command("print")
object PrintCommand {
    ...
}
```

#### Accessing the sender
This functionality varies between implementations, but for Bukkit there are two ways to access the sender.

The first way is to simply create a parameter at the beginning of the function with one of the following types:

| Class Type             | Availability          |
|------------------------|-----------------------|
| `CommandSender`        | Everyone              |
| `Player`               | Restricted to Players |
| `ConsoleCommandSender` | Restricted to Console |

```kotlin
@Command
object HelloCommand {
    fun main(sender: CommandSender) { // Command: /hello
        sender.sendMessage("Hello!")
    }
}
```

The second way is to use the `@Sender` annotation that the Bukkit implementation provides.
This annotation can be used on any parameter, and will automatically be filled with the sender.

```kotlin
@Command
object HelloCommand {
    
    fun main( // Command: /hello
        @Sender sender: CommandSender
    ) {
        sender.sendMessage("Hello!")
    }
    
}
```

While there's not much of a difference, the annotation allows the parameter to be placed anywhere in the function signature.

#### Accessing arguments
Coffee makes it super simple handle arguments, and tries to support all the functionality a normal Kotlin function would have.

> It is not possible to default a parameter, Coffee doesn't support this functionality due to limitations provided by the reflection API.

```kotlin
@Command
object EchoCommand {
    
    fun main( // Command: /echo <message>
        @Sender sender: CommandSender,
        message: String
    ) {
        sender.sendMessage(message)
    }
    
}
```

##### → Optional arguments

Like in normal Kotlin functions, you can make arguments optional by adding a `?` to the end of the type.

> For those who are using Java, you can use the `@Optional` annotation to achieve the same effect.

```kotlin
@Command
object EchoCommand {

    fun main( // Command: /echo [message]
        @Sender sender: CommandSender,
        message: String?
    ) {
        sender.sendMessage(message)
    }

}
```

##### → Infinite arguments

While not always perfect, Coffee supports infinite arguments by using the `vararg` keyword.
**Please keep in mind this functionality is still in development and might produce buggy results**

```kotlin
@Command
object EchoCommand {
    
    fun main( // Command: /echo [message]
        @Sender sender: CommandSender,
        vararg message: String
    ) {
        sender.sendMessage(message.joinToString(" "))
    }
    
}
```

#### Adapters

Coffee has a few built-in adapters to make a lot of the process plug-and-play, however these default adapters can be
overwritten by creating your own, or create your own adapters for custom class types.

The following adapters are built-in: `Double`, `Float`, `Integer`, `String`, `Player` *(Bukkit Implementation)*

##### Creating an adapter

The following is the adapter for the `Player` class in the Bukkit implementation.

```kotlin
object PlayerAdapter: ArgumentAdapter<Player>() {
    override val type: Class<Player> = Player::class.java

    override fun adapt(arg: String): Player? {
        return Bukkit.getPlayer(arg)
    }

    override val error: String = "The player '%arg%' was not found"
}
```

Alternatively, you can also use the helper method for simpler adapters.

```kotlin
Coffee.bind(UUID::class.java) {
    UUID.fromString(it)
}
```