package ua.kas.chat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.PreparedStatement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {

	@FXML TextField tf_login;
	@FXML PasswordField tf_password;
	static String login;
	static String password;
	
	public void login(ActionEvent e) throws SQLException, IOException{
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/freemove", "root", "root");
		
		login = tf_login.getText();
		password = tf_password.getText();
		
		PreparedStatement pstat = (PreparedStatement) conn.prepareStatement("select * from users where user_name=? and password=?");
		pstat.setString(1, login);
		pstat.setString(2, password);
		ResultSet result = pstat.executeQuery();
		
		while(result.next()){
			
			Scene chat = new Scene(FXMLLoader.load(getClass().getResource("Chat.fxml")));
			chat.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage_chat = (Stage) ((Node)e.getSource()).getScene().getWindow();
			stage_chat.setScene(chat);
			stage_chat.show();
		}
	}
}
