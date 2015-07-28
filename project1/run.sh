#!/bin/bash

java -Xmx256m -cp `sh getclasspath.sh`:bin cs310.HashPrac $@
