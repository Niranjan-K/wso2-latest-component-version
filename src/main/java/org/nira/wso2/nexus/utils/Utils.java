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

import org.nira.wso2.nexus.exception.ComponentException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    /**
     *
     * @param object
     * @param xPathExpression
     * @return
     * @throws ComponentException
     */
    public static NodeList getNodeListFromXPath(Object object, String xPathExpression) throws ComponentException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xPathExpression);

            Document document;
            if (object instanceof InputStream) {
                InputSource inputSource = new InputSource((InputStream) object);
                document = docBuilder.parse(inputSource);
            } else if (object instanceof String) {
                document = docBuilder.parse((String) object);
            } else {
                throw new ComponentException("Object: " + object.getClass() +  " instance not defined!");
            }
            Object xPathResult = expr.evaluate(document, XPathConstants.NODESET);
            return (NodeList) xPathResult;
        } catch (ParserConfigurationException e) {
            throw new ComponentException("Exception for xpath : " + xPathExpression, e);
        } catch (XPathExpressionException e) {
            throw new ComponentException("Exception for xpath : " + xPathExpression, e);
        } catch (SAXException e) {
            throw new ComponentException("Exception for xpath : " + xPathExpression, e);
        } catch (IOException e) {
            throw new ComponentException("Exception for xpath : " + xPathExpression, e);
        }
    }
}
