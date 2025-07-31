package capers;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import static capers.Utils.*;

/** Represents a dog that can be serialized.
 * @author TODO
*/
/* must add implements Serializable if you want to use readObject() method. */
public class Dog implements Serializable{ // TODO

    /** Folder that dogs live in. */
    static final File DOG_FOLDER = join(".capers/dogs"); // TODO (hint: look at the `join`
                                         //      function in Utils)

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) throws IOException {
        // TODO (hint: look at the Utils file)
        File readDog = join(DOG_FOLDER, name);
        if (!readDog.exists()) throw new IOException("The dog doesn't exist" + readDog.getAbsolutePath());

        Dog d = readObject(readDog, Dog.class);
        return d;
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(toString());
        System.out.println("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() throws IOException {
        // TODO (hint: don't forget dog names are unique)
        File current = join(DOG_FOLDER, name);
        if (current.exists()) throw new IOException("Dog " + name + " already exists at " + current.getAbsolutePath());

        if (!current.createNewFile()) throw new IOException("failed to creat dog file" + current.getAbsolutePath());

        writeObject(current, (Serializable) this);
    }

    public void saveDog(int oneyear) throws IOException {
        // TODO (hint: don't forget dog names are unique)
        File current = join(DOG_FOLDER, name);
        if (!current.exists()) throw new IOException("Dog " + name + " doesn't exist at " + current.getAbsolutePath());

        rewriteObject(current, (Serializable) this);
    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}
