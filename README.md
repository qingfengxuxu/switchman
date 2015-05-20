# A/B Testing and Feature Switch Service
```
  _________       .__  __         .__                           
 /   _____/_  _  _|__|/  |_  ____ |  |__   _____ _____    ____  
 \_____  \\ \/ \/ /  \   __\/ ___\|  |  \ /     \\__  \  /    \ 
 /        \\     /|  ||  | \  \___|   Y  \  Y Y  \/ __ \|   |  \
/_______  / \/\_/ |__||__|  \___  >___|  /__|_|  (____  /___|  /
        \/                      \/     \/      \/     \/     \/ 
                                  by ImmobilienScout24.de 
                                  
A Microservice for storing A/B Test configurations and decisions as well as Feature Switch states.                                
```
[![Build Status](https://api.travis-ci.org/ImmobilienScout24/switchman.svg?branch=master)](https://travis-ci.org/ImmobilienScout24/switchman)
[![Codacy Badge](https://www.codacy.com/project/badge/e289b7e7172c49d09b83876e2acf7f7a)](https://www.codacy.com/app/jan_1691/switchman)

## Configuration

* mongodb.url

Optional parameters:
* hadoop.enabled
* hadoop.rest.url
* graphite.enabled
* graphite.host
* graphite.port
* graphite.metrics.prefix
