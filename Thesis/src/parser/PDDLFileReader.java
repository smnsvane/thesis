package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The PDDLFileReader reads a PDDL formatted file, that is both domain - and problem files,
 * and saves a trimmed version of the PDDL definition with no new lines boundaries (all
 * content on one line and without any comments).
 */
public class PDDLFileReader
{
	private BufferedReader bf = null;
	
	private Pattern commentRemover = Pattern.compile("(.*?)(?:;;.*)?");

	// array to hold the file content
	private String content;
	/**
	 * Return the content (PDDL definition) parsed from the PDDL file (see
	 * {@link #initialize(String)}).
	 * @return the parsed content of the PDDL file (one line without any comments).
	 */
	public String getContent() { return content; }

	/**
	 * See {@link #getContent()}).
	 */
	@Override
	public String toString() { return getContent(); }
	
	/**
	 * Set name of (path to) the PDDL file to parse.
	 * If the file does not exist an error will be printed in the console
	 * and the program will be forced to terminate.
	 * @param filename
	 * @throws FileNotFoundException 
	 */
	public void initialize(String filename) throws FileNotFoundException
	{
		File file = new File(filename);
		bf = new BufferedReader(new FileReader(file));
	}
	
	/**
	 * Parse the file given in {@link #initialize(String)} (and fill the content buffer) to
	 * enable reading the PDDL definition (see {@link #getContent()}).
	 */
	public void fillBuffer()
	{
		// make instances
		StringBuilder sb = new StringBuilder();
		
		// read the file
		try
		{
			while (bf.ready())
			{
				String line = bf.readLine();
				
				// Remove potential comments on the line (- all data after first
				// instance of ";;").
				Matcher m = commentRemover.matcher(line);
				
				if (m.matches())
					// Add the content of each line (without comment) and add a
					// space as separator between lines, to be sure no strings
					// on two lines are concatenated into one.
					sb.append(m.group(1) + " ");
			}
		}
		catch (IOException e) { e.printStackTrace(); }
		
		content = sb.toString().toLowerCase();
	}
}
