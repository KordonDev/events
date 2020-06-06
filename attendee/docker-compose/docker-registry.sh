#!/bin/bash

# docker run -d -p 5000:5000 --restart=always --name registry registry:2
docker run -d -p 5000:5000 --name registry registry:2

# docker container stop registry