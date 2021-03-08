/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieles.webserver.httpMirror;

import java.net.*;
import java.io.*;
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
                while(true) {
                    zeile = in.readLine();
                    System.out.println(zeile);
                    out.println(zeile);
                    if(zeile.length() == 0) break;
                    
                }
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
        HttpMirror httpMirror = new HttpMirror(9992);
    }
    
}
