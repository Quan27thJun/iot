package Controller;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

@WebServlet(
   urlPatterns = {"/SenSor_Data"},
   loadOnStartup = 1
)

public class Sensor_Data extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MqttClient mqttClient;
    
    public String getDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    public void init() throws ServletException {
        super.init();
        System.out.print("Hello");
        String brokerUrl = "tcp://localhost:1883"; 
        String clientId = MqttClient.generateClientId();
        try {
           this.mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
           MqttConnectOptions connOpts = new MqttConnectOptions();
           this.mqttClient.connect(connOpts);
           
           this.mqttClient.setCallback(new CallBack_Sensor(this));
           this.mqttClient.subscribe("topic");
           this.mqttClient.subscribe("stateDevice");
//           this.mqttClient.connect(connOpts);
        } catch (MqttException e) {
           e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        String action = request.getParameter("action");
        if(action.equals("getData")){
            Gson gson = new Gson();
            String a = gson.toJson(new SensorDAO().getADataSensor());
            response.getWriter().write(a);
        }
        if(action.equals("postData")){
            float nhiptim = Float.parseFloat(request.getParameter("nhiptim"));
            float oxy = Float.parseFloat(request.getParameter("oxy"));
            float cothe = Float.parseFloat(request.getParameter("cothe"));
            float nhietdo = Float.parseFloat(request.getParameter("nhietdo"));
            float doam = Float.parseFloat(request.getParameter("doam"));
            
            
            try {
                String brokerUrl = "tcp://localhost:1883";
                String clientId = "MyMqttSubscriber";
                MqttClient mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
                mqttClient.connect();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("nhiptim", nhiptim);
                jsonObject.addProperty("oxy", oxy);
                jsonObject.addProperty("cothe", cothe);
                jsonObject.addProperty("nhietdo", nhietdo);
                jsonObject.addProperty("doam", doam);
                
                String jsonString = jsonObject.toString();
//                try {
//                    MqttMessage message = new MqttMessage(jsonString.getBytes());
//                    mqttClient.publish("postData", message);
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
                
            } catch (MqttException var7) {
                var7.printStackTrace();
            }
            System.out.println(nhietdo);
        }
        if(action.equals("state")){
            String data = request.getParameter("data");
            try {
                String brokerUrl = "tcp://localhost:1883";
                String clientId = "MyMqttSubscriber";
                MqttClient mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
                mqttClient.connect();
//                try {
//                    String topic = "state";
//                    MqttMessage message = new MqttMessage(data.getBytes());
//                    message.setQos(0);
//                    mqttClient.publish(topic, message);
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
                
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        if(action.equals("getAllDataSensor")){
            String page = request.getParameter("page");
            Gson gson = new Gson();
            String a = gson.toJson(new SensorDAO().getAll(Integer.parseInt(page), 10));
            response.getWriter().write(a);
        }
        if(action.equals("search")){
            String start = request.getParameter("start");
            String end = request.getParameter("end");
            String colume = request.getParameter("column");
            String sort_type = request.getParameter("sort_type");
            int currentIndex = Integer.parseInt(request.getParameter("index"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/json");
            String search_type = request.getParameter("search_type");
            String key = request.getParameter("keyword");
            System.out.print("abc");
            List<Sensor> list = new ArrayList();

            try {
                list = new SensorDAO().searchData(currentIndex, start, end, colume, sort_type, key, search_type);
            } catch (SQLException ex) {
                Logger.getLogger(Sensor_Data.class.getName()).log(Level.SEVERE, null, ex);
            }

            Gson gson = new Gson();
            String a = gson.toJson(list);
            response.getWriter().write(a);
        }

}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//       this.doGet(request, response);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        String control_device = request.getParameter("data");
        try {
                String brokerUrl = "tcp://localhost:1883";
                String clientId = "MyMqttSubscriber";
                MqttClient mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
                mqttClient.connect();

                
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
