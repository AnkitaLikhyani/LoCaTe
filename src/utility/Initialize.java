package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


import java.util.AbstractMap.SimpleEntry;

public class Initialize {
	
	public HashMap<String, Map<String, ArrayList<SimpleEntry<String, String>>>> userCategoryCheckinList = new HashMap<String, Map<String, ArrayList<SimpleEntry<String, String>>>>();
    public HashMap<String, ArrayList<SimpleEntry<String,String>>> userCheckinList = new HashMap<String, ArrayList<SimpleEntry<String,String>>>();
    public HashMap<String, ArrayList<SimpleEntry<String,String>>> testCheckinList = new HashMap<String, ArrayList<SimpleEntry<String,String>>>();
    public HashMap<String, List<Entry<String, String>>> locationCheckinList = new HashMap<String, List<Entry<String,String>>>();
    public HashMap<String,SimpleEntry<geoCordinates,String[]>> venue_info = new HashMap<String,SimpleEntry<geoCordinates,String[]>>();
	public HashMap<String,String> user_info = new HashMap<String,String>();
	public HashMap<String, Set<String>> social_graph = new HashMap<String, Set<String>>(); 
	public HashMap<String,Map<String,int[]>> evaluation_results = new HashMap<String,Map<String,int[]>>();
	public HashMap<String,Map<String,List<double[]>>> all_users_avg_result = new HashMap<String,Map<String,List<double[]>>>();
	public double[][] avg_TPR_FPR = new double[2000][2];
	public double test_cases = 0.0;
	
	//for each userid we have list of checkins (a pair of venue id and timestamp)
	public void loadCheckins(String inputFile) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line = br.readLine();
		while((line = br.readLine())!=null){
			String[] strList;
			strList=line.trim().split(",");
			if(!userCheckinList.containsKey(strList[0]))
			{
				userCheckinList.put(strList[0], new ArrayList<SimpleEntry<String,String>>());
				SimpleEntry<String, String> pair = new SimpleEntry<String,String>(strList[1],strList[2]);
				userCheckinList.get(strList[0]).add(pair);
			}
			else
			{
				SimpleEntry<String, String> pair = new SimpleEntry<String,String>(strList[1],strList[2]);
				userCheckinList.get(strList[0]).add(pair);
			}
		}
	}
	
	
	
	//for each venueid we have a pair of geo-cordinates and categories
	public void loadVenueDetails(String inputFile) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		String line = br.readLine();
		while((line = br.readLine())!=null){
			String[] strList;
			strList=line.trim().split(",");
			if(!venue_info.containsKey(strList[0]))
			{
				geoCordinates cord = new geoCordinates(Double.parseDouble(strList[1]),Double.parseDouble(strList[2]));
				//if(strList[0].equals("l0"))
					//System.out.println(strList[0]+" "+cord.lat+" "+cord.lon);
				if(strList.length > 3 && strList[3].contains(";")){
					String[] categories = strList[3].trim().split(";");
					venue_info.put(strList[0], new SimpleEntry<geoCordinates,String[]>(cord,categories) );
				}
				else
					venue_info.put(strList[0], new SimpleEntry<geoCordinates,String[]>(cord, null) );
			}
		}
	}
	
	//for each userid we have its homecity location
	public void loadUserDetails(String inputFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		String line = br.readLine();
		while((line = br.readLine())!=null){
			if(line.contains(",")){
				String userId= line.substring(0, line.indexOf(",")); 
				user_info.put(userId, line.substring(line.indexOf(",")+1,line.length()));
			}
		}
	}
	
	//for each userid load social graph
	public void loadSocialGraph(String inputFile)throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		String line ="";
		while((line = br.readLine())!=null){
			String[] strList;
			strList=line.trim().split(",");
			if(social_graph.containsKey(strList[0]))
				social_graph.get(strList[0]).add(strList[1]);
			else{
				Set<String> tmp = new HashSet<String>();
				tmp.add(strList[1]);	
				social_graph.put(strList[0], tmp);
			}
		}
	}

	//for each userid we have list of test checkins (a pair of venue id and timestamp)
	
	public void loadTestCheckins(String inputFile) throws IOException{
		
			
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = br.readLine();
			while((line = br.readLine())!=null){
				String[] strList;
				strList=line.trim().split(",");
				if(!testCheckinList.containsKey(strList[0]))
				{
					testCheckinList.put(strList[0], new ArrayList<SimpleEntry<String,String>>());
					SimpleEntry<String, String> pair = new SimpleEntry<String,String>(strList[1],strList[2]);
					testCheckinList.get(strList[0]).add(pair);
				}
				else
				{
					SimpleEntry<String, String> pair = new SimpleEntry<String,String>(strList[1],strList[2]);
					testCheckinList.get(strList[0]).add(pair);
				}
			}
	}
	
	// load test set created for locate
	 public HashMap<String,Map<String,Set<String>>> loadTestSet(String inputFile) throws IOException{
	    	
	    	HashMap<String,Map<String,Set<String>>> testSet = new HashMap<String,Map<String,Set<String>>>();
			
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = br.readLine();
			while((line = br.readLine())!=null){
				String[] strList;
				strList=line.trim().split(",");
				if(testSet.containsKey(strList[0]))
				{
					Set<String> tmpSet = new HashSet<String>();
					String[] activeUsers = strList[3].split(";"); 
					for(int i = 0; i<activeUsers.length;i++)	
						tmpSet.add(activeUsers[i].substring(0,activeUsers[i].indexOf(":")));
					testSet.get(strList[0]).put(strList[1], tmpSet);
				}
				else
				{
					TreeMap<String,Set<String>> tmp = new TreeMap<String,Set<String>>();
					Set<String> tmpSet = new HashSet<String>();
					String[] activeUsers = strList[3].split(";"); 
					for(int i = 0; i<activeUsers.length;i++)	
						tmpSet.add(activeUsers[i].substring(0,activeUsers[i].indexOf(":")));
					tmp.put(strList[1],tmpSet );
					testSet.put(strList[0], tmp);
				}
				
			}
			return testSet;
	    }
	 
	// for each location we have list of check-ins (a pair of user id and
		// timestamp)
	public void loadPerLocationCheckins(String inputFile) throws IOException, ParseException {

			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] strList;
				strList = line.trim().split(",");
				
			
				if (!locationCheckinList.containsKey(strList[1])) {
					locationCheckinList.put(strList[1], new ArrayList<Entry<String,String>>());
					locationCheckinList.get(strList[1]).add(new SimpleEntry<String,String>(strList[0],strList[2]));
				} else {
					locationCheckinList.get(strList[1]).add(new SimpleEntry<String,String>(strList[0],strList[2]));
				}
			}
