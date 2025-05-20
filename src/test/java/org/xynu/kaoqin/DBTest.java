package org.xynu.kaoqin;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBTest {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/zxyf?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "123456";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("连接成功！");
        }
    }
}