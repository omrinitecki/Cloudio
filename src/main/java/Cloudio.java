import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;

public class Cloudio extends HttpServlet
 {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
        resp.getWriter().print("Hello from Java!\n");
    }

    public static void main(String[] args) throws Exception
	{
        Server server = new CloudioServer(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new Cloudio()),"/*");
        server.start();
        server.join();   
    }
}