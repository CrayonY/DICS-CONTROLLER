package com.ucd.server.trapswapApi.ManagerApi;

import com.ucd.server.trapswapApi.connection.Connection;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServicesApiTest {

    @Test
    public void getAllServices() {
        Connection client = new Connection("http://10.28.3.51:8180/api");
        try {
            UserApi.login(client, "admin", "admin");
           // String result = ServicesApi.getAllServices(client,1,"true","ZOOKEEPER");
            String result = ServicesApi.getAllServices(client);
            System.out.print(result);
            UserApi.logout(client);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}