//			System.out.println("location checkin list size: "+locationCheckinList.size());
//			long sum = 0;
//			for(Entry<String, TreeMap<Integer, List<String>>> e : locationCheckinList.entrySet())
//				for(Entry<Integer, List<String>> e1 : e.getValue().entrySet())
//					sum = sum + e1.getValue().size();
//			System.out.println("total checkins : "+sum);
		}


		// load ground truth set created for IM
		public Map<String,List<String>> loadTestSetIM(String inputFile) throws IOException {
			Map<String,List<String>> gt = new HashMap<String,List<String>>();
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] strList;
				strList = line.trim().split(",");
				List<String> seedUsers = new ArrayList<String>();
				for(int i =1;i<strList.length;i++)
					seedUsers.add(strList[i]);
				gt.put(strList[0], seedUsers);
			}
			
			return gt;
		}
		
      public void loadCheckinsCatgeoryWise(Initialize init) throws IOException {
			
			for (Entry<String, ArrayList<SimpleEntry<String, String>>> e : init.userCheckinList.entrySet()) {
				userCategoryCheckinList.put(e.getKey(), new HashMap<String, ArrayList<SimpleEntry<String, String>>>());
				for (SimpleEntry<String, String> checkin : e.getValue()) {
					String[] categories = init.venue_info.get(checkin.getKey()).getValue();
					if(categories != null){
						for (int i = 0; i < categories.length; i++) {
							if (userCategoryCheckinList.get(e.getKey()).containsKey(categories[i]))
								userCategoryCheckinList.get(e.getKey()).get(categories[i]).add(checkin);
							else {
								userCategoryCheckinList.get(e.getKey()).put(categories[i],
									new ArrayList<SimpleEntry<String, String>>());
								userCategoryCheckinList.get(e.getKey()).get(categories[i]).add(checkin);
							}
						}
					}
				}
			}
		}
		
		
}
