#!/bin/bash

set -e

if [[ -n "${BUSPLAN_DEBUG}" ]]; then
    set -x
fi

DIR="$(dirname "$0")"

source ${DIR}/util/realpath.sh

realpath $DIR

DOCKER_COMPOSE_FILE=$(realpath ${DIR}/../docker-compose.analytics.yml)

function usage() {
    echo -n \
"Usage: $(basename "$0")

Run a Juptyer Notebook in a docker container for analysis.

Options:
        build: build the docker containersn
        run: Run a jupyter notebook instance
"
}

if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    if [ "${1:-}" = "--help" ]; then
        usage
    else
        case "${1}" in
            build)
                docker-compose -f ${DOCKER_COMPOSE_FILE} build
                ;;
            run)
                docker-compose -f ${DOCKER_COMPOSE_FILE} up
                ;;
            *)
                echo "Invalid command: ${1}";
                usage;
                exit 1
                ;;
        esac
    fi
fi
