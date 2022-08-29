package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class Admin {

    public static void main(String[] args) throws IOException {

        int numberOfNodes=5;
        Thread buildServer =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                     new Server().main(new String[]{"1", "5", "100"});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        );
    }
}
