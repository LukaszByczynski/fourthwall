#!/bin/bash
set -e

# Update passwords for existing users
psql -v ON_ERROR_STOP=1 --username fourthwall -d fourthwalldb <<-EOSQL
    ALTER USER cinemamanager WITH PASSWORD '${CINEMAMANAGER_USER_PASSWORD}';
    ALTER USER movieslisting WITH PASSWORD '${MOVIESLISTING_USER_PASSWORD}';
    ALTER USER eventbus WITH PASSWORD '${EVENTBUS_USER_PASSWORD}';
EOSQL