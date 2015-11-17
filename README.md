```
  _________       .__  __         .__
 /   _____/_  _  _|__|/  |_  ____ |  |__   _____ _____    ____
 \_____  \\ \/ \/ /  \   __\/ ___\|  |  \ /     \\__  \  /    \
 /        \\     /|  ||  | \  \___|   Y  \  Y Y  \/ __ \|   |  \
/_______  / \/\_/ |__||__|  \___  >___|  /__|_|  (____  /___|  /
        \/                      \/     \/      \/     \/     \/
                                  by ImmobilienScout24.de
```
[![Build Status](https://api.travis-ci.org/ImmobilienScout24/switchman.svg?branch=master)](https://travis-ci.org/ImmobilienScout24/switchman)
[![Coverage Status](https://coveralls.io/repos/ImmobilienScout24/switchman/badge.svg)](https://coveralls.io/r/ImmobilienScout24/switchman)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.is24.common/switchman/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.is24.common/switchman/)

A Microservice for storing A/B Test configurations and decisions as well as Feature Switch states.

HowTo Run the service
---------------------
To run this microservice, simply start a local mongodb and run the JAR as follows:
```bash
java -jar switchman-<VERSION>.jar -Dmongodb.url=mongodb://localhost:27017/switchman
```

Configuration
-------------

| Parameter| Description | Optional |
| ------------- |-------------|-------------|
| mongodb.url     | formatted according to [MongoDB Connection String Documentation](http://docs.mongodb.org/manual/reference/connection-string/)| No |
| graphite.enabled | Enable reporting of Spring Boot Metrics to Graphite | Yes |
| graphite.host | Graphite host | Yes |
| graphite.port | Graphite port | Yes |
| graphite.metrics.prefix | Metrics prefix for Switchman | Yes |
