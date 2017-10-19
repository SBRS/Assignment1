package chat.unitec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread
{
	DataInputStream dis;
	DataOutputStream dos;
	Socket remoteClient;	
	ServerController serverController;
	ArrayList<ServerThread> connectedClients;
	String userName;
	
	public ServerThread(Socket remoteClient, ServerController serverController, ArrayList<ServerThread> connectedClients)
	{
		this.userName = "";
		this.remoteClient = remoteClient;
		this.connectedClients = connectedClients;
		try {
			this.dis = new DataInputStream(remoteClient.getInputStream());
			this.dos = new DataOutputStream(remoteClient.getOutputStream());
			this.serverController = serverController;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void run()
	{
		while(true)
		{
			try {
				int mesgType = dis.readInt();
				
				switch(mesgType)
				{
					case ServerConstants.CHAT_MESSAGE:
						String data = dis.readUTF();
						serverController.getTextArea().appendText(remoteClient.getInetAddress()+":"+remoteClient.getPort()+"("+userName+")"+">"+data+"\n");
						
						for(ServerThread otherClient: connectedClients)
						{
							if(otherClient.equals(this))
							{
								otherClient.getDos().writeInt(ServerConstants.CHAT_BROADCAST);
								otherClient.getDos().writeUTF("Me : " + data);
							}
							else
							{
								otherClient.getDos().writeInt(ServerConstants.CHAT_BROADCAST);
								otherClient.getDos().writeUTF(userName + " : " + data);
							}
						}
						
						break;
					case ServerConstants.REGISTER_CLIENT:
						String name = dis.readUTF();
						serverController.getTextArea().appendText(remoteClient.getInetAddress()+":"+remoteClient.getPort()+"("+name+")" + " has joined the chat" + "\n");
						userName = name;


						for(ServerThread client: connectedClients)
						{
							for(ServerThread clientName: connectedClients) 
							{
							client.getDos().writeInt(ServerConstants.REGISTER_BROADCAST);
							client.getDos().writeUTF(clientName.getUserName());
							}
						}

						break;
					case ServerConstants.PRIVATE_MESSAGE:
						String data1 = dis.readUTF();
						serverController.getTextArea().appendText(remoteClient.getInetAddress()+":"+remoteClient.getPort()+"("+userName+")"+">"+data1+"\n");
						
						String[] str = data1.split("\\s+");
						String name1 = str[0].substring(1);

						for(ServerThread otherClient: connectedClients)
						{
							if(otherClient.userName.equals(name1))
							{
								otherClient.getDos().writeInt(ServerConstants.CHAT_BROADCAST);
								otherClient.getDos().writeUTF("Private message from " + userName + " : " + data1);
							}
							else if(otherClient.equals(this))
							{
								otherClient.getDos().writeInt(ServerConstants.CHAT_BROADCAST);
								otherClient.getDos().writeUTF("My private message : " + data1);
							}
						}
						
						break;
					case ServerConstants.CANVAS_BROADCAST:
						String canvasChange = dis.readUTF();
						
						for(ServerThread otherClient: connectedClients)
						{
							if(!otherClient.equals(this))
							{
								otherClient.getDos().writeInt(ServerConstants.CANVAS_BROADCAST);
								otherClient.getDos().writeUTF(canvasChange);
							}
						}
						break;
					case ServerConstants.IMAGE_BROADCAST:
						int w = dis.readInt();
						int h = dis.readInt();
						int length = dis.readInt();
						byte[] pixels = new byte[w*h*4];
						for(int i=0;i<length;i++){
						    pixels[i]=dis.readByte();
						}
						for(ServerThread otherClient: connectedClients)
						{
							if(!otherClient.equals(this))
							{
								otherClient.getDos().writeInt(ServerConstants.IMAGE_BROADCAST);
								otherClient.getDos().writeInt(w);
								otherClient.getDos().writeInt(h);
								otherClient.getDos().writeInt(length);
			        				for(int i=0;i<length;i++)
			        				{
			        				otherClient.getDos().writeByte(pixels[i]);
			        				}
							}
						}
						break;
					case ServerConstants.CLEAR_BROADCAST:						
						for(ServerThread otherClient: connectedClients)
						{
							if(!otherClient.equals(this))
							{
								otherClient.getDos().writeInt(ServerConstants.CLEAR_BROADCAST);
							}
						}
						break;
				}				
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public String getUserName()
	{
		return userName;
	}
}
