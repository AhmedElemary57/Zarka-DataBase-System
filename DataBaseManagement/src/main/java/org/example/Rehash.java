package org.example;

import org.apache.commons.codec.digest.MurmurHash3;
import org.example.RingStructure;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.Scanner;

public class Rehash {
    static ArrayList<ArrayList<String>> ranges;
    public static int  findIndex(List<String> list, String k) {
        // Lower and upper bounds
        int start = 0;
        int end = list.size() - 1;
        // Traverse the search space
        while (start <= end) {
            int mid = (start + end) / 2;
            // If K is found
            if (list.get(mid).equals(k))
                return mid;
            else if (list.get(mid).compareTo(k) < 0)
                start = mid + 1;
            else
                end = mid - 1;
        }

        // Return insert position
        return end;
    }

    public static void deleteOldSegment(ArrayList<ArrayList<String>> ranges) {

    }
    static boolean isInRange(String key, ArrayList<ArrayList<String>> ranges) {
        for (int i = 0; i < ranges.size(); i++) {
            String start = ranges.get(i).get(0);
            String end = ranges.get(i).get(1);
            if (key.compareTo(start) >= 0 && key.compareTo(end) <= 0) {
                return true;
            }
        }
        return false;
    }
    /**
     * @param ranges : List of pairs represents the ranges (start, end).
     * @param newNodePath : The path of the ssTable file for the new node.
     * @param segmentPath : The path of the old ssTable file to transfer the data from it.
     *
     */
    public static void createNewSegment(ArrayList<ArrayList<String>> ranges, String newNodePath, String newSegmentName, String segmentPath) throws IOException {
        System.out.println("Creating Segment"+newSegmentName);
        File oldFile = new File(segmentPath);

        Scanner myReader = new Scanner(oldFile);

        RedBlackTree<String> redBlackTree=new RedBlackTree();
        List<String> recordsRemain=new ArrayList<>();

        while (myReader.hasNextLine()){
            String line = myReader.nextLine();
            String[] keyValue = line.split(",");
            String hashedValue = String.valueOf(Long.valueOf(MurmurHash3.hash32x86(keyValue[0].getBytes())));
            if (isInRange( hashedValue, ranges)) {
                redBlackTree.insert(keyValue[0],keyValue[1]);
            }
            else {
                recordsRemain.add(line);
            }
        }
        myReader.close();
        oldFile.delete();

        FileWriter oldSegmentWriter = new FileWriter(oldFile);
        for (String record:recordsRemain) {
            oldSegmentWriter.write(record+"\n");
        }
        oldSegmentWriter.close();
        if (redBlackTree.size() == 0) {
            return;
        }
        File newFile = new File(newNodePath);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }

        FileWriter newFileWriter = new FileWriter(newFile+"/"+newSegmentName+".txt", true);
        List<Node<String>> keyValue=redBlackTree.inOrderTraversal();
        for (int i = 0; i < keyValue.size(); i++) {
            System.out.println(keyValue.get(i).getKey() + "," + keyValue.get(i).getValue());
            newFileWriter.write(keyValue.get(i).getKey() + "," + keyValue.get(i).getValue() + "\n");
        }
        newFileWriter.close();
    }

    public static void createNewPartition(String newNodePath, String oldPath) throws IOException {
        File folder = new File(oldPath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            ArrayList<ArrayList<String>> ranges=RingStructure.ranges();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    listOfFiles[i].getName();
                    createNewSegment(ranges,newNodePath,Server.currentPortNumber+String.valueOf(i+1),listOfFiles[i].getPath());
                }
            }
        }
    }
    public static void main(String[] args) {
        ranges = new ArrayList<>();
        ranges.add(new ArrayList<>(Arrays.asList("key19", "key24")));
        String oldNodePath = "/home/elemary/Projects/DataBaseManagement/Node_Number"+ 5003 +"/ReplicaOf"+5003+"/Data/";
        String newNodePath = "/home/elemary/Projects/DataBaseManagement/Node_Number"+ 5017 +"/ReplicaOf"+5017+"/Data/";
        try {
            createNewPartition(newNodePath,oldNodePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
