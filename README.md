# David Server Plugin (DSP)

This is a plugin for the DMC Minecraft Server Project which I worked on for about 2 years now, from November 19th 2020 until October 10th 2022.

First, I want to say that this code is really not good. So I recommend having a strong heart because heart attacks are common, LOL.  
As I said, the progress I made during these 2 years is shown in these files because many of them are not edited ever since.

So take a coffee and enjoy this shitty code :)

## Structure of the code

This project is not well organized, to say it nice.

Mainly, our plugin depends on Maven for dependencies, but for building we rely on the IntelliJ build system.  
Aswell, we have two projects setup, `David Server Plugin.iml` and `dsp.iml`.  
Furthermore, `src/main/java` includes two dependencies, I didn't add via Maven but rather by source.

I don't know why I did all of this but I have to work on this. If I would remain coding this project I probably would refactor the code to one project, all Maven and other, but I don't want to spend any more time in a project that does not continue to be used.

If you want to refactor the code yourself, feel free to contact me on Discord or create a pull request.

## Disclaimer

Our plugin **saves** IP-addresses of the users joining the Minecraft Server. This is done because we don't want them to enter the Login-Token every time they login but rather only if they join with an unknown IP-address.  
If you want to remove this, comment out line `46` in `src/main/kotlin/dev/marius/dsp/Login.kt`.  
The entire code for saving the IPs is located in `src/main/kotlin/dev/marius/dsp/JavaLogin.kt`.

## Features

The plugin's main goal is to have a Whitelist via Discord.

The developer has to specifiy the channel id for a registration channel. Users can now send there are command with there Minecraft-Username that they want to register in the project. Then they get a message from the bot with a unique Login-Token.

When the user tries to join the server, the server will block him until his username is declared as registered. After registering, the user has to go on the server and enter the Login-Token.

We done this because DMC supported Cracked-Accounts and we didn't want to let players join as other players so we added this token feature.

There are also some other things the bot can do like banning (tempban should be supported aswell) and deleting user accounts aswell as editing them.

One other thing I started developing was a connection between the chat of Minecraft and Discord. This code can be found in the project but is not supported by the plugin.

## Installation

I recommend using [IntelliJ IDEA](https://jetbrains.com/idea) for this, because this IDE supports all the features I used in this code.

Just clone the repository and open it in IntelliJ. That's it.

If you want to **use** the plugin, you have to create the settings-file. A sample file is already created in `src/main/kotlin/dev/marius/dsp/Settings.sample.kt`. Edit this file and remove the `.sample` from the name.

## Building

There are two ways to build the project.

1. you can use the IntelliJ internal build tool by going on the topbar to `Build -> Build Artifacts -> David Server Plugin:jar -> Build`. That should output a JAR-file into the `out` directory.

2. you can use Maven to build the project. These should output to the `target` directory.
   1. In IntelliJ, go on the sidebar to `Maven -> dsp -> Run Configurations -> build`.
   2. In the terminal, run `mvn clean package`.

## Support

You can get support by...

1. opening an issue
2. writing me on Discord (`Marius#0686`)
3. writing me an email (`avolgha@gmail.com`)
4. joining the DMC Discord: [invite](https://discord.gg/5U8aww6Pzm)
