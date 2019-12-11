package Models;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.JTable;


public class DbConnect {
	public Connection conn = null;
	
	public static int rate_recall;
	public static int rate_error;

	
	public Connection init(){
		

        try {
            // 1.加载数据访问驱动
			//Class.forName("com.mysql.jdbc.Driver");
			Class.forName("com.mysql.cj.jdbc.Driver");
	        //2.连接到数据"库"上去
			if(Config.jdbc!=null){
	        	conn= DriverManager.getConnection("jdbc:mysql://"+Config.jdbc+"?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai", Config.user, Config.pass);

	        }else{
	        	conn= DriverManager.getConnection("jdbc:mysql://localhost:3306/voice?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai", "root", "mysql11");
	        }
			
			//3.构建SQL命令
	        Statement state=conn.createStatement();
	        String s="select count(*) from agr20190216_5";
	        ResultSet rs = state.executeQuery(s);
	        rs.next();
	        System.out.println(rs.getLong("count(*)"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return conn;
	}
	
	public int doProcess(JTable table,String firstCheck,String score3,String score2,String score1){
		String sql = "";
		String sql_head ="insert into score20190216(call_number,imsi,imei,score)  select AAA.call_number,AAA.imsi,AAA.imei,AAA.city,AAA.prov,AAA.prov_code,AAA.area_id,  	current_timestamp(),  	'20190216',  	AAA.score_sum from  	(select call_number,imsi,imei,city,prov,prov_code,area_id,sum(score_single) as score_sum from (";
		String sql_tail =")AA  	group by call_number)AAA  ;";
		String score_index = "ALTER TABLE `score20190216` ADD INDEX index_score ( `score` );";
		String row_head = "select call_number,imsi,imei,city,prov,prov_code,area_id,max(score) as score_single from (";
		String row_tail = ")A group by call_number";
		
		String createScore1 = "DROP TABLE IF EXISTS `score20190216`;";
		String createScore2 = " CREATE TABLE `score20190216` (  `id` int NOT NULL auto_increment,  `call_number` bigint(11) NOT NULL DEFAULT 0, `imsi` bigint(15) DEFAULT 0 ,`imei` bigint(15) DEFAULT 0 ,`prov` smallint(3) DEFAULT -1  ,`city` smallint(3) DEFAULT -1  ,`prov_code` smallint(3) DEFAULT -1 ,`area_id` smallint(3) DEFAULT -1  ,`check_time` timestamp DEFAULT 0  ,`bill_time` timestamp DEFAULT 0 ,`score` smallint(1) DEFAULT -1 ,PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		try {
			Statement state=conn.createStatement();
			state.execute(createScore1);
			state.execute(createScore2);
			
			// 开始拼核心串
			String title = "";
			String single = "";
			Boolean isNewGroup = false;
//			ArrayList<String> conditions;
			for(int i=0;i < table.getRowCount();i++){
				//	判断是否为新分组
				if(i==0||(!title.equals(table.getValueAt(i, 0).toString()))){
					//single += row_head;
					isNewGroup = true;
				}else{
					isNewGroup = false;
				}
				title = table.getValueAt(i, 0).toString();
				
				String condition = "";
				for(int j=1;j <= 3;j++){
					if(table.getValueAt(i, j).toString()!=null&&table.getValueAt(i, j).toString().length()>1){
						if(condition.length()<=1){
							condition = table.getValueAt(i, j).toString();
						}else{
							condition += " and " + table.getValueAt(i, j).toString();
						}
						
					}
				}
				
				// 进行拼凑分组串
				if(condition.length()<=2)
					continue;
				
				if(i==0){
					single += row_head;
				}else if(isNewGroup){
					single += " union all " + row_head;
				}else{
					single += " union all ";
				}
				single += "select a.*," + table.getValueAt(i, 4)+" as score from agr20190216 a where " + firstCheck + " and " + condition;
				
				//  如果下个不是一个分组则收尾
				if(i+1 == table.getRowCount()||!title.equals(table.getValueAt(i+1, 0).toString())){
					single += row_tail;
				}
			}
			
			if(single.length() <= 20)
				return -1;
			
			// 拼完全串
			sql = sql_head + single + sql_tail;
	        System.out.println(sql);
	        state.executeUpdate(sql);
	        state.executeUpdate(score_index);
	        
//			ResultSet rs = state.executeQuery(sql);
//	        rs.next();
	        // 导入至模型结果
	        String createDetect1 = "DROP TABLE IF EXISTS `detectquick20190216`;";
	        String createDetect2 = "CREATE TABLE `detectquick20190216` (  `id` int NOT NULL auto_increment,  `call_number` bigint(11) NOT NULL DEFAULT 0 ,  `imsi` bigint(15) DEFAULT 0 ,  `imei` bigint(15) DEFAULT 0  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	        String level30 = score3;
	        String level20 = score2;
	        String level10 = score1;
	        String level31 = (Integer.parseInt(level20)-1)+"";
	        String level21 = (Integer.parseInt(level10)-1)+"";
	        String sql_detect = "insert into detectquick20190216(call_number,imsi,imei,acc_level) select call_number,imsi,imei,acc_level from 	(select call_number,imsi,imei,3 as acc_level from score20190216 where score between " + level30 + " and " + level31
	        					+ "	union all 	select call_number,imsi,imei,2 as acc_level from score20190216 where score between " + level20 + " and " + level21
	        					+ "	union all  	select call_number,imsi,imei,1 as acc_level from score20190216 where score >= " + level10 + ")A ;";
	        String index_detect = "ALTER TABLE `detectquick20190216` ADD INDEX index_number ( `call_number` );";
	        
	        System.out.println(sql_detect);
	        
	        state.executeUpdate(createDetect1);
	        state.executeUpdate(createDetect2);
	        state.executeUpdate(sql_detect);
	        state.executeUpdate(index_detect);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return 0;
	}
	
	public String doStatistic(){
		int totalCnt = 115014488;
		int TXDayCnt = 2112;
		int checkCnt = -1;
		int checkTXDayCnt = -1;
		int checkTXHistoryCnt = -1;
		
		String check_count = "select count(call_number) from detectquick20190216;";
		String check_TXDayCnt = "select count(*) from detectquick20190216 where call_number in( 	select distinct call_number from evil_0 where check_time = timestamp('2019-02-16') and type_code = 2 and acc_level in(1,2) );";
		String check_TXHistoryCnt = "select count(*) from detectquick20190216 where call_number in( 	select distinct call_number from evil_0 where type_code = 2 and acc_level in(1,2) );";
		
		
        Statement state;
		try {
			ResultSet rs;
			state = conn.createStatement();
	        rs = state.executeQuery(check_count);
	        rs.next();
	        checkCnt = rs.getInt("count(call_number)");
	        
	        rs = state.executeQuery(check_TXDayCnt);
	        rs.next();
	        checkTXDayCnt = rs.getInt("count(*)");
	        
	        rs = state.executeQuery(check_TXHistoryCnt);
	        rs.next();
	        checkTXHistoryCnt = rs.getInt("count(*)");
	        
	        String results = "总号码数：" + totalCnt + "\r\n"
	        				+"TX日检出数：" + TXDayCnt + "\r\n"
	        				+"模型检出数：" + checkCnt + "\r\n"
	        				+"模型对标TX日检出数：" + checkTXDayCnt +"\r\n"
	        				+"模型对标TX历史数据：" + checkTXHistoryCnt +"\r\n"
	        				+"覆盖率：" + 1.0f*checkTXDayCnt/TXDayCnt*100 +"%\r\n"
	        				+"误报率：" + (1.0f - 1.0f*checkTXHistoryCnt/checkCnt)*100 +"%\r\n";
	        System.out.println(results);
	        rate_recall = (int) (1.0f*checkTXDayCnt/TXDayCnt*100);
	        rate_error = (int) ((1.0f - 1.0f*checkTXHistoryCnt/checkCnt)*100);
	        
	        return results;
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return "========分析出错！========";
	}
	
	public String doStatistic_removevirtual(){
		int totalCnt = 115014488;
		int TXDayCnt = 2112;
		int checkCnt = -1;
		int checkTXDayCnt = -1;
		int checkTXHistoryCnt = -1;
		
		String check_count = "select count(call_number) from detectquick20190216 where call_number div 100000000 not in(170,171) and call_number div 10000000000 =1;";
		String check_TXDayCnt = "select count(*) from detectquick20190216 where call_number div 100000000 not in(170,171) and call_number div 10000000000 =1 and call_number in( 	select distinct call_number from evil_0 where check_time = timestamp('2019-02-16') and type_code = 2 and acc_level in(1,2) );";
		String check_TXHistoryCnt = "select count(*) from detectquick20190216 where call_number div 100000000 not in(170,171) and call_number div 10000000000 =1 and call_number in( 	select distinct call_number from evil_0 where type_code = 2 and acc_level in(1,2) );";
		
        Statement state;
		try {
			ResultSet rs;
			state = conn.createStatement();
	        rs = state.executeQuery(check_count);
	        rs.next();
	        checkCnt = rs.getInt("count(call_number)");
	        
	        rs = state.executeQuery(check_TXDayCnt);
	        rs.next();
	        checkTXDayCnt = rs.getInt("count(*)");
	        
	        rs = state.executeQuery(check_TXHistoryCnt);
	        rs.next();
	        checkTXHistoryCnt = rs.getInt("count(*)");
	        
	        String results = //"总号码数：" + totalCnt + "\r\n"
	        				//+"TX日检出数：" + TXDayCnt + "\r\n"
	        				"模型检出数：" + checkCnt + "\r\n"
	        				+"模型对标TX日检出数：" + checkTXDayCnt +"\r\n"
	        				+"模型对标TX历史数据：" + checkTXHistoryCnt +"\r\n"
	        				+"覆盖率：" + 1.0f*checkTXDayCnt/TXDayCnt*100 +"%\r\n"
	        				+"误报率：" + (1.0f - 1.0f*checkTXHistoryCnt/checkCnt)*100 +"%\r\n";
	        System.out.println(results);
	        
	        return results;
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return "========分析出错！========";
	}
	
	public String doStatistic_total(){
		int totalCnt = 115014488;
		int TXDayCnt = 4716;	//4716/4753
		int checkCnt = -1;
		int checkTXDayCnt = -1;
		int checkTXHistoryCnt = -1;
		
		String check_count = "select count(call_number) from detectquick20190216;";
		String check_TXDayCnt = "select count(*) from detectquick20190216 where call_number in( 	select distinct call_number from evil_0 where check_time = timestamp('2019-02-16') and type_code = 2 );";
		String check_TXHistoryCnt = "select count(*) from detectquick20190216 where call_number in( 	select distinct call_number from evil_0 where type_code = 2 );";
		
		
        Statement state;
		try {
			ResultSet rs;
			state = conn.createStatement();
	        rs = state.executeQuery(check_count);
	        rs.next();
	        checkCnt = rs.getInt("count(call_number)");
	        
	        rs = state.executeQuery(check_TXDayCnt);
	        rs.next();
	        checkTXDayCnt = rs.getInt("count(*)");
	        
	        rs = state.executeQuery(check_TXHistoryCnt);
	        rs.next();
	        checkTXHistoryCnt = rs.getInt("count(*)");
	        
	        String results = //"总号码数：" + totalCnt + "\r\n"
	        				"TX日检出数：" + TXDayCnt + "\r\n"
	        				//+"模型检出数：" + checkCnt + "\r\n"
	        				+"模型对标TX日检出数：" + checkTXDayCnt +"\r\n"
	        				+"模型对标TX历史数据：" + checkTXHistoryCnt +"\r\n"
	        				+"覆盖率：" + 1.0f*checkTXDayCnt/TXDayCnt*100 +"%\r\n"
	        				+"误报率：" + (1.0f - 1.0f*checkTXHistoryCnt/checkCnt)*100 +"%\r\n";
	        System.out.println(results);
	        
	        return results;
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return "========分析出错！========";
	}
	
	public int doProcess5(JTable table,String firstCheck,String score3,String score2,String score1){
		String sql = "";
		String sql_head ="insert into score20190220_5(call_number,imsi,imei,score)  select AAA.call_number,AAA.imsi,AAA.imei,AAA.city,AAA.prov,AAA.prov_code,AAA.area_id,  	current_timestamp(),  	'20190220',  	AAA.score_sum from  	(select call_number,imsi,imei,city,prov,prov_code,area_id,sum(score_single) as score_sum from (";
		String sql_tail =")AA  	group by call_number)AAA  ;";
		String score_index = "ALTER TABLE `score20190220_5` ADD INDEX index_score ( `score` );";
		String row_head = "select call_number,imsi,imei,city,prov,prov_code,area_id,max(score) as score_single from (";
		String row_tail = ")A group by call_number";
		
		String createScore1 = "DROP TABLE IF EXISTS `score20190220_5`;";
		String createScore2 = " CREATE TABLE `score20190220_5` (  `id` int NOT NULL auto_increment,  `call_number` bigint(11) NOT NULL DEFAULT 0 , `imsi` bigint(15) DEFAULT 0 ,`imei` bigint(15) DEFAULT 0 ,`score` smallint(1) DEFAULT -1,PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		try {
			Statement state=conn.createStatement();
			state.execute(createScore1);
			state.execute(createScore2);
			
			// 开始拼核心串
			String title = "";
			String single = "";
			Boolean isNewGroup = false;
//			ArrayList<String> conditions;
			for(int i=0;i < table.getRowCount();i++){
				//	判断是否为新分组
				if(i==0||(!title.equals(table.getValueAt(i, 0).toString()))){
					//single += row_head;
					isNewGroup = true;
				}else{
					isNewGroup = false;
				}
				title = table.getValueAt(i, 0).toString();
				
				String condition = "";
				for(int j=1;j <= 3;j++){
					if(table.getValueAt(i, j).toString()!=null&&table.getValueAt(i, j).toString().length()>1){
						if(condition.length()<=1){
							condition = table.getValueAt(i, j).toString();
						}else{
							condition += " and " + table.getValueAt(i, j).toString();
						}
						
					}
				}
				
				// 进行拼凑分组串
				if(condition.length()<=2)
					continue;
				
				if(i==0){
					single += row_head;
				}else if(isNewGroup){
					single += " union all " + row_head;
				}else{
					single += " union all ";
				}
				single += "select a.*," + table.getValueAt(i, 4)+" as score from agr20190216_5 a where " + firstCheck + " and " + condition;
				
				//  如果下个不是一个分组则收尾
				if(i+1 == table.getRowCount()||!title.equals(table.getValueAt(i+1, 0).toString())){
					single += row_tail;
				}
			}
			
			if(single.length() <= 20)
				return -1;
			
			// 拼完全串
			sql = sql_head + single + sql_tail;
	        System.out.println(sql);
	        state.executeUpdate(sql);
	        state.executeUpdate(score_index);
	        
//			ResultSet rs = state.executeQuery(sql);
//	        rs.next();
	        // 导入至模型结果
	        String createDetect1 = "DROP TABLE IF EXISTS `detectquick20190220_5`;";
	        String createDetect2 = "CREATE TABLE `detectquick20190220_5` (  `id` int NOT NULL auto_increment,  `call_number` bigint(11) NOT NULL DEFAULT 0,  `imsi` bigint(15) DEFAULT 0,  `imei` bigint(15) DEFAULT 0 comment ,  `acc_level` tinyint(1) DEFAULT -1 comment '检出准确度：1、完全确定2、高危3、疑似',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	        String level30 = score3;
	        String level20 = score2;
	        String level10 = score1;
	        String level31 = (Integer.parseInt(level20)-1)+"";
	        String level21 = (Integer.parseInt(level10)-1)+"";
	        String sql_detect = "insert into detectquick20190220_5(call_number,imsi,imei,acc_level) select call_number,imsi,imei,acc_level from 	(select call_number,imsi,imei,3 as acc_level from score20190220_5 where score between " + level30 + " and " + level31
	        					+ "	union all 	select call_number,imsi,imei,2 as acc_level from score20190220_5 where score between " + level20 + " and " + level21
	        					+ "	union all  	select call_number,imsi,imei,1 as acc_level from score20190220_5 where score >= " + level10 + ")A ;";
	        String index_detect = "ALTER TABLE `detectquick20190220_5` ADD INDEX index_number ( `call_number` );";
	        
	        System.out.println(sql_detect);
	        
	        state.executeUpdate(createDetect1);
	        state.executeUpdate(createDetect2);
	        state.executeUpdate(sql_detect);
	        state.executeUpdate(index_detect);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return 0;
	}
	

	public String doStatistic5(){
		int totalCnt = 122717201;
		int TXDayCnt = 3940;
		int checkCnt = -1;
		int checkTXDayCnt = -1;
		int checkTXHistoryCnt = -1;
		
		String check_count = "select count(call_number) from detectquick20190220_5 where call_number div 10000000000 =1;";
		String check_TXDayCnt = "select count(*) from detectquick20190220_5 where call_number div 10000000000 =1 and call_number in( 	select distinct call_number from evil_0 where check_time = timestamp('2019-02-20') and type_code = 2 and acc_level in(1,2) );";
		String check_TXHistoryCnt = "select count(*) from detectquick20190220_5 where call_number div 10000000000 =1 and call_number in( 	select distinct call_number from evil_0 where type_code = 2 and acc_level in(1,2) );";
		
		
        Statement state;
		try {
			ResultSet rs;
			state = conn.createStatement();
	        rs = state.executeQuery(check_count);
	        rs.next();
	        checkCnt = rs.getInt("count(call_number)");
	        
	        rs = state.executeQuery(check_TXDayCnt);
	        rs.next();
	        checkTXDayCnt = rs.getInt("count(*)");
	        
	        rs = state.executeQuery(check_TXHistoryCnt);
	        rs.next();
	        checkTXHistoryCnt = rs.getInt("count(*)");
	        
	        String results = "总号码数：" + totalCnt + "\r\n"
	        				+"TX日检出数：" + TXDayCnt + "\r\n"
	        				+"模型检出数：" + checkCnt + "\r\n"
	        				+"模型对标TX日检出数：" + checkTXDayCnt +"\r\n"
	        				+"模型对标TX历史数据：" + checkTXHistoryCnt +"\r\n"
	        				+"覆盖率：" + 1.0f*checkTXDayCnt/TXDayCnt*100 +"%\r\n"
	        				+"误报率：" + (1.0f - 1.0f*checkTXHistoryCnt/checkCnt)*100 +"%\r\n";
	        System.out.println(results);
	        
	        rate_recall = (int) (1.0f*checkTXDayCnt/TXDayCnt*100);
	        rate_error = (int) ((1.0f - 1.0f*checkTXHistoryCnt/checkCnt)*100);
	        
	        return results;
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return "========分析出错！========";
	}
	
	public String doStatistic5_removevirtual(){
		int totalCnt = 122717201;
		int TXDayCnt = 3940;
		int checkCnt = -1;
		int checkTXDayCnt = -1;
		int checkTXHistoryCnt = -1;
		
		String check_count = "select count(call_number) from detectquick20190220_5 where call_number div 100000000 not in(170,171) and call_number div 10000000000 =1;";
		String check_TXDayCnt = "select count(*) from detectquick20190220_5 where call_number div 100000000 not in(170,171) and call_number div 10000000000 =1 and call_number in( 	select distinct call_number from evil_0 where check_time = timestamp('2019-02-20') and type_code = 2 and acc_level in(1,2) );";
		String check_TXHistoryCnt = "select count(*) from detectquick20190220_5 where call_number div 100000000 not in(170,171) and call_number div 10000000000 =1 and call_number in( 	select distinct call_number from evil_0 where type_code = 2 and acc_level in(1,2) );";
		
		
        Statement state;
		try {
			ResultSet rs;
			state = conn.createStatement();
	        rs = state.executeQuery(check_count);
	        rs.next();
	        checkCnt = rs.getInt("count(call_number)");
	        
	        rs = state.executeQuery(check_TXDayCnt);
	        rs.next();
	        checkTXDayCnt = rs.getInt("count(*)");
	        
	        rs = state.executeQuery(check_TXHistoryCnt);
	        rs.next();
	        checkTXHistoryCnt = rs.getInt("count(*)");
	        
	        String results = //"总号码数：" + totalCnt + "\r\n"
	        				//+"TX日检出数：" + TXDayCnt + "\r\n"
	        				"模型检出数：" + checkCnt + "\r\n"
	        				+"模型对标TX日检出数：" + checkTXDayCnt +"\r\n"
	        				+"模型对标TX历史数据：" + checkTXHistoryCnt +"\r\n"
	        				+"覆盖率：" + 1.0f*checkTXDayCnt/TXDayCnt*100 +"%\r\n"
	        				+"误报率：" + (1.0f - 1.0f*checkTXHistoryCnt/checkCnt)*100 +"%\r\n";
	        System.out.println(results);
	        
	        return results;
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return "========分析出错！========";
	}
	
	public String doStatistic5_total(){
		int totalCnt = 122717201;
		int TXDayCnt = 8654;		//8678/8678
		int checkCnt = -1;
		int checkTXDayCnt = -1;
		int checkTXHistoryCnt = -1;
		
		String check_count = "select count(call_number) from detectquick20190220_5 where call_number div 10000000000 =1;";
		String check_TXDayCnt = "select count(*) from detectquick20190220_5 where call_number div 10000000000 =1 and call_number in( 	select distinct call_number from evil_0 where check_time = timestamp('2019-02-20') and type_code = 2 );";
		String check_TXHistoryCnt = "select count(*) from detectquick20190220_5 where call_number div 10000000000 =1 and call_number in( 	select distinct call_number from evil_0 where type_code = 2 );";
		
		
        Statement state;
		try {
			ResultSet rs;
			state = conn.createStatement();
	        rs = state.executeQuery(check_count);
	        rs.next();
	        checkCnt = rs.getInt("count(call_number)");
	        
	        rs = state.executeQuery(check_TXDayCnt);
	        rs.next();
	        checkTXDayCnt = rs.getInt("count(*)");
	        
	        rs = state.executeQuery(check_TXHistoryCnt);
	        rs.next();
	        checkTXHistoryCnt = rs.getInt("count(*)");
	        
	        String results =//"总号码数：" + totalCnt + "\r\n"
	        				"TX日检出数：" + TXDayCnt + "\r\n"
	        				//+"模型检出数：" + checkCnt + "\r\n"
	        				+"模型对标TX日检出数：" + checkTXDayCnt +"\r\n"
	        				+"模型对标TX历史数据：" + checkTXHistoryCnt +"\r\n"
	        				+"覆盖率：" + 1.0f*checkTXDayCnt/TXDayCnt*100 +"%\r\n"
	        				+"误报率：" + (1.0f - 1.0f*checkTXHistoryCnt/checkCnt)*100 +"%\r\n";
	        System.out.println(results);
	        
	        return results;
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return "========分析出错！========";
	}
}
