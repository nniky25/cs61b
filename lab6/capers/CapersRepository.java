package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = join(CWD, ".capers"); // TODO Hint: look at the `join`
                                            //      function in Utils
    private static File dogsFile = null;
    private static File storyFile = null;

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() throws IOException {
        // TODO
        /* create .capers/dogs/ and .capers/story.txt if they are not exist. */
        if (!CAPERS_FOLDER.exists()) {
            if (!CAPERS_FOLDER.mkdir()) throw new IOException("fail to mkdir" + CAPERS_FOLDER.getAbsolutePath());

            //System.out.println("The first time to mkdir '.capers/' directory");
        }

        String dogs = "dogs/";
        String story = "story.txt";

        dogsFile = join(CAPERS_FOLDER, dogs);
        storyFile = join(CAPERS_FOLDER, story);

        if (!dogsFile.exists() && !storyFile.exists()){
            if (!dogsFile.mkdir()) throw new IOException("failed to mkdir" + dogsFile.getAbsolutePath());
            if (!storyFile.createNewFile()) throw new IOException("failed to creat" + storyFile.getAbsolutePath());

            //System.out.print(System.getProperty("The first time to create 'dogs/' and 'story.txt'"));
        }
    }

    public static File getDogsFile() {
        return dogsFile;
    }

    public static File getStoryFile() {
        return storyFile;
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // TODO
        writeContents(getStoryFile(), text);

        try {
            String content = readContentsAsString(getStoryFile());
            System.out.println(content);
        } catch (Exception e) {
            System.out.println("读取失败:");
        }
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        // TODO
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        // TODO
    }
}
