package org.nira.wso2.nexus.utils;

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

public class Constants {

    public static final String CMD_POM_FILE = "-DpomFile";
    public static final String CMD_IGNORE_SAME_VERSION = "-DignoreSameVersion";

    public static final String WSO2_MAVEN_URL = "http://maven.wso2.org/nexus/content/repositories";
    public static final String REPO_MAVEN_WSO2_RELEASES = "http://maven.wso2.org/nexus/content/repositories/releases/";
    public static final String REPO_MAVEN_WSO2_PUBLIC = "http://maven.wso2.org/nexus/content/groups/wso2-public/";
    public static final String REPO_MAVEN = "http://repo1.maven.org/maven2/";

    public static final String PUBLIC = "public";
    public static final String MAVEN_METADATA_XML = "maven-metadata.xml";

    public static final String USER_AGENT = "User-Agent";
    public static final String CHROME = "Chrome";

    public static final String GROUPID = "groupId";
    public static final String ARTIFACTID = "artifactId";
    public static final String VERSION = "version";
    public static final String DEPENDENCY = "dependency";
    public static final String PROJECT_VERSION = "${project.version}";

    public static final String MAVEN_META_XPATH_VERSION = "//metadata/versioning/versions/version/text()";
    public static final String DEPENDENCY_PROPERTIES_XPATH = "//project/properties/*";
    public static final String DEPENDENCY_MANAGEMENT_XPATH = "/project/dependencyManagement/dependencies/dependency";
    public static final String SELECT_ALL = "*";
}
