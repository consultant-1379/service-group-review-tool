package snt.rmrt.services.gerrirt;

import com.google.gerrit.extensions.api.GerritApi;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class GerritRmrtApi {

    @Value("${app.root.user}")
    private String username;
    @Value("${app.root.password}")
    private String password;
    @Value("${gerrit.domain}")
    private String domain;
    public final String branch = "master";

    private GerritApi gerritApi;

    public final GerritApi getGerritApi() {
        if (gerritApi == null) {
//            System.setProperty("https.protocols", "TLSv1.2");
            final GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
            final GerritAuthData.Basic authData = new GerritAuthData.Basic(domain, username, password);
            gerritApi = gerritRestApiFactory.create(authData);
        }
        return gerritApi;
    }

    public final String convertValueToMB(String value) {
        if (value.endsWith("G")) {
            int gbVal = Integer.parseInt(value.replace("G", ""));
            value = Integer.toString(gbVal * 1024);
        }
        if (value.endsWith("MB") || value.endsWith("M")) {
            value = value.replaceAll("[MB]", "");
        }
        return value;
    }
}
