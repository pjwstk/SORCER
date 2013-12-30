# .cshrc
# Mike Sobolewski, CS TTU
# This file is the first initialization of the C Shell.  It runs in every
# shell, and runs *before* .login.

# It is expensive to set path here.  But, it keeps network oriented things
# happy even though it might frustrate temporary changes (size of path < 7
# helps to alleviate the latter).  For example, `rsh' doesn't run .login, and
# so the path is needed for commands executed by rsh.

unset autologout

if ($?ARCH == 0 || $?PLATFORM == 0 || $#path < 7) then

    # The core path

    set corepath = (/bin /usr/bin /common/bin)
    set coreman = /usr/share/man

    # Additional system searches

    set syspath = (/sbin /usr/sbin /usr/etc)

    # Common files get searched before the system files

    set commonpath = (/common/all/bin)
    set commonman = /common/all/man

    # Local files get searched before common files

    set localpath = (/usr/local/bin )

    # User binaries get searched before any of the above

    set userpath = ($HOME/bin /reserach/sorcer/bin)
    set userman = $HOME/man:/research/sorcer/man

    # Set an initial path that includes architecture independent files
    unset path
    set path = (. $userpath $localpath $corepath $syspath $commonpath)

    # manual path needs to be kept in sync with path variable
    setenv MANPATH $userman\:$coreman\:$commonman

    # Determine the target machine architecture

    if ($?ARCH == 0 || $?PLATFORM == 0) then

	# arch is a C-shell script on some systems.  Set $ARCH
	# to something... so that this doesn't run recursively.

	setenv ARCH unknown 
	setenv PLATFORM unknown
        setenv ARCH `arch`
	setenv PLATFORM `uname`

    endif

    # Add architecture-dependent files to the path

    if ($ARCH == 'unknown') then
	echo "How to determine architecture?"
    else
	set commonpath = ( /common/$ARCH/bin $commonpath )
	set commonman = /common/$ARCH/man:$commonman

	# Special architectural kludges
	switch ($PLATFORM)
	    case "Darwin":
		# Mac OS X
		setenv JAVA_HOME /Library/Java/Home
		set localpath = ( /usr/X11R6/bin $JAVA_HOME/bin $localpath )
    		set corenman = /usr/X11R6/man:$coreman
		#setenv ENSCRIPT "-2Grh"
		#configure fink
		source /sw/bin/init.csh
		breaksw

	    case "Linux":
		setenv JAVA_HOME /usr/java
		set localpath = (/usr/X11R6/bin $JAVA_HOME/bin /java $localpath)
    		set corenman = /usr/X11R6/man:$coreman
		#setenv ENSCRIPT "-2Grh"
		breaksw

	    case "SunOS":
		setenv JAVA_HOME /usr/java
		set localpath = (/usr/X/bin $JAVA_HOME/bin $localpath)
		setenv LD_LIBRARY_PATH /usr/lib:/lib
    		set corenman = /usr/X/man:$coreman
		set userman = /opt/sfw/man:$userman
		#setenv ENSCRIPT "-2rG"

		# get programming language and other stuff
		#set corepath = ( /usr/ucb /usr/dt/bin /usr/ccs/bin $corepath )
		set corepath = ( /usr/dt/bin /usr/ccs/bin $corepath )
		foreach dir (/opt/*/bin)
			set corepath = ( $dir $corepath )
		end
		breaksw
	endsw

	# Finally set the path
	set path = (. $userpath $localpath $commonpath $corepath $syspath)
	# manual path needs to be kept in sync with path variable
	setenv MANPATH $userman\:$commonman\:$coreman

    endif

    # clean up
    unset userpath
    unset userman
    unset localpath
    unset commonpath
    unset commonman
    unset corepath
    unset coreman
    unset syspath

endif

# Environment setup needed for remote shells and such
# (because rsh does not do .login)

if ( ! $?ENVSETUP ) then

	# HP's are System Vish so they use LOGNAME instead of
	# USER in some places.  We want USER everywhere.

	if ( $?LOGNAME && ! $?USER ) then
		set user=$LOGNAME
	endif

	setenv EDITOR emacs

	setenv MANPAGER less
	setenv PAGER less

	# MIME/metamail stuff
	# setenv MM_NOASK 1
	# setenv MM_QUIET 1

	# end of environment variables
	setenv ENVSETUP 1
endif

# Interactive shell only

if ($?prompt) then
	set	filec
	set	prompt = "`hostname`[\!]>"
	set	notify
	set	history = 50
	alias	h	history 20

	# iGrid config facility
	source ~/configs/.iGrid_config
	source $IGRID_HOME/configs/.iGrid_env

	switch ($PLATFORM)
	    case "Darwin":
		# alias for finding my processes
		alias psu /bin/ps -ux
		alias me '/sw/bin/emacs \!*&'
		breaksw
	    case "Linux":
		# alias for finding my processes
		alias psu /bin/ps -ux
		breaksw
	    case "SunOS":
		# alias for finding my processes
		alias psu /bin/ps -fu $user
		alias psi '/usr/dt/bin/sdtprocess &'
		breaksw
	endsw
endif

# Useful both interactive and otherwise

#alias ds 'echo $DISPLAY'
alias sd 'setenv DISPLAY \!*":0.0"'
alias   ds "printenv DISPLAY"

alias setprompt 'set prompt="$HOST@$cwd>"'
alias cd 'cd \!* && setprompt'

alias   dir     "ls -la"
alias 	ldir 'ls -al \!* | grep ^d'
alias   dirm    "ls -la \!* | more"
alias   llm     "ls -l \!* | more"
alias	ll	"ls -l"
alias	lc	"ls -CFs"

alias 	h 'history'
alias 	ht 'history | tail'
alias 	hh 'history | head'

alias   pd      "pushd"
alias   pop     "popd"
alias   .       'echo $cwd'
alias   ..      'cd ../'
alias   print  'enscript -2rG -d $PRINTER'
alias   nol    "pr -n  \!* | enscript -l"

# useful both interactive and otherwise

#alias   make    /usr/ccs/bin/make

# Aliases for all shells

alias xm '/usr/bin/X11/xbiff -display hampshire:0.0 -g 50x50+0-0 &'
#alias 	cd 'cd \!*;~/bin/xt; set prompt = "$hostname@ $cwd > "'
#alias 	cd 'cd \!*;~/bin/xt'
#alias 	cd 'cd \!*; set prompt="$hostname>"'
#alias 	cd 'cd \!*; echo $cwd'
#alias 	cd 'cd \!*; set prompt="$hostname:`dirs`%"'
alias 	lo 'logout'
#alias 	send 'xhost + \!*; rlogin \!*'
alias 	rl 'rlogin \!* -8'
alias 	m more
alias 	cls 'clear'

# File space
alias 	mydisk 'df ~'
alias 	myhome 'du -s ~'
alias 	listhome 'du ~ | sort -nr | head -100'

alias 	sys 'ps -augx | grep $USER'
alias 	j  'jobs -l'

alias 	rm /bin/rm -i
alias 	mv /bin/mv -i
alias 	del  rm
alias 	ff 'find . -name \!* -print'
alias 	rgrep 'find .| xargs grep \!*' 

alias 	e 'emacs \!*&'
alias 	xe 'xemacs \!*&'
alias   ix '/research/sorcer/incax/browser/incax-browser.sh &'

# CVS
alias 	cvss 'cvs -d /research/sorcer/cvsroot \!*'
alias 	cvsstate 'cvs -f -n update -d -P'

# Network and security
alias nstat 'netstat -f inet'
alias nastat 'netstat -f inet -a'

# Stuff for emacs.

if ( $?EMACS ) then
  	setenv PAGER emacs-look
  	setenv MANPAGER emacs-look
  	setenv EDITOR emacsclient
  	setenv VISUAL emacsclient
  	alias more emacs-look
  	alias less emacs-look
endif

