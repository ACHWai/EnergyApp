import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.*;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class nglogin implements HttpHandler {
        public void handle (HttpExchange exchange) throws IOException {
                System.out.println("doing login thing");
                String requestBody = IOUtils.toString(exchange.getRequestBody());
                System.out.println(requestBody);

                JSONObject requestJson = new JSONObject(requestBody);
                String id = (requestJson.getString("id")).toLowerCase();
                String password = requestJson.getString("password");
                String usertype = requestJson.getString("usertype");

                Headers responseHeaders = exchange.getResponseHeaders();
                responseHeaders.set("Content-Type", "text/plain");
                OutputStream responseBody = exchange.getResponseBody();

                System.out.println("set up worked");

                if (id == null || password == null || usertype == null) {
                        exchange.sendResponseHeaders(403, 0);
                        responseBody.close();
                        System.out.println("input failure, no nulls please");
                        System.out.println("output = 403");
                        return;
                }

                //defence against weird stuff
                if (password.contains(" ") || password.contains(";") || password.contains("'") || password.contains("`")) {
                        exchange.sendResponseHeaders(403, 0);
                        System.out.println("no using weird characters lmao");
                        responseBody.close();
                        return;
                }
                System.out.println("working so far");
                int account_id = -1;
                String p = "";
                try {
                        if (usertype.equals("user")) {
                                p = "select * from user_accounts where user_id = '";
                                p += id;
                                p += "' and password = '";
                                p += password;
                                p += "'";
                                PreparedStatement stmt = ngdatabase.conn.prepareStatement(p);
                                ResultSet rs = stmt.executeQuery();
                                if (rs.next())
                                        account_id = rs.getInt("account_id");

                                rs.close();
                        }
                        if (usertype.equals("bin")) {
                                p = "select * from bin_accounts where bin_id = '";
                                p += id;
                                p += "' and password = '";
                                p += password;
                                p += "'";
                                PreparedStatement stmt = ngdatabase.conn.prepareStatement(p);
                                ResultSet rs = stmt.executeQuery();
                                if (rs.next())
                                        account_id = rs.getInt("account_id");

                                rs.close();
                        }
                        if (usertype.equals("admin")) {
                                p = "select * from admin_accounts where admin_id = '";
                                p += id;
                                p += "' and password = '";
                                p += password;
                                p += "'";
                                PreparedStatement stmt = ngdatabase.conn.prepareStatement(p);
                                ResultSet rs = stmt.executeQuery();
                                if (rs.next())
                                        account_id = rs.getInt("account_id");

                                rs.close();
                        }
                        System.out.println("got data");
                } catch (SQLException e){
                        exchange.sendResponseHeaders(200, 0);
                        String response = "{\"error\" : \"" + e.getSQLState() +  "\"}";
                        System.out.println("output:");
                        System.out.println(response);
                        responseBody.write(response.getBytes());
                        responseBody.close();
                        return;
                }

                if (account_id == -1) {
                        System.out.println("too lazy to do stuff, failure.");
                        exchange.sendResponseHeaders(200, 0);
                        String response = "{\"results\" : \" invalid info\"}";
                        System.out.println("output:");
                        System.out.println(response);
                        responseBody.write(response.getBytes());
                        responseBody.close();
                        return;
                }

                //do session stuff ig
                //send session id back
                System.out.println("sending stuff back now");
                SessionManager SM = new SessionManager();
                String SessionID = SM.RegisterSession(account_id);

                exchange.sendResponseHeaders(200, 0);
                String response = "{\"sessionid\" : \"" + SessionID + "\"}";
                System.out.println("output:");
                System.out.println(response);

                responseBody.write(response.getBytes());
                responseBody.close();
                System.out.println("sent done");

        }
}
