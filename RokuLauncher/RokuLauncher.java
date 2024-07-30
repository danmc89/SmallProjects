package RokuLauncher;

import java.io.IOException;
import java.lang.ProcessHandle;

import javax.swing.SwingUtilities;

public class RokuLauncher {
	private static Process rokuChannelProcess = null;
	
	public static void executeProcess(String ...args)
	{
		try {
			destroyRunningProcess();
			
			ProcessBuilder pb = new ProcessBuilder(args);
			rokuChannelProcess = pb.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean destroyRunningProcess()
	{
		if(rokuChannelProcess != null)
		{
			rokuChannelProcess.destroy();
			rokuChannelProcess.descendants().forEach(ProcessHandle::destroy);
			return true;
		}
		return false;
	}
	
	public static void launchChannel(String filename)
	{
		LoggingMessages.printCurrentPath();
		executeProcess(RokuProperties.BROWSER_PATH.getPropertiesValue(), filename);
	}
	
	public static void closeRokuVideo()
	{
		destroyRunningProcess();
	}
	
	public static void main(String [] args)
	{
		SwingUtilities.invokeLater(() -> {
			 RokuLauncherWindow window = new RokuLauncherWindow();
		        window.setVisible(true);
		});
	}
}
