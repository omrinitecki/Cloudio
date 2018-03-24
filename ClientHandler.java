import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable
{

    private Socket clientSocket;

    ClientHandler(Socket client)
    {
        this.clientSocket = client;
    }

    @Override
    public void run()
    {
        try
        {
            boolean check = true;
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (check)
            {
                String clientSelection;

                if ( (clientSelection = in.readLine()) != null )
                {
                    switch (clientSelection)
                    {
                        case "1":
                            receiveFile();
                            break;

                        case "2":
                            String outGoingFileName;
                            while ((outGoingFileName = in.readLine()) != null)
                            {
                                sendFile(outGoingFileName);
                            }

                            break;

                        case "3":
                            check = false;
                            in.close();
                            System.out.println("Bye!");
                            break;

                        default:
                            System.out.println("Incorrect command received.");
                            break;
                    }
                }
            }
        }

        catch (IOException ex)
        {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void receiveFile()
    {
        try
        {
            int bytesRead;

            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());

            String fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream(("C:\\Users\\User\\Desktop\\" + fileName));
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];

            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
            {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            //clientData.close();

            System.out.println("File " + fileName + " received from client.");
        }

        catch (IOException ex)
        {
            System.err.println("Client error. Connection closed.");
        }
    }

    public void sendFile(String fileName)
    {
        try
        {
            //handle file read
            File myFile = new File(fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream os = clientSocket.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File " + fileName + " sent to client.");
        }

        catch (Exception e)
        {
            System.err.println("File does not exist!");
        }
    }
}