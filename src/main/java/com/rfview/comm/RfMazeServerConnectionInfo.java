package com.rfview.comm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.rfview.management.MazeserverManagement;

public class RfMazeServerConnectionInfo {

    protected final MazeserverManagement mgmt = MazeserverManagement.getInstance();
    private Map<String, MazeServer> servers = new ConcurrentHashMap<String, MazeServer>();
    private static RfMazeServerConnectionInfo instance = new RfMazeServerConnectionInfo();

    public static RfMazeServerConnectionInfo getInstance() {
        return instance;
    }

    public MazeServer getServer(String name) throws IOException {
        MazeServer server = servers.get(name);
        FileReader reader = null;
        if (server == null) {
            try {
                Properties props = new Properties();
                reader = new FileReader(new File(mgmt.getConfigureDir()
                        + File.separator + name + ".cfg"));
                props.load(reader);

                String ip = "127.0.0.1";
                int port = Integer.parseInt(props.getProperty("server_socket_port", "29010"));
                server = new MazeServer(name, ip, port);
                servers.put(name, server);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        return server;
    }
}
