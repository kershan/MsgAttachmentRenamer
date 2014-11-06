package msgattachmentrenamer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.auxilii.msgparser.Message;
import com.auxilii.msgparser.MsgParser;
import com.auxilii.msgparser.attachment.Attachment;
import com.auxilii.msgparser.attachment.FileAttachment;

public class Main 
{
	//Reference: http://auxilii.com/msgparser/
	
	static MsgParser msgParser = new MsgParser();
	
	public static void main(String[] args) 
	{
		System.out.println("Msg Attachment Renamer");
		
		//Set UI to look like system UI
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} 
		catch (UnsupportedLookAndFeelException e) 
		{
			e.printStackTrace();
		}
		
		JFrame frame = new JFrame();
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fileChooser.setMultiSelectionEnabled(true);
		int dialogResult = fileChooser.showOpenDialog(frame);
		
		/*JProgressBar progreeBar = new JProgressBar();
		progreeBar.setVisible(true);*/
		
		//User dialog selection
		switch (dialogResult)
		{
			case JFileChooser.APPROVE_OPTION:
				int success = saveAttachment(fileChooser.getSelectedFiles());
				
				System.out.println(success);
				System.exit(0);
				break;
			default: 
				System.out.println("Exiting");
				System.exit(0);
				break;
		}	
	}
	
	private static int saveAttachment(File [] files)
	{
		int success = 0;
		for (File file : files)
		{
			try
			{
				String filename = file.getName();
				String fileLocation = file.getParent();
				Message msg = msgParser.parseMsg(file);
				
				List<Attachment> atts = msg.getAttachments();
				
				if (atts.size() == 1)
				{
					for (Attachment att : atts) 
					{
						FileAttachment fileAttach = (FileAttachment) att;
						
						//Get extension
						String mime = fileAttach.getMimeTag();
						String extension = mime.substring(mime.indexOf("/") + 1);
						
						//Add extension from attachment to new filename
						String newFilename = filename.substring(0, filename.lastIndexOf(".") + 1);		
						newFilename = newFilename + extension;
						
						//Add new filename to file's current directory
						newFilename = fileLocation + File.separator + newFilename;
						
						//Save attachment 
						FileOutputStream fos = new FileOutputStream(new File(newFilename));
						fos.write(fileAttach.getData());
						fos.close();
						
						System.out.println("Attachement saved");
						
						success++;
					}
				}
				else
				{
					System.out.println("Multiple Attachments, not saved");
				}
			}
			catch (IOException ioe)
			{
				System.out.println("IO Error: " + ioe.getMessage());
			}
		}
		return success;
	}
}
