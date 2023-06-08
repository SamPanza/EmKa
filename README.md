# EmKa: embedded [Kafka](https://kafka.apache.org/) server for your [JUnit 5](https://junit.org/junit5/) tests

[![Build](https://img.shields.io/github/actions/workflow/status/SamPanza/EmKa/build.yml?style=for-the-badge)](https://github.com/SamPanza/EmKa/actions/workflows/build.yml)
[![Coverage](https://img.shields.io/codecov/c/github/SamPanza/EmKa?style=for-the-badge)](https://app.codecov.io/gh/SamPanza/EmKa)
![Lines-of-Code](https://img.shields.io/tokei/lines/github/SamPanza/EmKa?label=Lines-of-Code&style=for-the-badge)
[![Hits-of-Code](https://img.shields.io/badge/dynamic/json?style=for-the-badge&label=Hits-of-Code&query=$.count&url=https://hitsofcode.com/github/SamPanza/EmKa/json?branch=main)](https://hitsofcode.com/github/SamPanza/EmKa/view?branch=main)

**EmKa** is an [extension](https://junit.org/junit5/docs/current/user-guide/#extensions) that automatically
starts a Kafka server on arbitrarily available ports before each test method and stops the server after.
To talk to the server **EmKa** injects supported test method parameters and test instance fields and that's it.
