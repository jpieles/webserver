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
                String zeile;
                
                out.print("HTTP/1.1 200 OK\n");
                out.print("Content-Type: text/html\n");
                out.print("\n");
                out.print("<h1 style=\"background-color: blue\">HTTP</h1>");
                
                String requestLine = in.readLine();
                
                if(requestLine.startsWith("Host")) {
                    return;
                }else {
                    String[] requestURL = requestLine.split(" ");
                    String path = requestURL[1];
                    String filename = path.replace("/", "");
                    System.out.println(path);
                    out.print("<h2>" + filename + "</h2>");
                    byte[] encoded = Files.readAllBytes(Paths.get("/home/jpi/sites/" + filename));
                    String file =  new String(encoded, StandardCharsets.UTF_8);
                    out.print(file);
                }
                
                while(true) {
                    zeile = in.readLine();
                    out.println(zeile);
                    out.println("<br />");
                    
                    if(zeile.length() == 0) break;
                    
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
