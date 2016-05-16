# Team 2 - Napkin

###Members
* Ty Daniels (Project Lead)
* Edwin Young (Tech Lead)
* Andrey Barsukov
* Chris Dea
* Myles Pedronan
* Sang Saephan
* Lee Thomas

###Getting Started
New to Git? Run the following commands:

    $ cd path/to/your/android/studio/workspace
    $ git clone git@github.com:sfsucsc413/t2.git
    $ cd t2
    $ git checkout development
    $ git branch $(your first name)
    $ git checkout $(your first name)
    $ git push origin $(your first name)



###Troubleshooting

If you have issues with the commands above, please look below.

    $ git clone git@github.com:sfsucsc413/t2.git
    git: not a recognized command or directory

Mac users: Install XCode, run it, and agree to user license.  

Linux (Debian distros) users: `$ sudo apt-get install git`  
Linux (Fedora distros) users: `$ sudo yum install git`  

Windows users: Download and install [Git Bash for Windows](https://git-for-windows.github.io). Select the "Checkout Windows-style, commit Unix-style" option in the installer.

    $ git clone git@github.com:sfsucsc413/t2.git
    Permission denied (publickey).
    fatal: Could not read from remote repository.

    Please make sure you have the correct access rights
    and the repository exists.

You do not have an SSH key, or you have not added an SSH key to your GitHub account. Please see GitHub's [Generating SSH Keys](https://help.github.com/articles/generating-ssh-keys/) page for help.

