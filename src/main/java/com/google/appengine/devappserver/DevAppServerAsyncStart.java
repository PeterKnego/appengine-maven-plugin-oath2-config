/**
 * Copyright 2012 Google Inc. All Rights Reserved.
 */
package com.google.appengine.devappserver;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.ArrayList;

/**
 * Starts the App Engine development server and does not wait.
 *
 * @author Matt Stephenson <mattstep@google.com>
 * @goal devserver_start
 * @execute phase="package"
 * @threadSafe false
 */
public class DevAppServerAsyncStart extends AbstractDevAppServerMojo {

  /**
   * Wait the server to be started before returning.
   *
   * @parameter expression="${appengine.waitServerStarted}"
   */
  protected boolean waitServerStarted;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("");
    getLog().info("Google App Engine Java SDK - Starting the Development Server");
    getLog().info("");

    String appDir = project.getBuild().getDirectory() + "/" + project.getBuild().getFinalName();

    File appDirFile = new File(appDir);

    if(!appDirFile.exists()) {
      throw new MojoExecutionException("The application directory does not exist : " + appDir);
    }

    if(!appDirFile.isDirectory()) {
      throw new MojoExecutionException("The application directory is not a directory : " + appDir);
    }

    ArrayList<String> devAppServerCommand = getDevAppServerCommand(appDir);

    startDevAppServer(appDirFile, devAppServerCommand, waitServerStarted ? WaitDirective.WAIT_SERVER_STARTED : WaitDirective.DO_NOT_WAIT);
  }

}