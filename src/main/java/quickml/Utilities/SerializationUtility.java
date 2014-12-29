package quickml.Utilities;

import quickml.supervised.classifier.randomForest.RandomForest;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by alexanderhawk on 12/17/14.
 */
public class SerializationUtility<E> {

    public E loadObjectFromGZIPFile(final String modelFile) {
        try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(modelFile)));) {
            return (E) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error reading predictive model", e);
        }
    }



    public void writeModelToGZIPFile(final String modelFileName, E object) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(modelFileName)));) {
            oos.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException("Error reading predictive model", e);
        }
    }
}
