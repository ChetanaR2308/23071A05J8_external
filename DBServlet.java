import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class DBServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT password, login_count FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            if (rs.next()) {
                String correctPassword = rs.getString("password");
                int count = rs.getInt("login_count");

                if (password.equals(correctPassword)) {
                    // Update login count
                    PreparedStatement update = conn.prepareStatement("UPDATE users SET login_count = login_count + 1 WHERE username = ?");
                    update.setString(1, username);
                    update.executeUpdate();

                    out.println("<h2>Welcome, " + username + "!</h2>");
                    out.println("<p>You have logged in " + (count + 1) + " times.</p>");
                } else {
                    out.println("<p>Invalid password.</p>");
                }
            } else {
                out.println("<p>User not found.</p>");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
