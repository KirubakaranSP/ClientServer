package learn.test.clientserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static String outputDirectory;

    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        try (FileInputStream configFile = new FileInputStream(args[0])) {
            config.load(configFile);
        }

        outputDirectory = config.getProperty("output.directory");
        int port = Integer.parseInt(config.getProperty("server.port"));

        ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService executorService = Executors.newFixedThreadPool(10); 
        System.out.println("Server is listening on port " + port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.submit(() -> handleClient(clientSocket));
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            Map<String, String> receivedProperties = new HashMap<>();
            String line;
            String fileName =null;
            while (!(line = in.readLine()).equals("END_OF_FILE")) {
                String[] keyValue = line.split("=", 2);
                if (keyValue.length == 2) {
                	if(keyValue[0].equals("filename")) {
                		fileName = keyValue[1];
                		continue;
                	}
                    receivedProperties.put(keyValue[0], keyValue[1]);
                }
            }

            File outputFile = new File(outputDirectory+fileName);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                Properties properties = new Properties();
                properties.putAll(receivedProperties);
                properties.store(fos, "Filtered Properties File");
            }
            System.out.println("Processed and saved file: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
