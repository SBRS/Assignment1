package chat.unitec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ClientController implements Runnable {
	
	Socket client;
	DataInputStream dis;
	DataOutputStream dos;
	ObservableList<String> names = FXCollections.observableArrayList();
	
	@FXML
	private ListView<String> lvUsers;
	
	@FXML
	private TextArea taChat;
	
	@FXML
	private TextField tfSend;

    @FXML
    private Button btnSend;
    
    @FXML
    private Canvas myCanvas;
    
	@FXML
    protected void initialize()
	{
		final GraphicsContext graphicsContext = myCanvas.getGraphicsContext2D();
//    	Global.getInstance().connected.setCallback(this);
//    	Global.getInstance().connected.send(7, "null", Global.getInstance().userName);
//    	usersList.setItems(listItems);
    	
    		initDraw(graphicsContext);
    	
	    	myCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
	    		new EventHandler<MouseEvent>(){
	
	            @Override
	            public void handle(MouseEvent event) {
	            	graphicsContext.beginPath();
	            	graphicsContext.moveTo(event.getX(), event.getY());
	//            	graphicsContext.setStroke();
	//            	startX = event.getX();
	//            	startY = event.getY();
	//            	if(action.equals("pen")){
	            	graphicsContext.stroke();
	        		try {
	        			dos.writeInt(ServerConstants.CANVAS_BROADCAST);
	        			dos.writeUTF("beginPath%"+event.getX()+"%"+event.getY());
	        			dos.flush();
	        		} catch (IOException e) {
	        			e.printStackTrace();
	        		}
	            	
	//            		Global.getInstance().connected.send(6, "start:" + drawColor+":"+ event.getX() + ":" + event.getY(), Global.getInstance().userName);
	//	        	} else if(action.equals("zoom")){
	//	        		myCanvas.setScaleX(2);
	//	        		myCanvas.setScaleY(2);
	//	        	} else if(action.equals("zoom-out")){
	//	        		myCanvas.setScaleX(1);
	//	        		myCanvas.setScaleY(1);
	//	        	} else if(action.equals("rotate")){
	//	        		
	//	        	}
	            }
	        });
	    	
	    	myCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
	    		new EventHandler<MouseEvent>(){
	
	            @Override
	            public void handle(MouseEvent event) {
	            	graphicsContext.lineTo(event.getX(), event.getY());
	//            	graphicsContext.setStroke(drawColor);
	//            	if(action.equals("pen")){
	//            		Global.getInstance().connected.send(6, "update:" + drawColor+":"+ event.getX() + ":" + event.getY(), Global.getInstance().userName);
		        graphicsContext.stroke();
	        		try {
	        			dos.writeInt(ServerConstants.CANVAS_BROADCAST);
	        			dos.writeUTF("lineTo%"+event.getX()+"%"+event.getY());
	        			dos.flush();
	        		} catch (IOException e) {
	        			e.printStackTrace();
	        		}
	//            	} 
	            }
	        });
	
	    	myCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, 
	                new EventHandler<MouseEvent>(){
	
	            @Override
	            public void handle(MouseEvent event) {
	//            	Global.getInstance().connected.send(6, "released:" + drawColor+":"+ startX + ":" + startY + ":" + action + ":" + event.getX() + ":" + event.getY(), Global.getInstance().userName);
	//            	double endX = event.getX()-startX;
	//            	endX =  endX<0?endX*-1:endX;
	//            	double endY = event.getY()-startY;
	//            	endY = endY<0?endY*-1:endY;
	//            	if(action.equals("oval")){
	//            		graphicsContext.strokeOval(event.getX(), event.getY(), endX , endY);
	//            	} else if(action.equals("rect")){
	//            		graphicsContext.strokeRect(event.getX(), event.getY(), endX , endY);
	//            	} else if(action.equals("line")){
	//            		graphicsContext.strokeLine(startX, startY, event.getX(), event.getY());
	//            	} 
	            }
	        });
	    	
	//    	colorToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
	//        {
	//	        @Override
	//	        public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1)
	//            {
	//	        	RadioButton color = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
	//	        	drawColor = drawColor.valueOf(color.getStyleClass().toString().split(" ")[1]);
	//            }
	//        });
	    	
	//    	actionToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
	//        {
	//	        @Override
	//	        public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1)
	//            {
	//	        	RadioButton actionRd = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button
	//	        	action = actionRd.getStyleClass().toString().split(" ")[1];
	//            }
	//        });
    }
    
    @FXML
    void btnSendOnActionHandler(ActionEvent event) {
		try {
			if(tfSend.getText().charAt(0) == '@') {
				dos.writeInt(ServerConstants.PRIVATE_MESSAGE);
			} else {
				dos.writeInt(ServerConstants.CHAT_MESSAGE);
			}
			dos.writeUTF(tfSend.getText());
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		tfSend.clear();
    }
    
    @FXML
    void lvUsersOnMouseClickedHandler() {
    	String name = lvUsers.getSelectionModel().getSelectedItem();
    	tfSend.setText("@" + name + " ");
    }
    
    public ClientController() {
		try {
			client = new Socket("localhost", 5000);
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			
			dos.writeInt(ServerConstants.REGISTER_CLIENT);
			dos.writeUTF(ClientMain.getName());
			dos.flush();
			
			Thread clientThread = new Thread(this);
			clientThread.start();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    }	

	@Override
	public void run() {		
		while(true)
		{
			try {
				int messageType = dis.readInt();

				switch(messageType)
				{
					case ServerConstants.CHAT_BROADCAST:
						taChat.appendText(dis.readUTF()+"\n");
						break;
					case ServerConstants.REGISTER_BROADCAST:
						String name = dis.readUTF();
						Platform.runLater(new Runnable() {
							@Override public void run() {
								if(!names.contains(name))
								{
									taChat.appendText(name + " has joined the chat"+"\n");
									names.add(name);
								}
								lvUsers.setItems(names);
							}
						});
						break;
					case ServerConstants.CANVAS_BROADCAST:
						GraphicsContext graphicsContext = myCanvas.getGraphicsContext2D();
						String[] canvasChange = dis.readUTF().split("%");
		            		if(canvasChange[0].equals("beginPath"))
		            		{
		            			graphicsContext.beginPath();
		        	    			graphicsContext.moveTo(Double.parseDouble(canvasChange[1]), Double.parseDouble(canvasChange[2]));
		        	    			graphicsContext.stroke();
		            		}
		            		else if(canvasChange[0].equals("lineTo"))
		            		{
		            			graphicsContext.lineTo(Double.parseDouble(canvasChange[1]), Double.parseDouble(canvasChange[2]));
		            			graphicsContext.stroke();
		            		} 
						break;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
    private void initDraw(GraphicsContext gc){
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        
//        gc.setFill(Color.LIGHTGRAY);
//        gc.setStroke(Color.BLACK);
//        gc.setLineWidth(5);
//
//        gc.fill();
//        gc.strokeRect(
//                0,              //x of the upper left corner
//                0,              //y of the upper left corner
//                canvasWidth,    //width of the rectangle
//                canvasHeight);  //height of the rectangle
        
        gc.setFill(Color.RED);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        
    }
}
