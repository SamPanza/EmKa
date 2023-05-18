[![Build](https://img.shields.io/github/actions/workflow/status/SamPanza/EmKa/build.yml?style=for-the-badge)](https://github.com/SamPanza/EmKa/actions/workflows/build.yml)
![Coverage](https://img.shields.io/endpoint?style=for-the-badge&url=https://raw.githubusercontent.com/SamPanza/EmKa/main/.github/badges/coverage.json)
![Branches](https://img.shields.io/endpoint?style=for-the-badge&url=https://raw.githubusercontent.com/SamPanza/EmKa/main/.github/badges/branches.json)
![Lines-of-Code](https://img.shields.io/tokei/lines/github/SamPanza/EmKa?style=for-the-badge)
[![Hits-of-Code](https://img.shields.io/badge/dynamic/json?style=for-the-badge&label=Hits-of-Code&query=$.count&url=https://hitsofcode.com/github/SamPanza/EmKa/json?branch=main)](https://hitsofcode.com/github/SamPanza/EmKa/view?branch=main)

**EmKa** is a [JUnit 5](https://junit.org/junit5/) [extension](https://junit.org/junit5/docs/current/user-guide/#extensions)
that automatically starts / stops a single-broker [Kafka](https://kafka.apache.org/) server on arbitrarily available ports
before / after each test method, and provides a few ways to communicate with the server by resolving test method parameters
and by injecting test instance fields supported by the extension. Also, there are more features to come.
