package RokuLauncher;

import java.io.IOException;
import java.lang.ProcessHandle;
import java.nio.file.Path;
import java.nio.file.Paths;

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
			rokuChannelProcess.descendants().forEach(ProcessHandle::destroy);
			rokuChannelProcess.destroy();
			return true;
		}
		return false;
	}
	
	public static void launchChannel(String filename)
	{
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current absolute path is: " + s);
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
