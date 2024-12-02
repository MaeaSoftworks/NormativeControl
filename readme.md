# normative control core

Document verification microservice.

This repository is a part of **normative control** service alongside other microservices:

- [MaeaSoftworks/normative-control-api](https://github.com/MaeaSoftworks/normative-control-api),
- [MaeaSoftworks/normative-control-frontend](https://github.com/MaeaSoftworks/normative-control-frontend).

However, it can be used as CLI utility. For more info, see [CLI](#CLI).

## Creating your own verification implementation
There is two ways to do this:
1. fork/clone this repository and add your implementation in [implementation](implementation) subproject;
2. import normative-control-core subproject as library into your project
(there is actually no uploaded versions on Maven Central or something so most likely it will be local jar).

In any case, you can use [UrFU implementation](implementation/src/main/kotlin/normativecontrol/implementation/urfu) as 
an example.

## Building
To build any subproject you need just one command:
```shell 
gradle :$subproject_name:build
```

## CLI

Launcher subproject has CLI. Detailed list of commands you can find below.

| Command   | Description               |
|-----------|---------------------------|
| `verify`  | Verify document and exit. |
| `client`  | Start client server.      |

### `verify` arguments

| Argument      | Description                                                                                                                  |
|---------------|------------------------------------------------------------------------------------------------------------------------------|
| `<source>`    | File that need to be verified.                                                                                               |
| `<result>`    | Path to result file. If not specified, it will be saved in same folder as source file.                                       |
| `<render>`    | Path to rendered document in HTML file. File will be opened after verification. If not specified, file will not be rendered. |
| `-b`          | Enable blocking mode (instead of multithreading).                                                                            |

### `client` arguments

| Argument | Description                                       |
|----------|---------------------------------------------------|
| `-b`     | Enable blocking mode (instead of multithreading). |

---
**NOTE**

Client mode requires a bunch of env variables:
- `nc_amqp_url`
- `nc_amqp_queue_name`
- `nc_s3_access_key_id`
- `nc_s3_secret_key_id`
- `nc_s3_bucket`
- `nc_s3_endpoint`
- `nc_s3_region`
- `nc_db_url`
- `nc_db_user`
- `nc_db_password`

---

# Authors

Developed by [Andrei Stremousov](https://github.com/prmncr) & [Maksim Diachenko](https://github.com/EliteHacker228) under [Apache 2.0 license](LICENSE).