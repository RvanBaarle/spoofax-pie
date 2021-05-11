# Installing the Spoofax 3 language workbench environment

This tutorial gets you started with language development in Spoofax 3 by installing the Spoofax 3 Eclipse language workbench (LWB) environment.

## Requirements

Spoofax 3 runs on the major operating systems:

* {{os.windows}} (64 bits)
* {{os.macos}} (64 bits)
* {{os.linux}} (64 bits)

## Download

To get started, we will download a premade Eclipse installation that comes bundled with the Spoofax 3 LWB plugin. Download the latest version for your platform:

* {{download.dev.lwb.eclipse.jvm.windows}}
* {{download.dev.lwb.eclipse.jvm.macos}}
* {{download.dev.lwb.eclipse.jvm.linux}}

These are bundled with an embedded Java Virtual Machine (JVM) so that you do not need to have a JVM installed. If your system has a JVM of version 11 or higher installed, and would rather use that, use the following download links instead:

* {{download.dev.lwb.eclipse.windows}}
* {{download.dev.lwb.eclipse.macos}}
* {{download.dev.lwb.eclipse.linux}}

## Unpack

Unpack the downloaded archive to a location with write access. Write access is required because Eclipse needs to write to several configuration files inside its installation.

!!! warning
    On {{ os.windows }} do not unpack the Eclipse installation into `Program Files`, because no write access is granted there, breaking both Eclipse and Spoofax.

## Running Eclipse

Start up Eclipse, depending on your operating system:

* {{os.windows}}: run `Spoofax3/eclipse.exe`
* {{os.macos}} run `Spoofax3.app`
* {{os.linux}} run `Spoofax3/eclipse`

!!! warning
    {{ os.macos }} Sierra (10.12) and above will mark the unpacked `Spoofax3.app` as "damaged" due to a modified signed/notarized application, because we have modified the eclipse.ini file inside it. To fix this, open the Terminal, navigate to the directory where the `Spoofax3.app` file is located, and execute:

    ```
    xattr -rc Spoofax3.app
    ```

After starting up, choose where your workspace will be stored. The Eclipse workspace will contain all of your settings, and is the default location for new projects.

## Configuring Eclipse's preferences

Some Eclipse preferences unfortunately have sub-optimal defaults. After you have chosen a workspace and Eclipse has completely started up (and you have closed the Welcome page), go to the Eclipse preferences and set these options:

* General ‣ Startup and Shutdown
    * Enable: Refresh workspace on startup
* General ‣ Workspace
    * Enable: Refresh using native hooks or polling

Finally, we need to make sure that Eclipse has detected an installed JRE. Open the Eclipse preferences and go to the Java ‣ Installed JREs page:

* If there are no installed JREs, and you've downloaded an Eclipse installation *with an embedded JVM*, press `Search...` and navigate to the location where you unpacked the Eclipse installation, and choose the `jvm` directory in it. Then press the checkmark of the JRE to activate it.
* If there are no installed JREs, and you've downloaded an Eclipse installation *without an embedded JVM*, press `Search...` and navigate to the location where your JVM installed, and choose it. Then press the checkmark of the JRE to activate it.
* If there are one or more installed JVMs, but none are selected, select an appropriate one by pressing the checkmark.
* If there are one or more installed JVMs, and one is selected, you are good to go.

!!! note
    These preferences are stored per workspace. If you create a fresh workspace, you have to re-do these settings. You can create a new workspace with copied preferences by selecting File ‣ Switch workspace ‣ Other..., and then checking `Preferences` under `Copy settings`.