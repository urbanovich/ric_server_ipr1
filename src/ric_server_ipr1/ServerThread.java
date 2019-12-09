/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ric_server_ipr1;

import java.io.*;
import java.net.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import Helpers.Reader;

/**
 *
 * @author dzmitry
 */
public class ServerThread extends Thread {
    private Socket socket;
 
    public ServerThread(Socket socket) {
        this.socket = socket;
    }
 
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
            Reader helperReader = new Reader();
        
            helperReader.read();
            
            String text;
 
            do {
                text = reader.readLine();
                
                String request = new StringBuilder(text).toString();
                
                JSONObject document = new JSONObject();
                
                try {
                    JSONParser parser = new JSONParser();
                    document = (JSONObject) parser.parse(request);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                
                String command = (String) document.get("command");
                
                switch (Integer.parseInt(command)) {
                    case 1 :
                        
                        String id = (String) document.get("id");
                        String title = (String) document.get("title");
                        String content = (String) document.get("content");
                        helperReader.add(Integer.parseInt(id), title, content);
                        
                        break;
                    case 2 :
                        
                        break;
                    case 3 :
                        
                        break;
                    case 4 :
                        helperReader.save();
                        break;
                }

                String list = helperReader.displayAll();
                
                writer.println(list);
 
            } while (!text.equals("4"));
 
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
