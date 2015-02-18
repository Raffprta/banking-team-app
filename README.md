## CSC2022 Team 19 Git Repository

### Introduction
This repository is to be used for all programming-related work in connection with the **CSC2022 Team Project**.

### Structure
* **/androidapp** - The Android app project.
* **/backend** - The website/backend system.
* **/experimental** - Mockups and experiments go here.
* **/keys** - Security keys required for signing the experimental code and using the shared API keys included in the source.
* ... (more to follow)

### Installing Git
This section will cover how to install the Git subversion control system on your computer!

1. Install Git for your platform of choice [here](http://git-scm.com/book/en/v2/Getting-Started-Installing-Git).
2. Follow the installation process; if you are using the Windows installer, then chose the install options **Windows Explorer Integration** and check both **Git Bash Here** and **Git GUI here** when prompted. Additionally, select **Use Git Bash Only** and **Checkout Windows-style, commit Unix-style line endings** in the next configuration prompts. 
3. If that installation fails under Windows, then you may need to install [MinGW](http://www.mingw.org/) separately prior to installing Git, but I believe the Git installer *should* cater for this dependancy and install it if it's not located. 
4. **Optional but highly recommended:** Feel free to try out the official Git tutorial [here](https://try.github.io/levels/1/challenges/1). It will familiarise you with the Git command line utilities.
5. To test that Git was correctly installed on your machine, make a test folder with a test file somewhere on your system, right click (or open the bash terminal) in that directory, and try typing `git init` or clicking `git init here` or even trying the Git GUI.
6. Git init marks the folder as a git repository, meaning you now can start to add files and folders to the repository and commit them when changes are applied to them! You should now read the next section on setting up the team's repository, though you are more than welcome to work on a repository of your own prior to further familiarise yourself with the git system.

### Setting up the team repository on your local machine
The following will show a step by step guide to cloning the team repository and how to contribute to it. This section will be assuming you are using Git Bash, though I will try to mention the process with a GUI counterpart as well where possible.

1. Create a folder called `Team19`. This will be where we will be cloning the team's repository into.
2. Within this folder, open Git Bash and type `git clone https://github.com/dwhinham/team19.git`. This will make a copy of the team's code repository and put it in your folder. Cloning should also be possible via the GUI by clicking [this](github-windows://openRepo/https://github.com/dwhinham/team19) link.
3. Since you have cloned the project from GitHub, the URL where you will be pulling and pushing updates will already be preconfigured, hence try doing a `git pull`. Login at the prompt using your GitHub username & password. This should give you a message along the lines of `Already up to date` as you just cloned the repository a few moments ago. If it can't detect where to pull the information from then type `git remote add origin https://github.com/dwhinham/team19.git` into the bash terminal. You can also use the Git GUI for this, via the `Remote` menu bar.
4. **Note:** Instead of `git pull` you may do `git fetch -m` instead. Git pull merges the code you fetch with what you have in the repository whilst fetch will just fetch it ensuring you never overwrite your local copy. This may be important later on (where -m is the master branch). It is good to perform this command every so often to update your local repo.
5. You now have the team's repository set up on your local computer, however, **everyone** has master access working within the master branch, meaning anyone can modify any part of the repository. Thus, it's best *for now* that you don't try to modify and push anything to the repository unless it's within its own folder (say a new mockup within the experimental folder).
6. To read more about adding, committing and pushing and more, I really recommend this short [tutorial](https://rogerdudler.github.io/git-guide/) as well as the previously linked tutorial. If you want to experiment with Git you should go back to the test repository you created during the first stage of installation and trying to add & commit some files to your own private repository (not the team one). The IDEs we will be using both support Git plugins natively, too, meaning you can do common Git tasks like adding, committing and pushing files within the IDE!


### Preparing the build environment - Android
The recommended tools for building are currently:

* **Android Studio 1.0+** - [available here](http://developer.android.com/sdk/index.html) for Windows/OS X/Linux.
* **Android SDK Tools / SDK 21 (Lollipop)** - Installed by Android Studio setup wizard.

For emulating Android devices:

* **Official Android device emulator** (included in SDK) with [Intel HAXM Android device emulator acceleration package](https://software.intel.com/en-us/android/articles/intel-hardware-accelerated-execution-manager). This package will speed up official Google Android emulators using **x86** or **x86_64** CPU architectures. *Highly recommended*.
  * **NOTE**: When creating Android virtual devices with the official emulator, be sure to choose **x86_64** or **x86** CPU architectures and not **ARM**. Also, be sure to choose a target including **Google APIs (Lollipop)**.

To install the build environment under **Windows**:

1. Run the installer bundle and follow the instructions to install Android Studio on your system.
2. Launch Android Studio.
3. Copy the `debug.keystore` file from this repository to `C:\Users\<yourname>\.android\` (overwriting any existing file). This ensures Android Studio will sign your compiled applications with the same security code as other team members, allowing us to share Google API keys.

If you want to build a project from this repository, use the *Import Project...* feature of Android Studio to import it and generate the necessary files specific to your system.

### Preparing the build environment - Web Development
The following will help you set up tools for web server and client side development. These are reccomended tools, but if you wish to use other tools for development (for example, a text editor instead of an IDE) you can skip that installation process but the AMP bundle must be installed in some shape or form for you to be able to run and develop the web site.

#### The PHP Development Environment

1. Register a free student account with [Jetbrains](https://www.jetbrains.com/estore/students) - this will allow you to download their products for free.
2. Download [PHPStorm](http://www.jetbrains.com/phpstorm/download) - this IDE comes with various useful frameworks and tools pre-configured to it, such as twig and dependency managers. 
3. Install it to a convenient location (the detailled installation process is elaborated in the link above). 
4. The first time you open PHPStorm you will be prompted for a license key, select the option "JetBrains Account" and use the login details you previously created.
5. You now have a working version of PHPStorm. The next step is to set up a working version of the Apache web server, PHP and MySQL.

#### Installing the Apache, MySQL and PHP Bundle
**Word of warning, this assumes you have not already installed these items separately or in a bundle already, if you have, then proceed to configuring PHPStorm to your AMP installations**

1. Download and install **one** of either [XAMPP](https://www.apachefriends.org/index.html), [WAMP](http://www.wampserver.com/en/), [LAMP](http://lamphowto.com/) or [MAMP](http://www.mamp.info/en/). XAMPP *should* be cross-platform, but I've only tested it on Windows. The rest are Windows, Linux and Mac specific solutions, respectively. I personally recommend XAMPP due to it being cross platform. 
2. Follow the installation process (generally an installer wizard) and apache, PHP and MySQL will be installed.
3. Open up the respective application control panel to your installation. You can now start the services `Apache` and `MySQL` though at this stage of the project, PHP is likely all you will need running unless you want to do some Database development. By going into the `Config` option you may also set these services to run at the operating system's startup as services.
4. To test that you have a working AMP package, go to your web browser and visit `127.0.0.1`. This should load a page specified to your AMP bundle generally.
5. **Optional**: If that does not work, or you wish to change the port your server listens at (because of other servers listening at port 80) then locate the `apache/conf/httpd.conf` file in your installation directory and change the line `Listen 80` to `Listen #` where # is the number of the port. This will mean you will have to type `127.0.0.1:#` when accessing pages loaded by the server in the future.
6. You should now have a working AMP bundle! Feel free to configure it by adding in a server password, under the `127.0.0.1/security` page.

### Configuring PHPStorm to your AMP installation

1. Open up PHPStorm and go to **File > Settings**
2. Follow this short [guide](https://confluence.jetbrains.com/display/PhpStorm/Installing+and+Configuring+XAMPP+with+PhpStorm+IDE#InstallingandConfiguringXAMPPwithPhpStormIDE-IntegratingthePHPexecutable) on integrating XAMPP with PHPStorm. If you downloaded another bundle, then the process should be the same within their respective install directories.
3. Create a new test project, and within it create a file called `index.php`. Within it type: `<?php phpinfo(); ?>` and save it.
4. Now run the project on your favourite browser (if it prompts you to configure a PHP intepreter to your project select from the dropdown the one you set up in the above tutorial). If you a table of debug information related to PHP properties then everything relating to setting up a working environment for client side + PHP development. Feel free to also run the quick MySQL configuration illustrated on that web page though at this stage it is probably not necessary.

### Coding style guidelines
Some hints for coding styles and conventions used in this project.

#### Android
* XML IDs for buttons, views, and so on - Use **CamelCase** for these, ***suffixed*** with the type. Examples: **loginButton**, **userNameEditText**, **passwordEditText**, **loginFormScrollView**. If you have multiple items that can be described in the same way, add a number suffix, for example **logoTextView1**, **logoTextView2**.
* Strings should always be placed into the `strings.xml` resource file, even when not required from Java. String names are freeform, but should be **snake_case** and descriptive. If the string is used for a menu action or an error message, consider adding a prefix. Example: `<string name="error_no_results">No results found.</string>`, `<string name="action_settings">Settings</string>`.

#### Twig Templates
 * Use Twig comments - `{# #}` instead of HTML comments - `<!-- -->` within the templates. This ensures that comments only intended to be seen by us (the developers) will not be included in the web pages that users receive.
