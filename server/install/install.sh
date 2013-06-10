#!/bin/sh

if [ "$1" = "" ] ; then
    echo "Alfresco path is not set. Use install.bat alfresco_home"
else
    rm $1/amps/*.amp
    cp amps/alfresco/*.amp $1/amps
    rm $1/amps_share/*.amp
    cp amps/share/*.amp $1/amps_share
    $1/bin/apply_amps.sh
fi
