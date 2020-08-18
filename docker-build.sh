#!/bin/bash
 
COLOR_RED="31"
COLOR_GREEN="32"
 
print_error() {
  echo -e "\e[${COLOR_RED}mErro:\e[0m $1"
}
 
if [ -z "$1" ]; then
  print_error "Necessário informar a versão"
  exit
fi

docker build \
  -t fagnerlima/sample-specification-api:$1 \
  -t fagnerlima/sample-specification-api:latest \
  .
