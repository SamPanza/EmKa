[![build](https://github.com/SamPanza/EmKa/actions/workflows/build.yml/badge.svg)](https://github.com/SamPanza/EmKa/actions/workflows/build.yml)
![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)
[![Hits-of-Code](https://hitsofcode.com/github/SamPanza/EmKa?branch=main)](https://hitsofcode.com/github/SamPanza/EmKa/view?branch=main)

**EmKa** is a [JUnit 5](https://junit.org/junit5/) [extension](https://junit.org/junit5/docs/current/user-guide/#extensions)
that automatically starts / stops a single-broker [Kafka](https://kafka.apache.org/) server on arbitrarily available ports
before / after each test method, and provides a few ways to communicate with the server by resolving test method parameters
and by injecting test instance fields supported by the extension. Also, there are more features to come.
