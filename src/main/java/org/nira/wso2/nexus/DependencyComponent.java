package org.nira.wso2.nexus;

/**
 * Copyright 2015 Niranjan Karunanandham (Nira)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Comparator;

public class DependencyComponent {

    private String groupId;
    private String artifactId;
    private String version;
    private String latestVersion;
    private String mavenUrl;

    public static Comparator<DependencyComponent> GroupIdArifactIdComparator = new Comparator<DependencyComponent>() {
        @Override
        public int compare(DependencyComponent dependencyComponent1, DependencyComponent dependencyComponent2) {
            String groupId1 = dependencyComponent1.getGroupId();
            String groupId2 = dependencyComponent2.getGroupId();
            String artifactId1 = dependencyComponent1.getArtifactId();
            String artifactId2 = dependencyComponent2.getArtifactId();

            if (groupId1.equalsIgnoreCase(groupId2)) {
                return artifactId1.compareTo(artifactId2);
            } else {
                return groupId1.compareTo(groupId2);
            }
        }
    };

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getMavenUrl() {
        return mavenUrl;
    }

    public void setMavenUrl(String mavenUrl) {
        this.mavenUrl = mavenUrl;
    }
}
