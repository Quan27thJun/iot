/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controller;

//import Dao.DeviceDAO;
import Dao.SensorDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Model.Sensor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

@WebServlet(
   loadOnStartup = 1
)
public class Control_DeviceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MqttClient mqttClient;
    private String clientId = MqttClient.generateClientId();
    
    public void init() throws ServletException {
        super.init();
        System.out.print("Hello");
        String brokerUrl = "tcp://localhost:1883";
        String clientId = "MyMqttSubscriber";

        try {
            this.mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
//            this.mqttClient.setCallback(new CallBack_Sensor(this));
            this.mqttClient.connect(connOpts);

            if (this.mqttClient.isConnected()) {
                System.out.println("Kết nối MQTT thành công");
            } else {
                System.out.println("Kết nối MQTT thất bại");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        String control_device = request.getParameter("data");
        System.out.println(control_device);
        try {
                String brokerUrl = "tcp://localhost:1883";
                MqttClient mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
                mqttClient.connect();
                try {
                    String topic = "control";
                    MqttMessage message = new MqttMessage(control_device.getBytes());
                    message.setQos(0);
                    mqttClient.publish(topic, message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
