#!/bin/bash

MCVER=1.4.5
FMLVER=381
MCCLIENTDIR="$HOME/minecraft-$MCVER-fml-$FMLVER/mods"
MCSERVERDIR="$HOME/mc-server-fml-$FMLVER/mods"
JARDIR="$HOME/projects/parachute-src-1.4.x"
JAR="parachute-$MCVER-fml-$FMLVER.jar"

cp -f "$JARDIR/$JAR" $MCCLIENTDIR
cp -f "$JARDIR/$JAR" $MCSERVERDIR
