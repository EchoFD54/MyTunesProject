package dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;

public class ConnectionManager {

    private final SQLServerDataSource ds;

    public ConnectionManager() {
        ds = new SQLServerDataSource();
        ds.setDatabaseName("CSe2023b_MyTunes_TeamF");
        ds.setUser("CSe2023b_e_20");
        ds.setPassword("CSe2023bE20#23");
        ds.setServerName("EASV-DB4");
        ds.setTrustServerCertificate(true);
    }

    public Connection getConnection() throws SQLServerException {
        return ds.getConnection();
    }

}
