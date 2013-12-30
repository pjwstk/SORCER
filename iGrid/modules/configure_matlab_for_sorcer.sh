#!/bin/bash

# Which matlab executable to look for
MATLAB=matlab
echo Looking for $MATLAB

# Determine $matlabroot based on MATLAB found in path
MATLAB_BIN=$(readlink -f $(which $MATLAB))
MATLAB_BIN=$(dirname $MATLAB_BIN)
MATLAB_ROOT=$(readlink -f "$MATLAB_BIN"/..)
echo \$matlabroot is $MATLAB_ROOT

CLASSPATH_FILE="$MATLAB_ROOT"/toolbox/local/classpath.txt
echo Default CLASSPATH_FILE is $CLASSPATH_FILE


echo Creating local classpath.txt file in $PWD
echo ${IGRID_HOME}/classes > classpath.txt
echo ${IGRID_HOME}/lib/river/lib/jsk-platform.jar >> classpath.txt
echo ${IGRID_HOME}/lib/river/lib/jsk-lib.jar >> classpath.txt
echo ${IGRID_HOME}/lib/rio/rio.jar >> classpath.txt
cat $CLASSPATH_FILE >> classpath.txt

echo Creating local startup.m file in $PWD
cat <<EOF > startup.m
% Matlab startup script

% Generic Matlab Utility Routines
addpath( fullfile( pwd, 'modules', 'engineering', 'matlabUtil' ) );

% ModelClientGui
addpath( fullfile( pwd, 'modules', 'sorcer', 'src', 'sorcer', 'client', 'ModelClientGui' ) );

% QsCsd pre/post processing utilities
addpath( fullfile( pwd, 'data', 'matlab_example', 'qscsd' ) );
addpath( fullfile( pwd, 'data', 'matlab_example', 'qscsd', 'pre' ) );
addpath( fullfile( pwd, 'data', 'matlab_example', 'qscsd', 'post' ) );
addpath( fullfile( pwd, 'data', 'matlab_example', 'qscsd', 'bin' ) );

% MSTCGA DataBase
addpath( fullfile( pwd, 'modules', 'engineering', 'optimization', 'matlab', 'library', 'src', 'mstcga' ) );

% MSTCGA DataBase Gui
addpath( fullfile( pwd, 'modules', 'engineering', 'optimization', 'matlab', 'library', 'src', 'ExploreDatabaseGui' ) );

EOF


cat <<EOF

NOTICE:

Matlab has been configured for SORCER. To make use of sorcer java classes, run matlab from: 

$PWD 

or the files classpath.txt and startup.m to the directory where you would like to start matlab.
EOF
