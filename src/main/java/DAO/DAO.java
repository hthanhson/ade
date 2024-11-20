package DAO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.sql.*;
import java.util.*;
import java.io.*;

public class DAO {

    protected Connection con;
    public DAO() throws IOException, ClassNotFoundException, SQLException {
        Properties prop = new Properties();
        prop.load(DAO.class.getResourceAsStream("/database.properties"));
        String url = prop.getProperty("jdbc.url");
        String user = prop.getProperty("jdbc.username");
        String password = prop.getProperty("jdbc.password","");
        String driver = prop.getProperty("jdbc.driverClassName");
        Class.forName(driver);
        con = DriverManager.getConnection(url, user,password);
    }


}
