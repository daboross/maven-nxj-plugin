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
package no.heim.maven.plugins.nxj.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Uploads a given NXJ executable file to a NXT robot.
 *
 * @goal upload
 * @author Felix Müller
 */
public class UploadMojo extends AbstractMojo {

    /**
     * Path of nxjupload script
     *
     * @parameter
     * @required
     */
    private String nxjupload;

    /**
     * The nxj executable file
     *
     * @parameter
     * @required
     */
    private String executable;

    /**
     * If true the uploaded executable starts immediately
     *
     * @parameter default-value=false
     */
    private boolean runImmediately;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Start uploading to nxt...");

        try {
            // best solution because the nxjupload script also handles jre 32 bit switch
            final Process uploadProcess = new ProcessBuilder(createUploadCommandLine()).start();
            pipeOutputToMavenLog(uploadProcess);

            final int uploadResult = uploadProcess.waitFor();
            if (uploadResult == 0) {
                getLog().info("Uploaded successfully");
            } else {
                getLog().error("Error at uploading!");
                throw new MojoFailureException("Could not upload file to NXT robot!");
            }
        } catch (IOException ioe) {
            getLog().error("I/O Error", ioe);
            throw new MojoExecutionException(
                    "Could not upload file because an I/O error occurred", ioe);
        } catch (InterruptedException ie) {
            getLog().error("Unexpected interruption of spawned process", ie);
            throw new MojoExecutionException(
                    "Could not upload file because spawned process threw an exception", ie);
        }
    }

    @SuppressWarnings("serial")
    private List<String> createUploadCommandLine() {
        return new LinkedList<String>() {{
            add(nxjupload);
            add("-u");
            if (runImmediately) {
                add("-r");
            }
            add(executable);
        }};
    }

    private void pipeOutputToMavenLog(Process process) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                getLog().info(line);
            }
        } catch (IOException e) {
            getLog().warn("Error at piping output of nxjupload tool to maven log", e);
        }
    }
}
