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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.junit.Test;

public class ClasspathBuilderTest {

    private static final String FILE_SEP = File.separator;
    private static final String PATH_SEP = File.pathSeparator;
    private static final String ROOT_PATH = "/temp";

    @Test
    public void oneDependency() {
        assertThat(classpathBuilder(dependency("myGroup", "myArtifact", "1.0.0")).build(), is(ROOT_PATH + FILE_SEP
                + "myGroup" + FILE_SEP + "myArtifact" + FILE_SEP + "1.0.0" + FILE_SEP + "myArtifact-1.0.0.jar"));
    }

    @Test
    public void onlyDependenciesWithCompileScope() {
        assertThat(classpathBuilder(dependency("myGroup", "myArtifact", "1.0.0", "jar", "test")).build(), is(""));
    }

    @Test
    public void oneDependencyWithNestedGroupId() {
        assertThat(classpathBuilder(dependency("my.Group.id-test", "myArtifact", "1.0.0")).build(), is(ROOT_PATH
                + FILE_SEP + "my" + FILE_SEP + "Group" + FILE_SEP + "id-test" + FILE_SEP + "myArtifact" + FILE_SEP
                + "1.0.0" + FILE_SEP + "myArtifact-1.0.0.jar"));
    }

    @Test
    public void oneDependencyWithOtherTypeThanJar() {
        assertThat(classpathBuilder(dependency("myGroup", "myArtifact", "1.0.0", "war")).build(), is(ROOT_PATH
                + FILE_SEP + "myGroup" + FILE_SEP + "myArtifact" + FILE_SEP + "1.0.0" + FILE_SEP + "myArtifact-"
                + "1.0.0.war"));
    }

    @Test
    public void manyDependenciesWithNestedGroupIdsAndManyTypes() {
        final List<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.add(dependency("myGroup", "myArtifact", "1.0.0", "war"));
        dependencies.add(dependency("secondGroup.test", "dummy-jar", "10.2.3-SNAPSHOT", "jar"));
        dependencies.add(dependency("biggest.company.of.world", "big-web-app", "1.0.0", "ear"));

        final String expectedPath = ROOT_PATH + FILE_SEP + "myGroup" + FILE_SEP + "myArtifact" + FILE_SEP + "1.0.0"
                + FILE_SEP + "myArtifact-1.0.0.war" + PATH_SEP + ROOT_PATH + FILE_SEP + "secondGroup" + FILE_SEP
                + "test" + FILE_SEP + "dummy-jar" + FILE_SEP + "10.2.3-SNAPSHOT" + FILE_SEP
                + "dummy-jar-10.2.3-SNAPSHOT.jar" + PATH_SEP + ROOT_PATH + FILE_SEP + "biggest" + FILE_SEP + "company"
                + FILE_SEP + "of" + FILE_SEP + "world" + FILE_SEP + "big-web-app" + FILE_SEP + "1.0.0" + FILE_SEP
                + "big-web-app-1.0.0.ear";

        assertThat(classpathBuilder(dependencies).build(), is(expectedPath));
    }

    @Test
    public void rootPathWithTrailingFileSeparator() {
        final List<Dependency> dependencies = Arrays.asList(new Dependency[] {
                dependency("myGroup", "myArtifact", "1.0.0") });
        assertThat(new ClasspathBuilder(ROOT_PATH + FILE_SEP, dependencies).build(), is(ROOT_PATH + FILE_SEP
                + "myGroup" + FILE_SEP + "myArtifact" + FILE_SEP + "1.0.0" + FILE_SEP + "myArtifact-" + "1.0.0.jar"));
    }

    private ClasspathBuilder classpathBuilder(Dependency dependency) {
        return classpathBuilder(Arrays.asList(new Dependency[] { dependency }));
    }

    private ClasspathBuilder classpathBuilder(Collection<Dependency> dependencies) {
        return new ClasspathBuilder(ROOT_PATH, dependencies);
    }

    private Dependency dependency(String groupId, String artifactId, String version) {
        return dependency(groupId, artifactId, version, "jar");
    }

    private Dependency dependency(String groupId, String artifactId, String version, String type) {
        return dependency(groupId, artifactId, version, type, "compile");
    }

    private Dependency dependency(String groupId, String artifactId, String version, String type, String scope) {
        final Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        dependency.setType(type);
        dependency.setScope(scope);
        return dependency;
    }
}
