package org.jboss.bridger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * A plugin that translates bridge methods.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Mojo(name = "transform", defaultPhase = LifecyclePhase.PROCESS_CLASSES, threadSafe = true)
public class BridgerMojo extends AbstractMojo {

    /**
     * The output directory where resources should be processed
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    /**
     * File patterns to include when processing
     */
    @Parameter(property = "bridger.excludes")
    private String[] excludes;

    /**
     * File patterns to exclude when processing
     */
    @Parameter(defaultValue = "**/*.class", property = "bridger.includes")
    private String[] includes;

    @Parameter(defaultValue = "false", property = "bridger.transform.skip")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Log log = getLog();
        if (skip) {
            log.info("Skipping bridger transform");
        } else {
            final Bridger bridger = new Bridger();
            final File[] files = getFiles();

            if (log.isDebugEnabled()) {
                final String newLine = String.format("%n\t");
                final StringBuilder sb = new StringBuilder("Transforming Files:");
                sb.append(newLine);
                for (File file : files) {
                    sb.append(file.getAbsolutePath()).append(newLine);
                }
                log.debug(sb);
            }

            bridger.transformRecursive(files);
            log.info(String.format("Translated %d methods and %d method calls%n", bridger.getTransformedMethodCount(), bridger.getTransformedMethodCallCount()));
        }
    }

    private File[] getFiles() {
        final List<File> result = new ArrayList<File>();
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(outputDirectory);
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        scanner.scan();
        for (String filename : scanner.getIncludedFiles()) {
            // Only class files
            final File targetFile = new File(outputDirectory, filename);
            if (targetFile.exists()) {
                result.add(targetFile.getAbsoluteFile());
            }
        }
        return result.toArray(new File[result.size()]);
    }
}
