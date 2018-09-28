package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import handler.App;

public class Catalog {
	//	public static final String DATA_PATH = "samples/input/db/data/";
	//    public static final String SCHEMA_PATH = "samples/input/db/schema.txt";
	//    public static final String OUTPUT_PATH = "samples/output/";
	//    public static final String SQLQURIES_PATH = "samples/input/queries.sql";
	private static Catalog instance = null;
	private String inputPath;
	private String outputPath;
	private String dataSchemaFile;
	private String dataDir;
	private String sqlPath;
	private Map<String,String> aliaMap = new HashMap<String,String>();
	private Map<String,String> dataStore = new HashMap<String,String>();
	private Map<String,Integer> currentSchema = new HashMap<String,Integer>();
	//table and its fields
	private Map<String,Map<String,Integer>> schemas = new HashMap<String,Map<String,Integer>>();

	private Catalog(String inputPath,String outputPath) {
		setInputPath(inputPath);
		setOutputPath(outputPath);
		this.sqlPath = this.getInputPath()+"/queries.sql";
		this.dataSchemaFile = this.getInputPath()+"/db/schema.txt";
		this.dataDir = this.getInputPath()+"/db/data";
		BufferedReader br = null;
		try {
			File schemaFile = new File(this.dataSchemaFile);
			Reader schemaReader;
			schemaReader = new FileReader(schemaFile);
			br = new BufferedReader(schemaReader);
			String s = null;
			while((s = br.readLine())!=null) {
				String[] table = s.split("\\s+");
				//			table stored map
				dataStore.put(table[0],dataDir+"/"+table[0]);
				Map<String,Integer> schema = new HashMap<String,Integer>();
				for(int i = 1;i<table.length;i++) {
					String tableName = table[0];
					schema.put(tableName+"."+table[i], i-1);
				}
				schemas.put(table[0], schema);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	//    singleton instance
	public static synchronized Catalog getInstance(String inputPath,String outputPath) {
		if(instance == null) {
			instance = new Catalog(inputPath,outputPath);
		}
		return instance;
	}

	public void setAliaMap(String[] aliaStr) {
		//		key is alias name
		this.aliaMap.put(aliaStr[2], aliaStr[0]);
	}

	public void setCurSchema(String alias) {
		Map<String, Integer> tempSchema = schemas.get(aliaMap.get(alias));
		currentSchema = new HashMap<>();
		for (Map.Entry<String, Integer> entry : tempSchema.entrySet()) {
			String newKey = alias + "." + entry.getKey().split("\\.")[1];
			currentSchema.put(newKey, entry.getValue());
		}
	}
	public void iniCurSchema(String tableName) {
		this.currentSchema = schemas.get(tableName);
	}
	
	public Map<String, Integer> getCurSchema(){
		return this.currentSchema;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getDataStoredPath(String table) {
		return dataStore.get(table);
	}

	public Map<String, Integer> getTableSchema(String table) {
		return schemas.get(table);
	} 
	public String getSqlPath() {
		return this.sqlPath;
	}
	//	public void setConstant() {
	//		
	//	}
	public static void main(String[] args) {
		Catalog catalog = Catalog.getInstance("samples/input","samples/output");
		System.out.println(catalog);
		Catalog catalog2 = Catalog.getInstance("samples/input","samples/output");
		System.out.println(catalog2);
		String[] argsArray = {"samples/input","samples/output"};
		App.main(argsArray);
		System.out.println(catalog);
		System.out.println(catalog2);
		System.out.println(catalog.getDataStoredPath("Sailors"));
		System.out.println(catalog.schemas);
		System.out.println(catalog.getSqlPath());
		System.out.println(catalog.getOutputPath());
	}
}
