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
import java.io.DataInputStream;
import java.io.DataOutputStream;

import Helpers.Reader;

/**
 *
 * @author dzmitry
 */
public class ServerThread extends Thread {
    
    private Socket socket;
 
    private DataInputStream dis;
    
    private DataOutputStream dos;
    
    public ServerThread(Socket socket) {
        this.socket = socket;
    }
 
    public void run() {
        try {

            this.dis = new DataInputStream(this.socket.getInputStream());
            this.dos = new DataOutputStream(this.socket.getOutputStream());
            
            Reader helperReader = new Reader();
        
            helperReader.read();
            
            String request, command, id, title, content;
 
            do {                
                request = this.dis.readUTF();
                                
                JSONObject document = new JSONObject();
                
                try {
                    JSONParser parser = new JSONParser();
                    document = (JSONObject) parser.parse(request);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                
                command = (String) document.get("command");
                String result = new String();
                
                switch (Integer.parseInt(command)) {
                    case 1 :
                        id = (String) document.get("id");
                        title = (String) document.get("title");
                        content = (String) document.get("content");
                        helperReader.add(Integer.parseInt(id), title, content);
                        result = helperReader.displayAll();
                        break;
                    case 2 :
                        String search = (String) document.get("search");
                        result = helperReader.search(search);
                        break;
                    case 3 :
                        id = (String) document.get("id");
                        result = helperReader.delete(Integer.parseInt(id));
                        break;
                    case 4 :
                        result = helperReader.displayAll();
                        break;
                    case 5 :
                        helperReader.save();
                        break;
                }
              
                this.dos.writeUTF(result);
 
            } while (!command.equals("5"));
 
            this.socket.close();
            this.dos.close();
            this.dis.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
