# Python REST Api

This is a sample Python REST Api.
This Api is built as simple as possible to showcase the following capabilities within docker:

## CLI Params

| Variable       | Description                                      | Default              |
|----------------|--------------------------------------------------|----------------------|
| --file         | Path to the config file                          | exampleSettings.yaml |
| --experimental | Adds a `/magic` REST endpoint to view the config | False                |


## Configuration File:

| Variable      | Description                          | Default |
|---------------|--------------------------------------|---------|
| server.host   | The IP address to host the server on | 0.0.0.0 |
| server.port   | The port to server the REST api on   | 8080    |
| server.cowsay | What does the cow say? `/cowsay`     | moo     |
| redis.enabled | Whether redis is enabled or not      | False   |
| redis.host    | Host to connect to redis             | ""      |
| redis.host    | Port to connect to redis             | ""      |


## Environment Variables

| Variable              | Description             | Default |
|-----------------------|-------------------------|---------|
| API_USER_FIRST_NAME   | The user's first name   | "Admin" |
| API_USER_LAST_NAME    | The user's last name    | "Admin" |
| API_USER_FAVORITE_PET | The user's favorite pet | "Dogs"  |


## Endpoints:

- `/`
- `/user/`
- `/cowsay`
- `/config`
- `/magic`

## This App is build to show off the following Docker features:

- CLI Parameters
  - The ability to pass in params to change behaviour
- ENV Variables
  - The ability to set environment variables on a container
- Volume Mounts
  - The ability to mount configuration files (if nessecary. The app sets default values if not present)
- Port Forwards
  - The REST Api is served on a port. Allows showing docker's port-forwards and accessing the REST Api through a browser
- Networking
  - Communication with other containers
  - The app allows being configured to talk to REDIS to show how two containers can talk together.