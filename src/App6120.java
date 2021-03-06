import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

public class App6120 {

    /**
     * CHANGE WITH CORRECT VALUE:
     * The Group_id has to be obtained from the database, it is the one that has the friendlyUrl: /guest
     * 
     * These properties were used:
     * auth.token.check.enabled=false
     * json.service.auth.token.enabled=fals
     * json.servlet.hosts.allowed=127.0.0.1, SERVER_IP
     * axis.servlet.hosts.allowed=127.0.0.1, SERVER_IP
     * jsonws.web.service.public.methods=*
     * jsonws.servlet.hosts.allowed=127.0.0.1,SERVER_IP
     * json.service.auth.token.hosts.allowed=127.0.0.1,192.168.0.101
     * change this last one to you own ip address
     * 
     */
    private static final String PORTAL_URL = "http://localhost:8080";
    private static final String URL_ADD_ARTICLE = "/api/secure/jsonws/journalarticle/add-article";
    private static final String LOGIN = "test@liferay.com";
    private static final String PASSWORD = "test";
    
    private static final long GROUP_ID = 10180;					// 6120
    private static final String structureId = null;	// 6120
    private static final String templateId = null;		// 6120

    public void addArticle() throws Exception {
        HttpPost httpPost = new HttpPost(URL_ADD_ARTICLE);
        setEntities(httpPost);
        String response = executeRequest(httpPost);
        System.out.println(response);
    }

    private void setEntities(HttpPost httpPost) throws Exception {
        String[] names = new String[] {
                "groupId",
                "classNameId",
                "classPK",
                "articleId",
                "autoArticleId",
                "titleMap",
                "descriptionMap",
                "content",
                "type",
                "structureId",
                "templateId",
                "layoutUuid",
                "displayDateMonth",
                "displayDateDay",
                "displayDateYear",
                "displayDateHour",
                "displayDateMinute",
                "expirationDateMonth",
                "expirationDateDay",
                "expirationDateYear",
                "expirationDateHour",
                "expirationDateMinute",
                "neverExpire",
                "reviewDateMonth",
                "reviewDateDay",
                "reviewDateYear",
                "reviewDateHour",
                "reviewDateMinute",
                "neverReview",
                "indexable",
                "articleURL",
                "serviceContext"
        };


        String titleMap = "{\"en_US\":\"Title SG\"}";
        String descriptionMap = "{\"en_US\":\"Description SG\"}";

        String content = "<root available-locales=\"en_US\" default-locale=\"en_US\"><dynamic-element "
                + "instance-id=\"w8lkXLvN\" name=\"image\" type=\"image\" index-type=\"text\"><dynamic-content "
                + "id=\"18809\">/image/journal/article?img_id=18809&amp;"
                + "t=1423488405667</dynamic-content></dynamic-element></root>";

        Calendar calendar = Calendar.getInstance();
        int displayDateMonth = calendar.get(Calendar.MONTH);
        int displayDateDay = calendar.get(Calendar.DAY_OF_MONTH);
        int displayDateYear = calendar.get(Calendar.YEAR);
        int displayDateHour = calendar.get(Calendar.HOUR_OF_DAY);
        int displayDateMinute = calendar.get(Calendar.MINUTE);

        byte[] bytes = convert(this.getClass().getClassLoader().getResourceAsStream("screenshot.png"));
        String images = "{\"w8lkXLvN_image_\":" + Arrays.toString(bytes) + "}";

        String serviceContext = 
                " \"scopeGroupId\":\"" + GROUP_ID + "\"}";

        Object[] values = new Object[] {
                GROUP_ID,           // groupId
                1,                  // classNameId HAS TO BE GREATER THAN 0
                0,                  // classPK
                0,                  // articleId
                true,               // autoArticleId
                titleMap,           // titleMap
                descriptionMap,     // descriptionMap
                content,            // content
                "general",          // type
                structureId,  // ddmStructureKey
                templateId,   // ddmTemplateKey
                "",                 // layoutId
                displayDateMonth,   // displayDateMonth
                displayDateDay,     // displayDateDay
                displayDateYear,    // displayDateYear
                displayDateHour,    // displayDateHour
                displayDateMinute,  // displayDateMinute
                0,                  // expirationDateMonth
                0,                  // expirationDateDay
                0,                  // expirationDateYear
                0,                  // expirationDateHour
                0,                  // expirationDateMinute
                true,               // neverExpire
                0,                  // reviewDateMonth
                0,                  // reviewDateDay
                0,                  // reviewDateYear
                0,                  // reviewDateHour
                0,                  // reviewDateMinute
                true,               // neverReview
                true,               // indexable
                "",                 // articleURL
                serviceContext      // serviceContext
        };

        httpPost.setEntity(getMultipartEntity(names, values));
    }

    private MultipartEntity getMultipartEntity(String[] names, Object[] values)
            throws Exception {

        MultipartEntity multipartEntity = new MultipartEntity();

        for (int i = 0; i < names.length; i++) {
            multipartEntity.addPart(names[i], getStringBody(values[i]));
        }

        return multipartEntity;
    }

    private ContentBody getStringBody(Object value) throws Exception {
        return new StringBody(String.valueOf(value), Charset.defaultCharset());
    }

    private byte[] convert(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private String executeRequest(HttpRequest request) throws Exception {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        URL url = new URL(PORTAL_URL);
        HttpHost httpHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());

        CredentialsProvider credentialsProvider = defaultHttpClient.getCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(url.getHost(), url.getPort()),
                new UsernamePasswordCredentials(LOGIN, PASSWORD));

        BasicAuthCache basicAuthCache = new BasicAuthCache();
        BasicScheme basicScheme = new BasicScheme();
        basicAuthCache.put(httpHost, basicScheme);
        BasicHttpContext basicHttpContext = new BasicHttpContext();
        basicHttpContext.setAttribute(ClientContext.AUTH_CACHE, basicAuthCache);

        return defaultHttpClient.execute(httpHost, request, new StringHandler(), basicHttpContext);
    }

    private class StringHandler implements ResponseHandler<String> {
        @Override
        public String handleResponse(HttpResponse response) throws IOException {
            checkStatusCode(response.getStatusLine());
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                return null;
            }
            return EntityUtils.toString(httpEntity);
        }

        protected void checkStatusCode(StatusLine statusLine) throws HttpResponseException {
            if (statusLine.getStatusCode() != 200) {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
        }

    }
} 