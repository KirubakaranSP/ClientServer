package learn.test.clientserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class Client {
    private static String directoryPath;
    private static String keyPattern;
    private static String serverAddress;
    private static String serverPort;
    
    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        try (FileInputStream configFile = new FileInputStream(args[0])) {
            config.load(configFile);
        }

        directoryPath = config.getProperty("directory.path");
        keyPattern = config.getProperty("key.filter.regex");
        serverAddress = config.getProperty("server.address");
        serverPort = config.getProperty("server.port");

        Pattern filterPattern = Pattern.compile(keyPattern);

        Socket socket = new Socket(serverAddress, Integer.parseInt(serverPort));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Monitor the directory for new property files
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(directoryPath);
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filePath = ev.context();
                if (filePath.toString().endsWith(".properties")) {
                	File  resolvedFile = new File(directoryPath+File.separator+filePath.toString());
                    Map<String, String> filteredProperties = processFile(resolvedFile, filterPattern);
                    out.println("filename="+filePath.getFileName());
                    for (Map.Entry<String, String> entry : filteredProperties.entrySet()) {
                        out.println(entry.getKey() + "=" + entry.getValue());
                    }
                    out.println("END_OF_FILE");
                    resolvedFile.delete();
                }
            }
            key.reset();
        }
    }

    private static Map<String, String> processFile(File file, Pattern filterPattern) throws IOException {
        Map<String, String> filteredProperties = new HashMap<>();
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        }

        for (String key : properties.stringPropertyNames()) {
            if (filterPattern.matcher(key).matches()) {
                filteredProperties.put(key, properties.getProperty(key));
            }
        }
        return filteredProperties;
    }
}

