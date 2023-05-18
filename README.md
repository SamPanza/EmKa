[![Build](https://github.com/SamPanza/EmKa/actions/workflows/build.yml/badge.svg)](https://github.com/SamPanza/EmKa/actions/workflows/build.yml)
![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)
![Lines-of-Code](https://img.shields.io/tokei/lines/github/SamPanza/EmKa?label=Lines-of-Code)
[![Hits-of-Code](https://img.shields.io/badge/dynamic/json?label=Hits-of-Code&query=$.count&url=https://hitsofcode.com/github/SamPanza/EmKa/json?branch=main)](https://hitsofcode.com/github/SamPanza/EmKa/view?branch=main)

**EmKa** is a [JUnit 5](https://junit.org/junit5/) [extension](https://junit.org/junit5/docs/current/user-guide/#extensions)
that automatically starts / stops a single-broker [Kafka](https://kafka.apache.org/) server on arbitrarily available ports
before / after each test method, and provides a few ways to communicate with the server by resolving test method parameters
and by injecting test instance fields supported by the extension. Also, there are more features to come.
