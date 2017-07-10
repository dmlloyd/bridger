bridger
=======

A bytecode mangler for creating your own synthetic bridge methods, which allows more dramatic source code changes while still maintaining binary compatibility.  And probably some other ugly tricks too.

Usage: Source code
------------------
In your Java source, create your bridge methods with `$$bridge` in the name.  Everything before the `$$bridge` is retained, everything including and after it is discarded.  The methods will additionally be tagged as `ACC_BRIDGE` and `ACC_SYNTHETIC`.

Usage: Maven
------------
Add a snippet like this to your pom.xml:

    <build>
        <plugins>
            <plugin>
                <groupId>org.jboss.bridger</groupId>
                <artifactId>bridger</artifactId>
                <version>1.4.Final</version>
                <executions>
                    <!-- run after "compile", runs bridger on main classes -->
                    <execution>
                        <id>weave</id>
                        <phase>process-classes</phase>
                        <goals>
                            <goal>transform</goal>
                        </goals>
                    </execution>
                    <!-- run after "test-compile", runs bridger on test classes -->
                    <execution>
                        <id>weave-tests</id>
                        <phase>process-test-classes</phase>
                        <goals>
                            <goal>transform</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.testOutputDirectory}</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

_Note that a separate execution is needed if you want your test classes to be transformed._

Usage: Command Line
-------------------
Execute like this:

    java -classpath path/to/bridger.jar:path/to/asm-4.1.jar org.jboss.bridger.Bridger path/of/class/files/

The class files will be transformed in place.
