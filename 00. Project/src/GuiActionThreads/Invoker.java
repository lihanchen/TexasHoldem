package GuiActionThreads;


import java.io.*;

public class Invoker extends Thread {
		
	 private int exec(Class<?> klass) throws IOException, InterruptedException {
		 
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = klass.getCanonicalName();
		
		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
		
		Process p = builder.start();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String line;
		while ( (line = br.readLine()) != null) {
		   System.out.println("\tINVOKED_PROC: "+line);
		}
		
		p.waitFor();
		return p.exitValue();
	}	
}