# java-explore-with-me

This is my final project during my yandex practicum learning sessions.
Main functionality is to manage, share and publish events.
It's based on REST API principles and uses SPRING BOOT backed with POSTGRESQL to provide functionality.

## Main features are:
- events
    - categories
    - locations
        - location types
- compilations
- requests
- users
    - admin API
    - user API
- statistics

## How to:
App is container-ready. Download zip, extract files and run file docker-compose.yml with:

```
sudo docker compose up
```

in any docker environment.

Main service will use :9090 port and :8080 for statistics. Use /postman folder for examples API.
