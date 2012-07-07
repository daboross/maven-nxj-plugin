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
package no.heim.maven.plugins.nxj.link;

import java.io.File;
import java.util.Collection;

import org.apache.maven.model.Dependency;

/**
 * This is a helper class for building a classpath of
 * all compile scope dependencies of a given dependencies list.
 *
 * @author Felix Müller
 */
public class ClasspathBuilder {

    private final String rootPath;
    private final Collection<Dependency> dependencies;

    public ClasspathBuilder(String rootPath, Collection<Dependency> dependencies) {
        this.rootPath = rootPath;
        this.dependencies = dependencies;
    }

    public String build() {
        final StringBuilder path = new StringBuilder();
        for (Dependency d : dependencies) {
            if (d.getScope().equalsIgnoreCase("compile")) {
                appendFile(path, rootPath);
                for (String groupPart : d.getGroupId().split("\\.")) {
                    appendFile(path, groupPart);
                }
                appendFile(path, d.getArtifactId());
                appendFile(path, d.getVersion());
                appendArtifactFilename(path, d);
                path.append(File.pathSeparator);
            }
        }
        return cutFilePathSeparatorAtEnd(path);
    }

    private void appendFile(StringBuilder path, String file) {
        path.append(file);
        if (!file.endsWith(File.separator)) {
            path.append(File.separator);
        }
    }

    private void appendArtifactFilename(StringBuilder path, Dependency dependency) {
        path.append(dependency.getArtifactId());
        path.append('-');
        path.append(dependency.getVersion());
        path.append('.');
        path.append(dependency.getType());
    }

    private String cutFilePathSeparatorAtEnd(final StringBuilder path) {
        if (path.toString().endsWith(File.pathSeparator)) {
            return path.toString().substring(0, path.length() - File.pathSeparator.length());
        } else {
            return path.toString();
        }
    }
}
