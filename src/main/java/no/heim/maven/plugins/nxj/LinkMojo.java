/*
 * Copyright 2012 Felix Müller <felix.mueller.berlin@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.heim.maven.plugins.nxj;

import java.io.File;
import java.io.IOException;

import js.tinyvm.TinyVMException;
import lejos.pc.tools.NXJLink;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Links all compiled classes to an executable for the NXJ platform.
 * 
 * @goal link
 */
public class LinkMojo extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     * @parameter
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Location of lejos-classes.
     *
     * @parameter
     * @required
     */
    private String bootClasspath;

    /**
     * Name of main class
     *
     * @parameter
     * @required
     */
    private String mainClass;

    /**
     * Name of output application
     *
     * @parameter
     * @required
     */
    private String applicationName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start linking...");
        final String classpath = createClasspath();
        getLog().debug("Link with classpath: " + classpath);

        try {
            final int linkResult = new NXJLink().run(new String[] {
                    "-bp", bootClasspath,
                    "-cp", classpath,
                    "-wo", "LE",
                    "-o", "target" + File.separator + applicationName + ".nxj",
                    mainClass });

            if (linkResult == 0) {
                getLog().info("Linked successfully " + applicationName);
            } else {
                getLog().error("Error occurred in nxj linker");
            }

        } catch (TinyVMException e) {
            getLog().error("Could not perform linking", e);
            throw new MojoFailureException(e, "Error", "");
        } catch (IOException e) {
            getLog().error("Could not perform linking", e);
            throw new MojoFailureException(e, "Error", "");
        }
    }

    @SuppressWarnings("unchecked")
    private String createClasspath() {
        final String localRepository = System.getProperty("user.home")
                + File.separator + ".m2" + File.separator + "repository";
        final ClasspathBuilder cpBuilder = new ClasspathBuilder(localRepository, project.getDependencies());
        final String classpath = "target" + File.separator + "classes" + File.pathSeparator + cpBuilder.build();
        return classpath;
    }
}
