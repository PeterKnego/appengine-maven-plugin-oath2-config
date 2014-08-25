/**
 * Copyright 2012 Google Inc. All Rights Reserved.
 */
package com.google.appengine.appcfg;

import com.google.appengine.SdkResolver;
import com.google.appengine.tools.admin.AppCfg;
import com.google.common.base.Joiner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for supporting appcfg commands.
 *
 * @author Matt Stephenson <mattstep@google.com>
 */
public abstract class AbstractAppCfgMojo extends AbstractMojo {

	/**
	 * The entry point to Aether, i.e. the component doing all the work.
	 *
	 * @component
	 */

	protected RepositorySystem repoSystem;

	/**
	 * The current repository/network configuration of Maven.
	 *
	 * @parameter default-value="${repositorySystemSession}"
	 * @readonly
	 */
	protected RepositorySystemSession repoSession;

	/**
	 * The project's remote repositories to use for the resolution of project dependencies.
	 *
	 * @parameter default-value="${project.remoteProjectRepositories}"
	 * @readonly
	 */
	protected List<RemoteRepository> projectRepos;

	/**
	 * The project's remote repositories to use for the resolution of plugins and their dependencies.
	 *
	 * @parameter default-value="${project.remotePluginRepositories}"
	 * @readonly
	 */
	protected List<RemoteRepository> pluginRepos;

	/**
	 * The server to connect to.
	 *
	 * @parameter expression="${appengine.server}"
	 */
	protected String server;

	/**
	 * The username to use.
	 *
	 * @parameter expression="${appengine.email}"
	 */
	protected String email;

	/**
	 * Override for the Host header setn with all RPCs.
	 *
	 * @parameter expression="${appengine.host}"
	 */
	protected String host;

	/**
	 * Proxies requests through the given proxy server. If --proxy_https is also set, only HTTP will
	 * be proxied here, otherwise both HTTP and HTTPS will.
	 *
	 * @parameter expression="${appengine.proxyHost}"
	 */
	protected String proxyHost;

	/**
	 * Proxies HTTPS requests through the given proxy server.
	 *
	 * @parameter expression="${appengine.proxyHttps}"
	 */
	protected String proxyHttps;

	/**
	 * Do not save/load access credentials to/from disk.
	 *
	 * @parameter expression="${appengine.noCookies}"
	 */
	protected boolean noCookies;

	/**
	 * Always read the login password from stdin.
	 *
	 * @parameter expression="${appengine.passin}"
	 */
	protected boolean passin;

	/**
	 * Do not use HTTPS to communicate with the Admin Console.
	 *
	 * @parameter expression="${appengine.insecure}"
	 */
	protected boolean insecure;

	/**
	 * Override application id from appengine-web.xml or app.yaml.
	 *
	 * @parameter expression="${appengine.appId}"
	 */
	protected String appId;

	/**
	 * Override version from appengine-web.xml or app.yaml.
	 *
	 * @parameter expression="${appengine.version}"
	 */
	protected String version;

	/**
	 * Use OAuth2 instead of password auth. Defaults to true.
	 *
	 * @parameter default-value=true expression="${appengine.oauth2}"
	 */
	protected boolean oauth2;

	/**
	 * Load OAuth2 parameters from external file.
	 *
	 * @parameter expression="${appengine.oauth2ConfigFile}"
	 */
	protected String oauth2ConfigFile;

	/**
	 * Use OAuth2 Client ID.
	 *
	 * @parameter expression="${appengine.oauth2ClientId}"
	 */
	protected String oauth2ClientId;

	/**
	 * Use OAuth2 Client Secret.
	 *
	 * @parameter expression="${appengine.oauth2ClientSecret}"
	 */
	protected String oauth2ClientSecret;

	/**
	 * Use OAuth2 refresh token.
	 *
	 * @parameter expression="${appengine.oauth2RefreshToken}"
	 */
	protected String oauth2RefreshToken;


	/**
	 * Split large jar files (> 10M) into smaller fragments.
	 *
	 * @parameter expression="${appengine.enableJarSplitting}"
	 */
	protected boolean enableJarSplitting;

	/**
	 * When --enable-jar-splitting is set, files that match the list of comma separated SUFFIXES will
	 * be excluded from all jars.
	 *
	 * @parameter expression="${appengine.jarSplittingExcludes}"
	 */
	protected String jarSplittingExcludes;

	/**
	 * Do not delete temporary (staging) directory used in uploading.
	 *
	 * @parameter expression="${appengine.retainUploadDir}"
	 */
	protected boolean retainUploadDir;

	/**
	 * The character encoding to use when compiling JSPs.
	 *
	 * @parameter expression="${appengine.compileEncoding}"
	 */
	protected boolean compileEncoding;

	/**
	 * Number of days worth of log data to get. The cut-off point is midnight UTC. Use 0 to get all
	 * available logs. Default is 1.
	 *
	 * @parameter expression="${appengine.numDays}"
	 */
	protected Integer numDays;

	/**
	 * Severity of app-level log messages to get. The range is 0 (DEBUG) through 4 (CRITICAL). If
	 * omitted, only request logs are returned.
	 *
	 * @parameter expression="${appengine.severity}"
	 */
	protected String severity;

