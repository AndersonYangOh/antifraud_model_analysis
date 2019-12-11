package Models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Config {
	public static String jdbc = null;
	public static String user = null;
	public static String pass = null;
	public static String model = null;
	public static final String _1DAY = "1";
	public static final String _5DAY = "5";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Config().initConfit();
	}
	
	
	public static void initConfit(){
//		System.out.println(Config.class.getResource(""));
		String contents = readToString("config.ini");
//		System.out.println(contents);
		
		String[]lines = contents.split("\r\n");
		for(int i=0;i < lines.length;i++){
			String[]temp = lines[i].trim().split("=");
			if(temp.length==2&&temp[0].equals("jdbc")){
				jdbc = temp[1];
				System.out.println(jdbc);
			}
			if(temp.length==2&&temp[0].equals("user")){
				user = temp[1];
				System.out.println(user);
			}
			if(temp.length==2&&temp[0].equals("pass")){
				pass = temp[1];
				System.out.println(pass);
			}
			if(temp.length==2&&temp[0].equals("model")){
				model = temp[1];
				System.out.println(model);
			}
		}
	}
	
	public static String readToString(String fileName) {  
        String encoding = "GBk";  
        File file = new File(fileName);  
        Long filelength = file.length();  
        byte[] filecontent = new byte[filelength.intValue()];  
        try {  
            FileInputStream in = new FileInputStream(file);  
            in.read(filecontent);  
            in.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        try {  
            return new String(filecontent, encoding);  
        } catch (UnsupportedEncodingException e) {  
            System.err.println("The OS does not support " + encoding);  
            e.printStackTrace();  
            return null;  
        }  
    }
	

}
