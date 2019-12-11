package Models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.JTextField;

public class FileHandler {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new FileHandler().importModel();
	}

	public int export(JTable table,JTextField first,JTextField score3,JTextField score2,JTextField score1){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		String basics = "筛查指标,"+first.getText()+","+score3.getText()+","+score2.getText()+","+score1.getText()+","+","+"\r\n";
		String contents = "指标名（分组名）,条件1,条件2,条件3,分数,单项结果\r\n";
		String fileName = "export"+sdf.format(date)+"_"+DbConnect.rate_recall+"_"+DbConnect.rate_error+"_"+Config.model+".csv";
		
		for(int i=0;i < table.getRowCount();i++){
			String single = "";
			String tmp = "";
			for(int j=0;j < table.getColumnCount();j++){
				String value = "";
				if(table.getValueAt(i, j)!=null)
					value = table.getValueAt(i, j).toString();
				if(j+1 != table.getColumnCount()){
					single += value + ",";
				}else{
					single += value + "\r\n";
				}
				
			}
			contents += single;
		}
		
		//System.out.println(contents);
		string2File(basics+contents,fileName);
		
		return 0;
	}
	
	public String[] importConfig(){
		String fileName = "import.csv";
		String contents = Config.readToString(fileName);
		
		if(contents!=null&&contents.length()> 10){
			String[] strs = contents.split("\r\n");
			String[] tmp = strs[0].split(",");
			String[] conf = new String[tmp.length-1];
			for(int i=0;i < tmp.length-1;i++){
				conf[i] = tmp[i+1];
			}
			return conf;
		}
		
		
		return null;
	}
	
	public String[][] importModel(){
		String fileName = "import.csv";
		
		String contents = Config.readToString(fileName);
//		System.out.println(contents);
		
		// 转为二维字符数组
		if(contents!=null&&contents.length()> 10){
			String[] strs = contents.split("\r\n");
			String[] title = strs[1].split(",");
			
			String[][] arr = new String[strs.length-2][title.length];
			for(int i=0;i < strs.length-2;i++){
				
				String[] tmp = strs[i+2].split(",");
				for(int j=0;j < tmp.length;j++){
						arr[i][j] = tmp[j];
				}
			}
			
			for(int i=0;i < arr.length;i++)
				for(int j=0;j < title.length;j++){
					if(arr[i][j]==null)
						arr[i][j] = "";
				}
			
			return arr;
		}
		
		return null;
	}
	
	public static boolean string2File(String res, String filePath) { 
        boolean flag = true; 
        BufferedReader bufferedReader = null; 
        BufferedWriter bufferedWriter = null; 
        try { 
                File distFile = new File(filePath); 
                //if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs();
                if(distFile.exists())	
                	distFile.delete();
                distFile.createNewFile();
                bufferedReader = new BufferedReader(new StringReader(res)); 
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(distFile),"GBk")); 
                char buf[] = new char[1024];         //字符缓冲区 
                int len; 
                while ((len = bufferedReader.read(buf)) != -1) { 
                        bufferedWriter.write(buf, 0, len); 
                } 
                bufferedWriter.flush(); 
                bufferedReader.close(); 
                bufferedWriter.close(); 
        } catch (IOException e) { 
                e.printStackTrace(); 
                flag = false; 
                return flag; 
        } finally { 
                if (bufferedReader != null) { 
                        try { 
                                bufferedReader.close(); 
                        } catch (IOException e) { 
                                e.printStackTrace(); 
                        } 
                } 
        } 
        return flag; 
	}
	
}
