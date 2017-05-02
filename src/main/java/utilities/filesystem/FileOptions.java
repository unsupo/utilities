package utilities.filesystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileOptions {
	public static void main(String[] args) throws IOException {
		String path = System.getProperty("user.dir");
		String s = System.getProperty("file.separator");
		String out = path + s + ".." + s + "all_jars", in = path + s + ".." + s
				+ "jars";
		moveAllFiles(in, out);
	
	}
	
	public static void downloadFile(String link, String path) throws IOException{
		URL website = new URL(link);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(path);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
	
	
	public static void deleteDirectory(String path) throws IOException{
//		FileUtils.deleteDirectory(new File(path));
		Files.delete(new File(path).toPath());
	}
	
	public static void runProcess(String process, String...dir) throws IOException {
		String udir = System.getProperty("user.dir");
		if(dir != null && dir.length != 0)
			udir = dir[0];
	    	List<String> evn = System.getenv().entrySet().stream().map(a -> a.getKey() + "=" + a.getValue()).collect(Collectors.toList());
	    	Process p = Runtime.getRuntime().exec(process,evn.toArray(new String[evn.size()]),new File(udir));
	    	new Thread(new Runnable() {
			public void run() {
		 	   BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		 	   BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		 	   String line = null;

		 	   try {
				while ((line = input.readLine()) != null)
				    System.out.println(line);
				while ((line = error.readLine()) != null)
				    System.out.println(line);
		 	   } catch (IOException e) {
				e.printStackTrace();
			    }
			}
		    }).start();

		    p.waitFor();
	}
	
	
	public static void writeToFileOverWrite(String filePath, String contents) throws IOException{
		FileOutputStream out = new FileOutputStream(filePath);
		out.write(contents.getBytes());
		out.close();
	}public static BufferedWriter writeToFileAppend(String file, String contents) throws IOException{
		BufferedWriter bw = null;
		try{bw = new BufferedWriter(new FileWriter(file, true));}
		catch(FileNotFoundException fnfe){ 
			String[] split = file.split("\\\\");
			new File(file.substring(0, file.indexOf(split[split.length-1]))).mkdirs();
			return writeToFileAppend(file, contents);
		}
		bw.write(contents);
		bw.newLine();
		bw.flush();
		return bw;
	}
	
	public static String readFileIntoString(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line, result = "";
		while((line = br.readLine())!=null)
			result+=line+"\n";
		br.close();
		return result;
	}public static List<String> readFileIntoListString(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		List<String> result = new ArrayList<>();
		while((line = br.readLine())!=null)
			result.add(line);
		br.close();
		return result;
	}


	public static List<File> getAllFilesRegex(String path, String regex) throws IOException {
		List<File> files = new ArrayList<>();
		_getAllFiles(path, files);
		return files.stream().filter(a-> Pattern.compile(regex).matcher(a.getName()).find()).collect(Collectors.toList());
	}public static List<File> getAllFilesEndsWith(String path, String endsWith) throws IOException{
		List<File> files = new ArrayList<>();
		_getAllFiles(path,files);
		return files.stream().filter(a->a.getName().endsWith(endsWith)).collect(Collectors.toList());
	}public static List<File> getAllFiles(String path, String contains) throws IOException{
		List<File> files = new ArrayList<>();
		_getAllFiles(path,files);
		return files.stream().filter(a->a.getName().contains(contains)).collect(Collectors.toList());
	}public static List<File> getAllFiles(String path) throws IOException{
		List<File> files = new ArrayList<>();
		_getAllFiles(path,files);
		return files;
	}private static void _getAllFiles(String path,List<File> files) throws IOException{
		if(!Files.isReadable(Paths.get(path))) return;
		for(File f : new File(path).listFiles())
			if(f.isDirectory())
				_getAllFiles(f.getAbsolutePath(), files);
			else
				files.add(f);
	}
	
	public static File findFile(String path, String name) throws IOException {
		Files.walk(Paths.get(path))
				.forEach(
						filePath -> {
							if (Files.isRegularFile(filePath)
									&& filePath.getFileName().toString()
											.contains(name)) {
								System.out.println(filePath);
							}
						});
		return null;
	}

	
	public static void moveAllFiles(String in, String out) throws IOException {
		String s = System.getProperty("file.separator");
		for (File f : new File(in).listFiles())
			if (f.isFile() && f.getAbsolutePath().endsWith(".jar"))
				copyFile(f, new File(out + s + f.getName()));
			else if (f.isDirectory())
				moveAllFiles(f.getAbsolutePath(), out);
	}

	private static void copyFile(File source, File dest) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	public static void renameAllFiles(String in) {
		for (File f : new File(in).listFiles())
			if (f.isFile())
				f.renameTo(new File(f.getAbsolutePath() + ".png"));
	}
}
