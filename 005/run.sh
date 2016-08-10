#!/bin/bash
GOPATH=$GOPATH:$PWD go run src/buttons/depression.go ${1:-input}
