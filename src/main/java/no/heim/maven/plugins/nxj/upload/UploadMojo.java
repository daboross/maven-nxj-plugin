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

import java.io.File;
import java.io.IOException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.tools.NXTNotFoundException;
import lejos.pc.tools.Upload;

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
     * Name of the NXT
     * 
     * @parameter
     */
    private String nxtName;

    /**
     * Mac address of the NXT
     * 
     * @parameter
     */
    private String nxtAddress;

    /**
     * The protocol to use for transmission (either "usb" or "bluetooth")
     * 
     * @parameter default-value="usb"
     */
    private String protocol;

    /**
     * The nxj executable file
     *
     * @parameter
     * @required
     */
    private String executable;

    /**
     * The filename of the nxj executable file at the robot
     *
     * @parameter
     * @required
     */
    private String nxtFilename;

    /**
     * If true the uploaded executable starts immediately
     *
     * @parameter expression=${runImmediately} default-value=true
     */
    private boolean shouldImmediatelyRun;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            getLog().info("Start uploading to nxt...");

            final boolean isUsb = protocol.equalsIgnoreCase("usb");
            new Upload().upload(nxtName,
                    nxtAddress,
                    (isUsb ? NXTCommFactory.USB : NXTCommFactory.BLUETOOTH),
                    new File(executable),
                    nxtFilename,
                    shouldImmediatelyRun);

            getLog().info("Uploaded successfully");

        } catch (NXTNotFoundException e) {
            getLog().error("Could not perform upload", e);
            throw new MojoFailureException(e, "NXT not found", "The given NXT was not found");
        } catch (IOException e) {
            getLog().error("Could not perform upload", e);
            throw new MojoFailureException(e, "IO error at upload", "IO error at upload to the NXT robot");
        }
    }
}
