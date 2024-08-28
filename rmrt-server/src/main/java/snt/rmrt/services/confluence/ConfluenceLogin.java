package snt.rmrt.services.confluence;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component

public class ConfluenceLogin {

    @Value("${app.root.user}")
    private String username;
    @Value("${app.root.password}")
    private String password;

    @Value("${confluence.loginForm.url}")
    private String LOGIN_FORM_URL;
    @Value("${confluence.loginAction.url}")
    private String LOGIN_ACTION_URL;


    public Document loginToConfluence(String destinationPage) throws IOException {
        final String USER_AGENT = "Mozilla/5.0";

        // # Go to login page and grab cookies sent by server
        Connection.Response loginForm = Jsoup.connect(LOGIN_FORM_URL)
        		.timeout(0)
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .execute();


        HashMap<String, String> cookies = new HashMap<>(loginForm.cookies()); // save the cookies to be passed on to next request

        HashMap<String, String> formData = new HashMap<>();
        formData.put("login", "Log+in");
        formData.put("os_username", username);
        formData.put("os_password", password);
        formData.put("os_destination", destinationPage);

        // # Now send the form for login
        Connection.Response homePage = Jsoup.connect(LOGIN_ACTION_URL)
        		.followRedirects(true)
                .cookies(cookies)
                .data(formData)
                .method(Connection.Method.POST)
                .userAgent(USER_AGENT)
                .execute();


        //get the document from response
        return homePage.parse();
    }
}
