package org.data_model.data_access;

import java.io.*;

public class SerializationOperation {
    private static final String FILE_NAME = "tasks_management.ser";

    public static void serialize(Object obj) {
        try (FileOutputStream fileOut = new FileOutputStream(FILE_NAME);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(obj);
            System.out.println("Datele au fost salvate cu succes in " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object deserialize() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return null;

        try (FileInputStream fileIn = new FileInputStream(FILE_NAME);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}