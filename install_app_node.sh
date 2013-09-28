#!/bin/bash

ansible all -m ping

ansible app-servers -m shell -a 'apt-get update' --sudo


