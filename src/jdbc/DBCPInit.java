package jdbc;

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.sql.DriverManager;

public class DBCPInit extends HttpServlet {
    public void init() throws ServletException {
        loadJDBCDriver();
        initConnectionPool();
    }

    private void loadJDBCDriver() {
        try {
            //1. 커넥션 풀이 내부에서 사용할 JDBC 드라이버를 로드한다.
            Class.forName("com.mysql.jdbc.Driver");
        }catch (ClassNotFoundException ex) {
            throw new RuntimeException("fail to load JDBC Driver", ex);
        }
    }

    private void initConnectionPool() {
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/chap14?" +
                    "useUnicode=true&characterEncoding=utf8";
            String dbUser = "rlawnsdud05";
            String dbPass = "Rlawnsdud1@";

            //2. 커넥션 풀이 새로운 커넥션을 생성할 때 사용할 커넥션 팩토리를 생성한다.
            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(jdbcUrl, dbUser, dbPass);

            //3. PoolableConnection을 생성하는 팩토리를 생성한다. DBCP는 커넥션 풀에 커넥션을 보관할 때 PoolableConnection을 사용한다.
            //이 클래스는 내부적으로 실제 커넥션을 담고 있으며, 커넥션 풀을 관리하는데 필요한 기능을 추가로 제공한다.
            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
            poolableConnectionFactory.setValidationQuery("select 1");

            //4. 커넥션 풀의 설정 정보를 생성한다.
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setTimeBetweenEvictionRunsMillis(100L * 60L * 5L);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMinIdle(4);
            poolConfig.setMaxTotal(50);

            //5. 커넥션 풀을 생성한다.
            GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory, poolConfig);
            poolableConnectionFactory.setPool(connectionPool);

            //6. 커넥션 풀을 제공하는 JDBC 드라이버를 등록한다.
            Class.forName("org.apache.commons.dbcp2.PoolingDriver");

            //7. 커넥션 풀 드라이버에 생성한 커넥션 풀을 등록한다.
            PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp");
            driver.registerPool("chap14", connectionPool);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
