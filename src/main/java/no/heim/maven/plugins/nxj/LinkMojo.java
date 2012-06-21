package no.heim.maven.plugins.nxj;

import java.io.IOException;

import js.tinyvm.TinyVMException;
import lejos.pc.tools.NXJLink;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Echos an object string to the output screen.
 * @goal link
 * @requiresProject true
 * @execute phase="compile"
 */
public class LinkMojo extends AbstractMojo {

	/**
     * Any Object to print out.
     * @parameter expression="${echo.message}" default-value="Hello World..."
     */
    private Object message;
    
    /**
     * Location of lejos-classes.
     * @parameter expression="${bootClassPath}"
     */
    private Object bootClassPath;
    
    /**
     * Name of main class
     * @parameter expression="${mainClass}"
     */
    private Object mainClass;
    
    /**
     * Name of output application
     * @parameter expression="${applicationName}
     */
    private Object applicationName;

    public void execute() throws MojoExecutionException, MojoFailureException {
    	try {
			new NXJLink().run(
					new String[] { "-bp", bootClassPath.toString(),
								   "-cp", "target/classes", "-wo", "BE", "-o", "target/" + applicationName.toString(),
								   mainClass.toString() });
		} catch (TinyVMException e) {
			getLog().error("Could not perform linking", e);
			throw new MojoFailureException(e, "Error", "");
		} catch (IOException e) {
			getLog().error("Could not perform linking", e);
			throw new MojoFailureException(e, "Error", "");
		}

        getLog().info(message.toString());
    }
}
