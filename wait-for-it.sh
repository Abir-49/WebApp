#!/bin/sh
# wait-for-it.sh

set -e

host="$1"
shift # Shifts "postgres" out

until PGPASSWORD=$POSTGRES_PASSWORD psql -h "$host" -p "5432" -U "admin" -d "$POSTGRES_DB" -c '\q'; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

>&2 echo "Postgres is up - executing command"

shift # Shifts "--" out

exec "$@" # Executes the remaining arguments as the command
