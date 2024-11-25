
# ClientServer

## Project Overview

This project provides a mechanism for monitoring a directory for `.properties` files. When a new properties file appears, the client reads it, filters key-value pairs based on a configurable regular expression, and sends the filtered data to the server. The server then reconstructs the properties file from the received data and saves it to a directory.

## Components

### Client Program:
1. Monitors a specified directory for new `.properties` files.
2. Filters key-value pairs based on a regex pattern.
3. Sends the filtered data to a server.
4. Deletes the processed `.properties` file.

### Server Program:
1. Listens for incoming data from clients.
2. Reconstructs a filtered properties file based on the received key-value pairs.
3. Saves the reconstructed file to a specified directory.

## Requirements

- **Java 17**
- **Maven** (for building the project)
- A **terminal/command line environment**

## Configuration

Both the client and server programs use separate configuration files to specify necessary values:

### Client Config (`client_config.properties`):
- `monitor.directory`: Directory to monitor for new `.properties` files.
- `key.filter.regex`: Regular expression pattern to filter keys.
- `server.address`: Server address and port to send the data to (e.g., `localhost:8080`).

### Server Config (`server_config.properties`):
- `output.directory`: Directory where the server will save reconstructed properties files.
- `server.port`: Port the server should listen on for incoming client connections.

## Building and Running the Project

### Build the project using Maven:

1. Clone the repository or download the source code.
2. Navigate to the `ClientServer` directory.
3. Run the following Maven command to clean and build the project:
   ```bash
   mvn clean package

### Running the project in Terminal:

1. Navigate to the target directory
2. Edit the client_config.properties file with the appropriate settings for the monitored directory, key filtering pattern, and server address.
3. Edit the server_config.properties file with the appropriate settings for the server port and output directory.
4. Run the following commands in seperate terminals.
   ```bash
   java -cp properties-monitor-1.0-SNAPSHOT.jar learn.test.clientserver.Client /path/to/client_config.properties
   java -cp properties-monitor-1.0-SNAPSHOT.jar learn.test.clientserver.Server /path/to/server_config.properties
