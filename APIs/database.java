import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import java.sql.Connection;
import java.sql.DriverManager;

public class database {
	//a user called postgres opens a database called energyapp
        public String url = "jdbc:mysql://127.0.0.1/energyapp";
	public String user = "student";
    	public String password = "isfshuyuan";
        public static Connection conn;

        ngdatabase() {

                System.out.println("database init\n");
                try {
                    conn = DriverManager.getConnection(url, user, password);
                    System.out.println("connect done\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
}
