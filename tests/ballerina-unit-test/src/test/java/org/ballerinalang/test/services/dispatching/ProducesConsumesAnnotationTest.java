/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.services.dispatching;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.ballerinalang.launcher.util.BServiceUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BJSON;
import org.ballerinalang.net.http.HttpConstants;
import org.ballerinalang.test.services.testutils.HTTPTestRequest;
import org.ballerinalang.test.services.testutils.MessageUtils;
import org.ballerinalang.test.services.testutils.Services;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;
import org.wso2.transport.http.netty.message.HttpMessageDataStreamer;

/**
 * Test class for @Produces @Consumes annotation tests.
 */
public class ProducesConsumesAnnotationTest {

    private static final String TEST_EP = "testEP";
    CompileResult compileResult;

    @BeforeClass()
    public void setup() {
        compileResult = BServiceUtil
                .setupProgramFile(this, "test-src/services/dispatching/produces-consumes-test.bal");
    }

    @Test(description = "Test Consumes annotation with URL. /echo66/test1 ")
    public void testConsumesAnnotation() {
        String path = "/echo66/test1";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "POST", "Test");
        cMsg.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/xml; charset=ISO-8859-4");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        BJSON bJson = new BJSON(new HttpMessageDataStreamer(response).getInputStream());
        Assert.assertEquals(bJson.value().get("msg").asText(), "wso2", "Content type matched");
    }

    @Test(description = "Test incorrect Consumes annotation with URL. /echo66/test1 ")
    public void testIncorrectConsumesAnnotation() {
        String path = "/echo66/test1";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "POST", "Test");
        cMsg.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "compileResult/json");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        int trueResponse = (int) response.getProperty(HttpConstants.HTTP_STATUS_CODE);
        Assert.assertEquals(trueResponse, 415, "Unsupported media type");
    }

    @Test(description = "Test bogus Consumes annotation with URL. /echo66/test1 ")
    public void testBogusConsumesAnnotation() {
        String path = "/echo66/test1";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "POST", "Test");
        cMsg.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), ",:vhjv");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        int trueResponse = (int) response.getProperty(HttpConstants.HTTP_STATUS_CODE);
        Assert.assertEquals(trueResponse, 415, "Unsupported media type");
    }

    @Test(description = "Test Produces annotation with URL. /echo66/test2 ")
    public void testProducesAnnotation() {
        String path = "/echo66/test2";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "GET");
        cMsg.setHeader(HttpHeaderNames.ACCEPT.toString(), "text/xml;q=0.3, multipart/*;Level=1;q=0.7");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        BJSON bJson = new BJSON(new HttpMessageDataStreamer(response).getInputStream());
        Assert.assertEquals(bJson.value().get("msg").asText(), "wso22", "media type matched");
    }

    @Test(description = "Test Produces with no Accept header with URL. /echo66/test2 ")
    public void testProducesAnnotationWithNoHeaders() {
        String path = "/echo66/test2";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "GET");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        BJSON bJson = new BJSON(new HttpMessageDataStreamer(response).getInputStream());
        Assert.assertEquals(bJson.value().get("msg").asText(), "wso22", "media type matched");
    }

    @Test(description = "Test Produces with wildcard header with URL. /echo66/test2 ")
    public void testProducesAnnotationWithWildCard() {
        String path = "/echo66/test2";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "GET");
        cMsg.setHeader(HttpHeaderNames.ACCEPT.toString(), "*/*, text/html;Level=1;q=0.7");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        BJSON bJson = new BJSON(new HttpMessageDataStreamer(response).getInputStream());
        Assert.assertEquals(bJson.value().get("msg").asText(), "wso22", "media type matched");
    }

    @Test(description = "Test Produces with sub type wildcard header with URL. /echo66/test2 ")
    public void testProducesAnnotationWithSubTypeWildCard() {
        String path = "/echo66/test2";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "GET");
        cMsg.setHeader(HttpHeaderNames.ACCEPT.toString(), "text/*;q=0.3, text/html;Level=1;q=0.7");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        BJSON bJson = new BJSON(new HttpMessageDataStreamer(response).getInputStream());
        Assert.assertEquals(bJson.value().get("msg").asText(), "wso22", "media type matched");
    }

    @Test(description = "Test incorrect Produces annotation with URL. /echo66/test2 ")
    public void testIncorrectProducesAnnotation() {
        String path = "/echo66/test2";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "GET");
        cMsg.setHeader(HttpHeaderNames.ACCEPT.toString(), "multipart/*;q=0.3, text/html;Level=1;q=0.7");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        int trueResponse = (int) response.getProperty(HttpConstants.HTTP_STATUS_CODE);
        Assert.assertEquals(trueResponse, 406, "Not acceptable");
    }

    @Test(description = "Test bogus Produces annotation with URL. /echo66/test2 ")
    public void testBogusProducesAnnotation() {
        String path = "/echo66/test2";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "GET");
        cMsg.setHeader(HttpHeaderNames.ACCEPT.toString(), ":,;,v567br");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        int trueResponse = (int) response.getProperty(HttpConstants.HTTP_STATUS_CODE);
        Assert.assertEquals(trueResponse, 406, "Not acceptable");
    }

    @Test(description = "Test Produces and Consumes with URL. /echo66/test3 ")
    public void testProducesConsumeAnnotation() {
        String path = "/echo66/test3";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "POST", "Test");
        cMsg.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "text/plain; charset=ISO-8859-4");
        cMsg.setHeader(HttpHeaderNames.ACCEPT.toString(), "text/*;q=0.3, text/html;Level=1;q=0.7");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        BJSON bJson = new BJSON(new HttpMessageDataStreamer(response).getInputStream());
        Assert.assertEquals(bJson.value().get("msg").asText(), "wso222", "media types matched");
    }

    @Test(description = "Test Incorrect Produces and Consumes with URL. /echo66/test3 ")
    public void testIncorrectProducesConsumeAnnotation() {
        String path = "/echo66/test3";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "POST", "Test");
        cMsg.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "text/plain ; charset=ISO-8859-4");
        cMsg.setHeader(HttpHeaderNames.ACCEPT.toString(), "compileResult/xml, text/html");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        int trueResponse = (int) response.getProperty(HttpConstants.HTTP_STATUS_CODE);
        Assert.assertEquals(trueResponse, 406, "Not acceptable");
    }

    @Test(description = "Test without Pro-Con annotation with URL. /echo67/echo1 ")
    public void testWithoutProducesConsumeAnnotation() {
        String path = "/echo67/echo1";
        HTTPTestRequest cMsg = MessageUtils.generateHTTPMessage(path, "GET");
        cMsg.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "text/plain; charset=ISO-8859-4");
        cMsg.setHeader(HttpHeaderNames.ACCEPT.toString(), "text/*;q=0.3, text/html;Level=1;q=0.7");
        HttpCarbonMessage response = Services.invokeNew(compileResult, TEST_EP, cMsg);

        Assert.assertNotNull(response, "Response message not found");
        BJSON bJson = new BJSON(new HttpMessageDataStreamer(response).getInputStream());
        Assert.assertEquals(bJson.value().get("echo33").asText(), "echo1", "No media types");
    }
}
