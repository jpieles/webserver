/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieles.webserver.httpMirror;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * webserver, der den request "spiegelt"<br />
 * der text, der vom userAgent kommt wird<br />
 * im message body der response zurueckgeschickt
 * @author Julian Pieles
 */
public class HttpMirror {
    
    BufferedReader in; // fuer alles, was gelesen wird
    PrintWriter out; // fuer alles, was geschrieben oder gesendet wird
    
    /**
     * constructor for a HttpMirror
     * @param port (port unter dem die Verbindung hergestellt wird)
     */
    public HttpMirror(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());

                
                out.print("HTTP/1.1 200 OK\n");
                out.print("Content-Type: text/html\n");
                
                
                String requestLine = in.readLine();
                
                if(requestLine.startsWith("Host")) {
                    return;
                }else {
                    String[] requestURL = requestLine.split(" ");
                    String path = requestURL[1];
                    
                    HashMap<String, Object> params = new HashMap<>();
                    if(path.contains("?")) {
                        /*String[] pathStrings = path.split("?");
                        String queryString = pathStrings[1];
                        String[] paramStrings = queryString.split("&");
                        for(int i = 0; i < paramStrings.length; i++) {
                            String[] values = paramStrings[i].split("=");
                            params.put(values[0], values[1]);
                        }*/
                    }

                    String filename = path.replace("/", "");
                    
                    
                    try {
                        byte[] encoded = Files.readAllBytes(Paths.get("/home/jpi/webserver/sites/" + filename));
                    String file =  new String(encoded, StandardCharsets.UTF_8);
                    out.printf("Content-Length: %d\n", file.length());
                    out.print(file);
                    }catch(Exception ex) {
                        out.print("HTTP/1.1 200 OK\n");
                        out.print("Content-Type: text/html\n");
                        out.print("404");
                    }
                    
                    
                }
                
                
                out.flush();
                out.close();
                in.close();
                clientSocket.close();
            }
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(HttpMirror.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * main method
     * @param args 
     */
    public static void main(String[] args) {
        HttpMirror httpMirror = new HttpMirror(9914);
    }
    
   
    
}
