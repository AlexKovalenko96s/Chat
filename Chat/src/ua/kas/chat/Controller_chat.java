package ua.kas.chat;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class Controller_chat implements Initializable {

	@FXML ComboBox<String> with;
	@FXML TextArea message;
	@FXML ListView<String> list;
	static String who_write;
	static String who_read;
	static int click;
	String M;
	static int id;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/freemove", "root", "root");
			Statement rstat = conn.createStatement();
			ResultSet result = rstat.executeQuery("select user_name from users");
			
			while(result.next()){
				with.getItems().addAll(result.getString("user_name"));
			}
			
		} catch (SQLException e) {}
	}
	
	public void send(ActionEvent e) throws SQLException {
		
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/freemove", "root", "root");
			Statement rstat = conn.createStatement();
			ResultSet result = rstat.executeQuery("select * from message");
		
			while(result.next()){
				
				who_write = result.getString("who_write");
				who_read = result.getString("who_read");
				
				if(who_write.equals(Controller.login) && who_read.equals(with.getEditor().getText())){
					
					click = result.getInt("click");
					M = result.getString("message");
					id = result.getInt("id");
					click++;
					
					java.sql.PreparedStatement myStmt = conn
							.prepareStatement("UPDATE message SET message=?, click=? WHERE id=?");
					myStmt.setString(1, M + "&" + click + message.getText() + click + "&");
					myStmt.setInt(2, click);
					myStmt.setInt(3, id);
					myStmt.executeUpdate();
					refresh(e);
				}
			}	
		} catch (Exception ex) {
			
			conn = DriverManager.getConnection("jdbc:mysql://localhost/freemove", "root", "root");
			java.sql.PreparedStatement pstat = conn.prepareStatement("insert into message (who_write,who_read,message, click) values (?,?,?,?)");
			pstat.setString(1, Controller.login);
			pstat.setString(2, with.getEditor().getText());
			pstat.setString(3, "&"+ 1 + message.getText() + 1 + "&");
			pstat.setInt(4, 1);
			pstat.executeUpdate();
			refresh(e);
		}	
		message.setText("");
	}
	
	public void refresh(ActionEvent e) throws SQLException{
		list.getItems().clear();
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/freemove", "root", "root");
		ResultSet myRs = null;
		java.sql.PreparedStatement myStmt = conn
				.prepareStatement("select * from message where who_write=? and who_read=?");
		myStmt.setString(1, Controller.login);
		myStmt.setString(2, with.getEditor().getText());
		myRs = myStmt.executeQuery();
		while (myRs.next()) {
			
			for(int i = 1; i<=myRs.getInt("click"); i++){
		
				String message =myRs.getString("message");
				list.getItems().addAll("      " + myRs.getString("who_write") + "\n"
						+ message.substring((message.indexOf("&"+i))+2, message.indexOf(i+"&")) + "\n" + "\n");
					
				try{
					
					String write = Controller.login;
					String read = with.getEditor().getText();
					
					ResultSet myRs_second = null;
					java.sql.PreparedStatement myStmt_second = conn
							.prepareStatement("select * from message where who_write=? and who_read=?");
					myStmt_second.setString(1, read);
					myStmt_second.setString(2, write);
					myRs_second = myStmt_second.executeQuery();
					
					while (myRs_second.next()) {
						
						String message_second =myRs_second.getString("message");
						
						list.getItems().addAll("      " + myRs_second.getString("who_write") + "\n"
								+ message_second.substring((message_second.indexOf("&"+i))+2, message_second.indexOf((i+"&"))) + "\n" + "\n");
					}
				}catch(Exception ex){}
			}	
		}
		message.setText("");
	}
}