	/**
	 * Append to existing file.
	 *
	 * @parameter expression="${appengine.append}"
	 */
	protected boolean append;

	/**
	 * Number of scheduled execution times to compute.
	 *
	 * @parameter expression="${appengine.numRuns}"
	 */
	protected Integer numRuns;

	/**
	 * Force deletion of indexes without being prompted.
	 *
	 * @parameter expression="${appengine.force}"
	 */
	protected boolean force;

	/**
	 * The name of the backend to perform actions on.
	 *
	 * @parameter expression="${appengine.backendName}"
	 */
	protected String backendName;

	/**
	 * Delete the JSP source files after compilation.
	 *
	 * @parameter expression="${appengine.deleteJsps}"
	 */
	protected boolean deleteJsps;

	/**
	 * Jar the WEB-INF/classes content.
	 *
	 * @parameter expression="${appengine.enableJarClasses}"
	 */
	protected boolean enableJarClasses;

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * Instance id to for vm debug.
	 *
	 * @parameter expression="${appengine.instance}"
	 */
	protected String instance;

	protected void executeAppCfgCommand(String action, String appDir)
					throws MojoExecutionException {
		ArrayList<String> arguments = collectParameters();

		arguments.add(action);
		arguments.add(appDir);
		getLog().info("Running " + Joiner.on(" ").join(arguments));

		try {
			AppCfg.main(arguments.toArray(new String[arguments.size()]));
		} catch (Exception ex) {
			throw new MojoExecutionException("Error executing appcfg command="
							+ arguments, ex);
		}
	}

	protected void executeAppCfgBackendsCommand(String action, String appDir)
					throws MojoExecutionException {
		ArrayList<String> arguments = collectParameters();

		arguments.add("backends");
		arguments.add(action);
		arguments.add(appDir);
		arguments.add(backendName);
		try {
			AppCfg.main(arguments.toArray(new String[arguments.size()]));
		} catch (Exception ex) {
			throw new MojoExecutionException("Error executing appcfg command="
							+ arguments, ex);
		}
	}

	protected ArrayList<String> collectParameters() {
		ArrayList<String> arguments = new ArrayList<String>();

		if (server != null && !server.isEmpty()) {
			arguments.add("-s");
			arguments.add(server);
		}

		if (email != null && !email.isEmpty()) {
			arguments.add("-e");
			arguments.add(email);
		}

		if (host != null && !host.isEmpty()) {
			arguments.add("-H");
			arguments.add(host);
		}

		if (proxyHost != null && !proxyHost.isEmpty()) {
			arguments.add("--proxy=" + proxyHost);
		}

		if (proxyHttps != null && !proxyHttps.isEmpty()) {
			arguments.add("--proxy_https=" + proxyHttps);
		}

		if (noCookies) {
			arguments.add("--no_cookies");
		}

		if (passin) {
			arguments.add("--passin");
		}

		if (insecure) {
			arguments.add("--insecure");
		}

		if (appId != null && !appId.isEmpty()) {
			arguments.add("-A");
			arguments.add(appId);
		}

		if (version != null && !version.isEmpty()) {
			arguments.add("-V");
			arguments.add(version);
		}

		if (oauth2) {
			arguments.add("--oauth2");
		}

		if (oauth2ConfigFile != null) {
			arguments.add("--oauth2_config_file="+oauth2ConfigFile);
		}

		if (oauth2ClientId != null) {
			arguments.add("--oauth2_client_id="+oauth2ClientId);
		}

		if (oauth2ClientSecret != null) {
			arguments.add("--oauth2_client_secret="+oauth2ClientSecret);
		}

		if (oauth2RefreshToken != null) {
			arguments.add("--oauth2_refresh_token="+oauth2RefreshToken);
		}

		if (enableJarSplitting) {
			arguments.add("--enable_jar_splitting");
		}

		if (jarSplittingExcludes != null && !jarSplittingExcludes.isEmpty()) {
			arguments.add("--jar_splitting_excludes=" + jarSplittingExcludes);
		}

		if (retainUploadDir) {
			arguments.add("--retain_upload_dir");
		}

		if (compileEncoding) {
			arguments.add("--compile_encoding");
		}

		if (numDays != null) {
			arguments.add("--num_days=" + numDays.toString());
		}

		if (severity != null && !severity.isEmpty()) {
			arguments.add("--severity=" + severity);
		}

		if (append) {
			arguments.add("-a");
		}

		if (numRuns != null) {
			arguments.add("--num_runs=" + numRuns.toString());
		}

		if (force) {
			arguments.add("-f");
		}

		if (deleteJsps) {
			arguments.add("--delete_jsps");
		}

		if (enableJarClasses) {
			arguments.add("--enable_jar_classes");
		}

		return arguments;
	}

	protected void resolveAndSetSdkRoot() throws MojoExecutionException {

		File sdkBaseDir = SdkResolver.getSdk(project, repoSystem, repoSession, pluginRepos, projectRepos);

		try {
			System.setProperty("appengine.sdk.root", sdkBaseDir.getCanonicalPath());
		} catch (IOException e) {
			throw new MojoExecutionException("Could not open SDK zip archive.", e);
		}
	}
}
