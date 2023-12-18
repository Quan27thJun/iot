package Controller;
import Dao.SensorDAO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

class CallBack_Sensor implements MqttCallback{

    private final Sensor_Data test;
    
    CallBack_Sensor(Sensor_Data var1) {
      this.test = var1;
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("K\u1ebft n\u1ed1i MQTT \u0111\u00e3 m\u1ea5t");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
    try {
        System.out.println("Nhận được dữ liệu MQTT từ chủ đề: " + topic);
        System.out.println("Nội dung: " + new String(message.getPayload()));

        String result = new String(message.getPayload());
        System.out.println(result);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(result, JsonObject.class);
        if(topic.equals("topic")){
            if (jsonObject.has("bpm") && jsonObject.has("sp") && jsonObject.has("body") && jsonObject.has("temp") && jsonObject.has("humi") ) {
                float bpm = jsonObject.get("bpm").getAsFloat();
                float sp = jsonObject.get("sp").getAsFloat();
                float body = jsonObject.get("body").getAsFloat();
                float temp = jsonObject.get("temp").getAsFloat();
                float humi = jsonObject.get("humi").getAsFloat();
                
                String time = test.getDateTime();
                if(Float.isNaN(temp) && Float.isNaN(humi) && Float.isNaN(bpm) && Float.isNaN(sp) && Float.isNaN(body)){
                    
                }
                else{
                    try {
                        new SensorDAO().insertData(bpm, sp, body, temp, humi, time);
                    } finally {
                        // Đóng tài nguyên nếu cần thiết
                    }
                }
            } else {
                System.out.println("Dữ liệu JSON không hợp lệ");
            }
        }
        } catch (JsonSyntaxException ex) {
            System.err.println("Lỗi xử lý JSON: " + ex.getMessage());
        }
        catch (Throwable ex) {
            Logger.getLogger(CallBack_Sensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
   public void deliveryComplete(IMqttDeliveryToken token) {
   }
}
