#!/usr/bin/env bash

# mounts the script directory
dir="$(cd -P -- "$(dirname -- "$0")" && pwd -P)"
cd "${dir}"

# asks for Client variables
read -p "Enter the port you want to use: " port
read -p "Enter the server hostname/IP address: " server
read -p "Enter your RMI Hostname, if needed (otherwise, you can leave it clear): " rmihostname

# selects the correct executable based on CPU Architecture
arch=$(uname -m)
if [[ $arch == x86_64* ]]; then
    java -jar ClientSelector-AM34-x64.jar $port $server $rmihostname
elif [[ $arch == i*86 ]]; then
    echo "Your architecture is unsupported. You may have to use the CLI interface only."
elif [[ $arch == arm* ]] || [[ $arch == aarch64 ]]; then
    java -jar ClientSelector-AM34-arm64.jar $port $server $rmihostname
fi

$SHELL