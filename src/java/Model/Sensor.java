/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author ADMIN
 */
public class Sensor {
    private int id;
    private float bpm;
    private float sp;
    private float body;
    private float temp;
    private float humi;
    private String time;

    public Sensor(int id, float bpm, float sp, float body, float temp, float humi, String time) {
        this.id = id;
        this.bpm = bpm;
        this.sp = sp;
        this.body = body;
        this.temp = temp;
        this.humi = humi;
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void setBpm(float bpm) {
        this.bpm = bpm;
    }
    
    public void setSp(float sp) {
        this.sp = sp;
    }
    
    public void setBody(float body) {
        this.body = body;
    }
    
    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setHumi(float humi) {
        this.humi = humi;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }
    public float getBpm() {
        return bpm;
    }
    
    public float getSp() {
        return sp;
    }
    
    public float getBody() {
        return body;
    }
    
    public float getTemp() {
        return temp;
    }

    public float getHumi() {
        return humi;
    }

    public String getTime() {
        return time;
    }
    
    
}
