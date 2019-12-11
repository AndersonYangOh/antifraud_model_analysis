package Models;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class MainApp {
	private DbConnect db;
	private int resProcess = -1;
	private String[] columnNames = {"指标名（分组名）","条件1","条件2","条件3","分数","单项结果"};
	
	private JFrame frame;
	private JTable table;
	private JScrollPane scrollPane;
	private JButton button;
	private JButton button_1;
	private JTextField firstValue;
	private JTextField score2;
	private JTextField score3;
	private JTextField score1;
	private JTextArea textArea;
	private JScrollPane scrollPane_1;
	
	private int rate_err;
	private int rate_recall;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					MainApp window = new MainApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApp() {
		Config.initConfit();
		db = new DbConnect();
		db.init();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		

		
		Object[][] data =
			{
						{"60秒通话占比", "", "", "PeriodCommRate>=0.9","5",""},
						{"60秒通话占比", "", "", "PeriodCommRate>=1","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"被叫次数", "", "", "CalledCnt<=5","1",""},
						{"被叫次数", "", "", "CalledCnt<=2","5",""},
						{"被叫次数", "", "", "CalledCnt<=0","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"重复通话比", "", "", "RepeatCommRate<=0.06","5",""},
						{"重复通话比", "", "", "RepeatCommRate<=0.02","7",""},
						{"重复通话比", "", "", "RepeatCommRate<=0.01","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"号码占比", "", "", "CallNoRate>=0.9","5",""},
						{"号码占比", "", "", "CallNoRate>=0.98","7",""},
						{"号码占比", "", "", "CallNoRate>=1","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"主叫次数", "", "", "CallCnt>=30","5",""},
						{"主叫次数", "", "", "CallCnt>=50","10",""},
						{"主叫次数", "", "", "CallCnt>=100","15",""},
						{"主叫次数", "", "", "CallCnt>=300","20",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"拨打省数", "", "", "CallProvCount>=4","5",""},
						{"拨打省数", "", "", "CallProvCount>=10","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"同省占比", "", "CallLostRate >= 0.2", "SameProvRate>=0.6","3",""},
						{"同省占比", "", "CallLostRate >= 0.2", "SameProvRate>=0.9","5",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"特殊地区", "", "", "prov_code=920","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
			};
		
		Object[][] data5 =
			{
						{"60秒通话占比", "", "", "PeriodCommRate>=0.9","5",""},
						{"60秒通话占比", "", "", "PeriodCommRate>=1","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"被叫次数", "", "", "CalledCnt<=5","1",""},
						{"被叫次数", "", "", "CalledCnt<=2","5",""},
						{"被叫次数", "", "", "CalledCnt<=0","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"重复通话比", "", "", "RepeatCommRate<=0.06","5",""},
						{"重复通话比", "", "", "RepeatCommRate<=0.02","7",""},
						{"重复通话比", "", "", "RepeatCommRate<=0.01","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"号码占比", "", "", "CallNoRate>=0.9","5",""},
						{"号码占比", "", "", "CallNoRate>=0.98","7",""},
						{"号码占比", "", "", "CallNoRate>=1","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"主叫次数", "", "", "CallCnt>=30","5",""},
						{"主叫次数", "", "", "CallCnt>=50","10",""},
						{"主叫次数", "", "", "CallCnt>=100","15",""},
						{"主叫次数", "", "", "CallCnt>=300","20",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"拨打省数", "", "", "CallProvCount>=4","5",""},
						{"拨打省数", "", "", "CallProvCount>=10","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"同省占比", "", "CallLostRate >= 0.2", "SameProvRate>=0.6","3",""},
						{"同省占比", "", "CallLostRate >= 0.2", "SameProvRate>=0.9","5",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"特殊地区", "", "", "prov_code=920","10",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
						{"", "", "", "","",""},
			};
		
		
		frame = new JFrame();
		frame.setTitle("单日指标演化分析");
		if(Config.model.equals(Config._5DAY))
			frame.setTitle("5日指标演化分析");
		frame.setBounds(100, 100, 1123, 526);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 883, 478);
		frame.getContentPane().add(scrollPane);
		
		table = new JTable(data,columnNames);
		if(Config.model.equals(Config._5DAY))
			table = new JTable(data,columnNames);
		scrollPane.setViewportView(table);
		table.setShowGrid(true);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		
		
		JPanel panel = new JPanel();
		panel.setBounds(910, 10, 187, 256);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("\u521D\u7B5B\u503C\uFF1A");
		lblNewLabel_1.setBounds(10, 8, 56, 15);
		panel.add(lblNewLabel_1);
		
		firstValue = new JTextField();
		firstValue.setBounds(78, 5, 99, 21);
		firstValue.setText("CallCnt>=25");
		panel.add(firstValue);
		firstValue.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("level3\uFF1A");
		lblNewLabel.setBounds(10, 34, 56, 15);
		panel.add(lblNewLabel);
		
		score3 = new JTextField();
		score3.setBounds(78, 31, 99, 21);
		score3.setText("30");
		panel.add(score3);
		score3.setColumns(10);
		
		JLabel lblLevel = new JLabel("level2\uFF1A");
		lblLevel.setBounds(10, 60, 56, 15);
		panel.add(lblLevel);
		
		score2 = new JTextField();
		score2.setBounds(78, 57, 99, 21);
		score2.setText("40");
		panel.add(score2);
		score2.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("level1\uFF1A");
		lblNewLabel_2.setBounds(10, 86, 56, 15);
		panel.add(lblNewLabel_2);
		
		score1 = new JTextField();
		score1.setBounds(78, 83, 99, 21);
		score1.setText("55");
		panel.add(score1);
		score1.setColumns(10);
		
		button = new JButton("\u5F00\u59CB\u5206\u6790");
		button.setBounds(40, 124, 99, 23);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(Config.model.equals(Config._1DAY))
					resProcess = db.doProcess(table,firstValue.getText(),score3.getText(),score2.getText(),score1.getText());
				if(Config.model.equals(Config._5DAY))
					resProcess = db.doProcess5(table,firstValue.getText(),score3.getText(),score2.getText(),score1.getText());
			}
		});

		panel.add(button);
		
		JButton button_2 = new JButton("\u7EDF\u8BA1\u7ED3\u679C");
		button_2.setBounds(40, 157, 99, 23);
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(resProcess ==0){
					String results = "";
					String results_removeVIR = "\r\n======以下为检出数据去掉虚商和不足11位的情况========\r\n";
					String results_total = "\r\n======以下为加上虚商和level为3的结果========\r\n";
					if(Config.model.equals(Config._1DAY)){
						results = db.doStatistic();
						results_removeVIR += db.doStatistic_removevirtual();
						results_total += db.doStatistic_total();
					}

					if(Config.model.equals(Config._5DAY)){
						results = db.doStatistic5();
						results_removeVIR += db.doStatistic5_removevirtual();
						results_total += db.doStatistic5_total();
					}
						
					
					textArea.setText(results+results_removeVIR+results_total);
				}
			}
		});
		panel.add(button_2);
		
		button_1 = new JButton("导入");
		button_1.setBounds(40, 190, 99, 23);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[][] contents = new FileHandler().importModel();
				if(contents!=null){
					table = new JTable(contents,columnNames);
					scrollPane.setViewportView(table);
					table.setShowGrid(true);
					table.setShowHorizontalLines(true);
					table.setShowVerticalLines(true);
					String[] config = new FileHandler().importConfig();
					firstValue.setText(config[0]);
					score3.setText(config[1]);
					score2.setText(config[2]);
					score1.setText(config[3]);
				}
				
			}
		});
		panel.add(button_1);
		
		JButton btnNewButton = new JButton("导出");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new FileHandler().export(table,firstValue,score3,score2,score1);
			}
		});
		btnNewButton.setBounds(40, 223, 99, 23);
		panel.add(btnNewButton);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(910, 276, 187, 187);
		frame.getContentPane().add(scrollPane_1);
		
		textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
		textArea.setColumns(5);
		textArea.setText("\u663E\u793A\u7EDF\u8BA1\u7ED3\u679C\u7684\u6570\u503C");
		textArea.setRows(5);
		textArea.setTabSize(30);
		textArea.setToolTipText("\u7528\u4E8E\u663E\u793A\u7EDF\u8BA1\u7ED3\u679C\u7684\u6570\u503C");
	}
}
