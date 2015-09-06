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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nira.wso2.nexus.exception.ComponentException;
import org.nira.wso2.nexus.utils.Constants;
import org.nira.wso2.nexus.utils.Utils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class ComponentVersion {

    private static Map<String, String> dependencyComponentVersions = new HashMap<>();
    private static List<DependencyComponent> dependencyComponentList = new ArrayList<>();
    private static List<String> urlList = new ArrayList<>();

    public static void main(String[] args) {
        urlList.add(Constants.REPO_MAVEN);
        urlList.add(Constants.REPO_MAVEN_WSO2_RELEASES);
        urlList.add(Constants.REPO_MAVEN_WSO2_PUBLIC);
        String pomFilePath = "";
        boolean ignoreSameVersion = false;

        for (String arg : args) {
            if (arg.startsWith(Constants.CMD_POM_FILE)) {
                pomFilePath = arg.substring(Constants.CMD_POM_FILE.length() + 1);
            } else if (arg.startsWith(Constants.CMD_IGNORE_SAME_VERSION)) {
                String param = arg.substring(Constants.CMD_IGNORE_SAME_VERSION.length() + 1);
                if ("true".equals(param)) {
                    ignoreSameVersion = true;
                }
            }
        }

//        pomFilePath = "C:\\Users\\Nira\\Desktop\\as_pom.xml";
        if (pomFilePath.isEmpty()) {
            throw new ComponentException("Pom File path not specified!");
        }
        readPomFile(pomFilePath);
        displayComponents(ignoreSameVersion);
    }

    private static void displayComponents(boolean ignoreSameVersion) {
        System.out.println("**************");
        System.out.print("GroupId\t");
        System.out.print("ArtifactId\t");
        System.out.print("Version\t");
        System.out.print("Latest Version\t");
        System.out.println("Maven URL");

        for (DependencyComponent dc : dependencyComponentList) {
            if (!ignoreSameVersion || !dc.getVersion().trim().equalsIgnoreCase(dc.getLatestVersion().trim())) {
                System.out.print(dc.getGroupId() + "\t");
                System.out.print(dc.getArtifactId() + "\t");
                System.out.print(dc.getVersion() + "\t");
                System.out.print(dc.getLatestVersion() + "\t");
                System.out.println(dc.getMavenUrl());
            }
        }
    }

    /**
     * Loads the Dependencies and its corresponding version
     *
     * @param pomFilePath : The path to the root pom.xml
     * @throws ComponentException
     */
    private static void readPomFile(String pomFilePath) throws ComponentException {
        if (Files.isReadable(Paths.get(pomFilePath))) {
            getDependenyVersions(pomFilePath);
            getDependencyManagement(pomFilePath);
        }
    }

    /**
     * Loads the List of all the Dependencies in the DependencyManagement from  the root
     * pom.xml
     *
     * @param pomFilePath : The path to the root pom.xml
     * @throws ComponentException
     */
    private static void getDependencyManagement(String pomFilePath) throws ComponentException {
        String nodeName, nodeValue;
        DependencyComponent dependencyComponent;
        NodeList dependenciesList = Utils.getNodeListFromXPath(pomFilePath, Constants.DEPENDENCY_MANAGEMENT_XPATH);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try {
            for (int i = 0; i < dependenciesList.getLength(); ++i) {

                Node dependency = dependenciesList.item(i);
                if (dependency != null && dependency.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList nodes = (NodeList) xpath.evaluate(Constants.SELECT_ALL, dependency, XPathConstants.NODESET);
                    dependencyComponent = new DependencyComponent();
                    for (int j = 0; j < nodes.getLength(); ++j) {
                        nodeName = nodes.item(j).getNodeName();
                        nodeValue = nodes.item(j).getTextContent();
                        if (nodeValue == null) {
                            throw new ComponentException("Dependency value is NULL for " + nodeName + "!");
                        }
                        switch (nodeName) {
                            case Constants.GROUPID:
                                dependencyComponent.setGroupId(nodeValue);
                                break;
                            case Constants.ARTIFACTID:
                                dependencyComponent.setArtifactId(nodeValue);
                                break;
                            case Constants.VERSION:
                                if (Constants.PROJECT_VERSION.equalsIgnoreCase(nodeValue)) {
                                    break;
                                }
                                while (!Character.isDigit(nodeValue.charAt(0))) {
                                    nodeValue = nodeValue.substring(2, nodeValue.length() - 1);
                                    nodeValue = dependencyComponentVersions.get(nodeValue);
                                    if (nodeValue == null) {
                                        throw new ComponentException("Dependency Version cannot be NULL!");
                                    }
                                }
                                dependencyComponent.setVersion(nodeValue);
                                break;
                        }
                    }
                    if (dependencyComponent.getGroupId() != null && dependencyComponent.getArtifactId() != null &&
                            dependencyComponent.getVersion() != null) {
                        getLatestComponentVersion(dependencyComponent);
                        dependencyComponentList.add(dependencyComponent);
                    }
                }
            }
        } catch (XPathExpressionException e) {
            throw new ComponentException("XPath Exception when retrieving Dependency Components!", e);
        }
        Collections.sort(dependencyComponentList, DependencyComponent.GroupIdArifactIdComparator);
    }

    /**
     * Loads the dependency versions into the hashmap
     *
     * @param pomFilePath : The path to the root pom.xml
     * @throws ComponentException
     */
    private static void getDependenyVersions(String pomFilePath) throws ComponentException {
        NodeList nodeList = Utils.getNodeListFromXPath(pomFilePath, Constants.DEPENDENCY_PROPERTIES_XPATH);
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (nodeList.item(i).getTextContent() != null) {
                dependencyComponentVersions.put(nodeList.item(i).getNodeName(), nodeList.item(i).getTextContent());
            }
        }
    }

    /**
     * Retrieves the latest version of the component
     *
     * @param dependencyComponent : Dependency Component
     * @throws ComponentException
     */
    private static void getLatestComponentVersion(DependencyComponent dependencyComponent) throws ComponentException {
        String groupIdArtifactUrl = dependencyComponent.getGroupId().replaceAll("\\.", "/") + "/" +
                dependencyComponent.getArtifactId();

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet;
        String httpUrl;
        HttpResponse httpResponse;

        for (String url : urlList) {
            httpUrl = url + groupIdArtifactUrl + "/";
            httpGet = new HttpGet(httpUrl + Constants.MAVEN_METADATA_XML);
            System.out.println(httpUrl + Constants.MAVEN_METADATA_XML);
            httpGet.addHeader(Constants.USER_AGENT, Constants.CHROME);
            try {
                httpResponse = httpClient.execute(httpGet);
                if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    NodeList nodes = Utils.getNodeListFromXPath(httpResponse.getEntity().getContent(), Constants.MAVEN_META_XPATH_VERSION);
                    List<String> versionList = new ArrayList<>();
                    for (int i = 0; i < nodes.getLength(); ++i) {
                        versionList.add(nodes.item(i).getNodeValue());
                    }
                    if (versionList.size() > 0) {
                        Collections.sort(versionList);
                    }
                    dependencyComponent.setMavenUrl(httpUrl);
                    dependencyComponent.setLatestVersion(versionList.get(versionList.size() - 1));
                }
            } catch (IOException e) {
                throw new ComponentException("Error retrieving Latest Component Version!", e);
            }
        }
    }
}
