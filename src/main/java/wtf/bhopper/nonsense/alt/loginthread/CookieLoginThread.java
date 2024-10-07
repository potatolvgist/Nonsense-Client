package wtf.bhopper.nonsense.alt.loginthread;

import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.mslogin.MSAuthException;
import wtf.bhopper.nonsense.gui.screens.altmanager.GuiAltManager;
import wtf.bhopper.nonsense.util.ErrorCallback;
import wtf.bhopper.nonsense.util.Http;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public class CookieLoginThread extends LoginThread {

    private static final String COOKIE_URL = "https://sisu.xboxlive.com/connect/XboxLive/?state=login&cobrandId=8058f65d-ce06-4c30-9559-473c9275a65d&tid=896928775&ru=https%3A%2F%2Fwww.minecraft.net%2Fen-us%2Flogin&aid=1142970254";

    private final File file;
    private String cookieHeader;

    public CookieLoginThread(File file, LoginDataCallback loginDataCallback, ErrorCallback errorCallback) {
        super(loginDataCallback, errorCallback);
        this.file = file;
    }

    @Override
    void execute() {
        try {

            GuiAltManager.message = "Logging in to Cookie alt...";

            List<Cookie> cookies = this.parseCookies();
            this.cookieHeader = this.buildCookieHeader(cookies);
            Nonsense.LOGGER.info("Cookie Header: {}", this.cookieHeader);
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
            headers.put("Cookie", this.cookieHeader);
            headers.put("User-Agent", Http.FIREFOX_USER_AGENT);

            Http http1 = new Http(COOKIE_URL)
                    .headers(headers)
                    .get();
            if (http1.status() != 302) {
                throw new MSAuthException("Request to " + COOKIE_URL + " returned status " + http1.status());
            }

            Http http2 = new Http(http1.getHeader("location"))
                    .headers(headers)
                    .get();
            if (http2.status() != 302) {
                throw new MSAuthException("Request to " + http1.getHeader("location") + " returned status " + http1.status());
            }

            Http http3 = new Http(http2.getHeader("location"))
                    .headers(headers)
                    .get();
            if (http3.status() != 302) {
                throw new MSAuthException("Request to " + http2.getHeader("location") + " returned status " + http1.status());
            }

            Nonsense.LOGGER.info(http3.getHeader("location"));



        } catch (Exception exception) {
            this.errorCallback.onError(exception);
        }
    }

    @Override
    void finish() {

    }

    private List<Cookie> parseCookies() throws IOException {
        List<Cookie> cookies = new ArrayList<>();
        Scanner scanner = new Scanner(this.file);
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] parts = data.split("\t");

            if (parts.length < 7) {
                continue;
            }

            String name = parts[5].trim();
            String value = parts[6].trim().replace("\r", "");
            String domain = parts[0].trim();
            String path = parts[2].trim();
            boolean secure = parts[3].trim().equalsIgnoreCase("true");
            double expires = Double.parseDouble(parts[4].trim());

            cookies.add(new Cookie(name, value, domain, path, secure, expires));
        }

        return cookies;
    }

    private String buildCookieHeader(List<Cookie> cookies) {
        return String.join("; ", cookies.stream().collect(
                        ArrayList::new,
                        (strings, cookie) -> strings.add(String.format("%s=%s", cookie.name, cookie.value)),
                        (BiConsumer<List<String>, List<String>>) List::addAll));
    }

    private static class Cookie {
        public final String name;
        public final String value;
        public final String domain;
        public final String path;
        public final boolean secure;
        public final double expires;

        public Cookie(String name, String value, String domain, String path, boolean secure, double expires) {
            this.name = name;
            this.value = value;
            this.domain = domain;
            this.path = path;
            this.secure = secure;
            this.expires = expires;
        }
    }
}
