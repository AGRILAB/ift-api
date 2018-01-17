package fr.gouv.agriculture.ift.helper;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class SpringControllerHelper {

    public static String getExternalURL(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getHeader("host") + request.getContextPath();
    }

    public static void redirect(HttpServletResponse response, String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException ioException) {
            log.warn("Redirect failure", ioException);
        }
    }
}
