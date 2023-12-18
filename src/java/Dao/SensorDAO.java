package Dao;

import Dao.DBContext;
import static Dao.DBContext.con;
//import Model.Schedule;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import Model.Sensor;

public class SensorDAO extends DBContext{
    public SensorDAO() {
    }
    
    public boolean checkLogin(String username, String password){
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void insertData(float bpm, float sp, float body, float temp, float humi, String time) throws SQLException, Throwable {
        String sql = "INSERT INTO Sensor ( bpm, sp, body, temp, humi, time) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setFloat(1, bpm);
            ps.setFloat(2, sp);
            ps.setFloat(3, body);
            ps.setFloat(4, temp);
            ps.setFloat(5, humi);
            ps.setString(6, time);
            ps.execute();
        } catch (Exception e) {
        }
    }
    
    public List<Sensor> searchData(int index, String start, String end, String column, String sort_type, String keyword, String search_type) throws SQLException {
        List<Sensor> list = new ArrayList();
        String sql = "SELECT * FROM Sensor" + "WHERE COLUMN_NAME LIKE ? AND time BETWEEN ? AND ?" + "ORDER BY COLUMN_NAME ASC/DESC" + "LIMIT ?, 10;";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, start);
            ps.setString(3, end);
            ps.setInt(4, index);
            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                Float bpm = resultSet.getFloat("bpm");
                Float sp = resultSet.getFloat("sp");
                Float body = resultSet.getFloat("body");
                Float temp = resultSet.getFloat("temp");
                Float humi = resultSet.getFloat("humi");
                Timestamp time = resultSet.getTimestamp("time");
                String time_string = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(time);
                list.add(new Sensor(id, bpm, sp, body, temp, humi,  time_string));
            }
        } catch (Exception e) {
        }
      return list;
   }
    public List<Sensor> getAllData(int index, String start, String end, String column, String sort_type) throws SQLException {
        List<Sensor> list = new ArrayList();
        String sql = "SELECT * FROM sensor WHERE time BETWEEN ? AND ?\n ORDER BY " + column + " " + sort_type + " OFFSET " + index + " ROWS FETCH NEXT 10 ROWS ONLY";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, start);
        preparedStatement.setString(2, end);
        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            int id = resultSet.getInt("id");
            Float bpm = resultSet.getFloat("bpm");
            Float sp = resultSet.getFloat("sp");
            Float body = resultSet.getFloat("body");
            Float temp = resultSet.getFloat("temp");
            Float humi = resultSet.getFloat("humi");
            
            Timestamp time = resultSet.getTimestamp("time");
            String time_string = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(time);
            list.add(new Sensor(id, bpm, sp, body, temp, humi,  time_string));
        }
        return list;
    }

    public ArrayList<Sensor> getAll(){
        ArrayList<Sensor> a = new ArrayList<>();
        String sql = "SELECT * FROM Sensor";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                a.add(new Sensor(rs.getInt("id"),rs.getFloat("bpm"), rs.getFloat("sp"), rs.getFloat("body"), rs.getFloat("temp"), rs.getFloat("humi"),  rs.getString("time")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }
    
    public ArrayList<Sensor> getAll(int page, int limit) {
        ArrayList<Sensor> result = new ArrayList<>();
        String sql = "SELECT * FROM (SELECT ROW_NUMBER() OVER (ORDER BY id) AS RowNum, * FROM Sensor) AS SubQuery WHERE RowNum BETWEEN ? AND ?";
        int offset = (page - 1) * limit;

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, offset + 1);
            ps.setInt(2, offset + limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(new Sensor(rs.getInt("id"), rs.getFloat("bpm"), rs.getFloat("sp"), rs.getFloat("body"),rs.getFloat("temp"), rs.getFloat("humi"),  rs.getString("time")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    
    public Sensor getADataSensor(){
        String sql = "SELECT * FROM Sensor ORDER BY id DESC LIMIT 1";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return new Sensor(rs.getInt("id"), rs.getFloat("bpm"), rs.getFloat("sp"), rs.getFloat("body"),rs.getFloat("temp"), rs.getFloat("humi"),  rs.getString("time"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) {
        System.out.println(new SensorDAO().getADataSensor().getId());
    }
}
