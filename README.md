bridger
=======

A bytecode mangler for creating your own synthetic bridge methods, which allows more dramatic source code changes while still maintaining binary compatibility.  And probably some other ugly tricks too.

Usage: Source code
------------------
In your Java source, create your bridge methods with `$$bridge` in the name.  Everything before the `$$bridge` is retained, everything including and after it is discarded.  The methods will additionally be tagged as `ACC_BRIDGE` and `ACC_SYNTHETIC`.

If the bridge method name includes `$$public` after `$$bridge`, the bridge method will additionally be made `public` in the bytecode.
Similarly, `$$protected` can be used to make the bridge method `protected`.
This allows keeping the bridge method `private` in the source code.
(If neither `$$public` nor `$$protected` is present in the name, the access mode of the method is left unchanged.)

If you add the `@hidden` tag (available since Java 9) to the method's JavaDoc comment, it will not show up in rendered documentation for the class.

Usage: Maven
------------
Add a snippet like this to your pom.xml:

    <build>
        <plugins>
            <plugin>
                <groupId>org.jboss.bridger</groupId>
                <artifactId>bridger</artifactId>
                <version>1.6.Final</version>
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

    java -classpath path/to/bridger.jar:path/to/asm-6.0.jar org.jboss.bridger.Bridger path/of/class/files/

You can also perform single method transforms on any given method like this:

    java -classpath path/to/bridger.jar:path/to/asm-6.0.jar org.jboss.bridger.Bridger --single-transform path/to/class/file.class methodName [methodDescriptor]

Using this mechanism, the method does not need to have $$bridge in the name. This bridges all overloads of the method with this name, unless the descriptor is provided.
Note that this uses JVM method descriptor notation, so a method such as `void method(String s, int i)` would
have the descriptor `(Ljava/lang/String;I)V`. This can be obtained by for instance using `javap -s` on the class file.

The class files will be transformed in place.
