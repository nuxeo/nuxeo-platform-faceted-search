#!/bin/bash -x

HERE=$(cd $(dirname $0); pwd -P)

# Retrieve Nuxeo Distribution, nuxeo-platform-faceted-search jars and selenium-server.jar
(cd .. && mvn clean dependency:copy) || exit 1

# Start JBoss
cd ../target
unzip nuxeo-distribution-jboss-*.zip || exit 1
mv nuxeo-dm-*-jboss jboss || exit 1
mv nuxeo-platform-faceted-search* jboss/server/default/deploy/nuxeo.ear/system/ || exit 1
chmod +x jboss/bin/nuxeoctl || exit 1
jboss/bin/nuxeoctl start || exit 1

# Run selenium tests
cd $HERE
./run.sh
ret1=$?

# Stop JBoss
(cd ../target && jboss/bin/nuxeoctl stop) || exit 1

# Exit if some tests failed
[ $ret1 -eq 0 ] || exit 9
