package io.fabric8.launcher.web.filters;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;

;**

@WebFilter(urlPatterns = "/index.html", asyncSupported = true)
public class InjectConfigFilter implements Filter {

    private String settings = "{}";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("META-INF/settings-tmpl.json")) {
            String settingsTemplate = IOUtils.toString(stream, Charset.defaultCharset());
            StringSubstitutor substitutor = new StringSubstitutor();
            substitutor.setVariableResolver(key -> System.getenv().getOrDefault(key, ""));
            this.settings = StringEscapeUtils.escapeJson(substitutor.replace(settingsTemplate)
                                                                 .replace("\n",""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        InputStream stream = request.getServletContext().getResourceAsStream("index.html");
        String indexHtml = IOUtils.toString(stream, Charset.defaultCharset());
        String newIndex = indexHtml.replace("$GLOBAL_CONFIG_JSON_STRING", settings);
        response.setContentType("text/html");
        try (PrintWriter writer = response.getWriter()) {
            writer.println(newIndex);
            writer.flush();
        }
    }
}
