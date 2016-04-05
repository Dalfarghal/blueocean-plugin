package io.jenkins.blueocean;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.security.csrf.CrumbExclusion;

/**
 * This class forces the Blueocean API to require json for POSTs so that we do not need a crumb.
 * @author Ivan Meredith
 */
@Extension
public class APICrumbExclusion extends CrumbExclusion{
    @Override
    public boolean process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        String pathInfo = httpServletRequest.getPathInfo();

        for (RootRoutable r : ExtensionList.lookup(RootRoutable.class)) {
            String path = getExclusionPath(r.getUrlName());
            if (pathInfo != null && pathInfo.startsWith(path)) {
                if(httpServletRequest.getHeader("Content-Type").contains("application/json")) {
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                    return true;
                } else {
                    return false;
                }

            }
        }

        return false;

    }

    public String getExclusionPath(String route) {
        return "/blue/" + route + "/";
    }

}
