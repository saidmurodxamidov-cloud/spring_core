package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.example.config.AppConfig;

public class JettyStarter {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);

        ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        // ✅ REQUIRED for Swagger static resources
        context.setResourceBase(
                JettyStarter.class
                        .getClassLoader()
                        .getResource(".")
                        .toExternalForm()
        );

        // Root Spring context
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        // ✅ Attach context to Jetty (VERY IMPORTANT)
        context.addEventListener(new ContextLoaderListener(rootContext));

        // DispatcherServlet (child context)
        DispatcherServlet dispatcherServlet =
                new DispatcherServlet(rootContext);

        ServletHolder servletHolder =
                new ServletHolder(dispatcherServlet);
        servletHolder.setInitOrder(1);

        context.addServlet(servletHolder, "/*");

        server.setHandler(context);

        System.out.println("Starting Jetty server on http://localhost:8080");
        server.start();
        server.join();
    }
}
