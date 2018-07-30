package utility;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.math.BigDecimal;



public class utility {
	
	
//	public static void plot(String user){
//		ArrayList<SimpleEntry<String,String>> checkins = userCheckinList.get(user);
//		double[][] xy = new double[checkins.size()][2];
//		for(int i = 0 ; i< checkins.size() ;i++){
//			SimpleEntry<geoCordinates,String[]> venueDetail = venue_info.get(checkins.get(i).getKey());
//			//System.out.println(venueDetail.getKey().lat+" "+venueDetail.getKey().lon);
//			xy[i][0] = venueDetail.getKey().lat;
//			xy[i][1] = venueDetail.getKey().lon;
//		}
//		Contour.plot(xy);
//	}
	
	


	// distance in km
	public static double computeDistance(double lat1, double lat2, double lon1, double lon2) {
		
		double dlong = lon1 - lon2;
   		double dlat =  lat1 - lat2;
   		double a = Math.pow(Math.sin(dlat/2), 2) + Math.cos(lat2) * Math.cos(lat1) * Math.pow(Math.sin(dlong/2), 2);
   		double c = 2.0 * Math.atan(Math.sqrt(a)*Math.sqrt(1-a));
   		double d = 6371.0 * c;
   		return d;
	
}
	
	public static double[][] get_TPR_FPR( Map<String, int[]> computed_users, Set<String> active_users) {
		double[][] TPR_FPR = new double[2000][2];
		List<Set<String>> computed_active_users = new ArrayList<Set<String>>();
		for(int i = 0;i<2000;i++){
			Set<String> element = new HashSet<String>();
			computed_active_users.add(i, element);
		}
		for(Entry<String,int[]> e : computed_users.entrySet()){
			int[] tmp = e.getValue();
			for(int i = 0;i<tmp.length;i++){
				if(tmp[i]==1)
					computed_active_users.get(i).add(e.getKey());
			}
		}
		int index = 0;
		for(Set<String> active_user_set : computed_active_users){
			double TPR = 0.0;
			for(String s : active_user_set)
				if(active_users.contains(s))
					TPR = TPR + 1;
			double FPR = (double)(active_user_set.size() - TPR);
			TPR = TPR/(double)active_users.size(); 
			FPR = FPR/(double)(computed_users.size()-active_users.size());	
			TPR_FPR[index][0] = TPR;
			TPR_FPR[index][1] = FPR;
			index++;
		}
		return TPR_FPR;
	}
	
	public static double[][] get_F_measure( Map<String, int[]> computed_users, Set<String> active_users) {
		
		double[][] precision_recall = new double[2000][2];
		List<Set<String>> computed_active_users = new ArrayList<Set<String>>();
		for(int i = 0;i<2000;i++){
			Set<String> element = new HashSet<String>();
			computed_active_users.add(i, element);
		}
		for(Entry<String,int[]> e : computed_users.entrySet()){
			int[] tmp = e.getValue();
			for(int i = 0;i<tmp.length;i++){
				if(tmp[i]==1)
					computed_active_users.get(i).add(e.getKey());
			}
		}
		int index = 0;
		for(Set<String> active_user_set : computed_active_users){
			double TP = 0.0;
			for(String s : active_user_set)
				if(active_users.contains(s))
					TP = TP + 1;
			
			double recall = TP/(double)active_users.size(); 
			double precision = TP/(double)(computed_users.size());	
			precision_recall[index][0] = precision;
			precision_recall[index][1] = recall;
			index++;
		}
		
		
		return precision_recall;
	}
	
	public static double truncateTo(long unroundedNumber) {
		int decimalPlaces = 2;
		 int truncatedNumberInt = (int)( unroundedNumber * Math.pow( 10, decimalPlaces ) );
		    double truncatedNumber = (double)( truncatedNumberInt / Math.pow( 10, decimalPlaces ) );
		    return truncatedNumber;
	}
	
	public static double truncateTo(double unroundedNumber, int decimalPlaces) {
		
		 int truncatedNumberInt = (int)( unroundedNumber * Math.pow( 10, decimalPlaces ) );
		    double truncatedNumber = (double)( truncatedNumberInt / Math.pow( 10, decimalPlaces ) );
		    return truncatedNumber;
	}

	public static int[] active_at_Thresholds (double p){
		int[] res = new int[2000];
		double start = 0.0;
		for(int i =0;i<2000;i++){
			if(p > start )
				res[i] = 1;
			start = start + 0.0005;
		}

//		System.out.print("result : ");
//		for(int i = 0;i<res.length;i++)
//			System.out.print(" i :"+i+" r : "+res[i]);
//		System.out.println();
		
		return res;
	}
	
	public double getRecall(List<String> computedSeedUsers, List<String> gt) {
		double match = 0.0;
		if(computedSeedUsers != null && gt != null)
		{
			for(int i = 0; i<gt.size();i++)
				for(int j = 0; j<computedSeedUsers.size();j++)
					if(gt.get(i).equals(computedSeedUsers.get(j)))
						match++;
		}
		return (match/(double)(gt.size()));
	}

	public double getAvgRank(List<String> computedSeedUsers, List<String> gt) {
		double avgRank = 0.0;
		for(int i = 0; i < computedSeedUsers.size();i++){
			for(int j = 0; j<gt.size();j++){
				if(gt.get(j).equals(computedSeedUsers.get(i)))
					avgRank = avgRank + i;
			}
		}
		return (avgRank/(double)gt.size());
	}
	
	// get k used in ground truth set
		public int getK(Map<String, List<String>> gt) {
			for(Entry<String,List<String>> e : gt.entrySet())
				return e.getValue().size();
			return 0;
		}
		
		public List<List<Double>> instantiateList(List<List<Double>> list){
			for(int i=0;i<10;i++){
				list.add(new ArrayList<Double>());
			}
			return list;
		}
		
		public List<Map<String, Set<String>>> instantiateSpread(List<Map<String, Set<String>>> seedUserAndSpread) {
			for(int i=0;i<30;i++){
				seedUserAndSpread.add(new HashMap<String,Set<String>> ());
			}
				return seedUserAndSpread;
			}
		
//		public List<Map<String, Set<String>>> computeSpread(double prob, List<Map<String, Set<String>>> spread, String individual, String seedUser) {
//			
//			double threshold = 0.0;
//			for(int i=0;i<10;i++){
//					//System.out.println(threshold+" "+prob+" "+seedUser+" -> "+individual);
//						if(spread.get(i).containsKey(seedUser)){
//							if(prob > threshold)
//								spread.get(i).get(seedUser).add(individual);
//						}else{
//							spread.get(i).put(seedUser, new HashSet<String>());
//							if(prob > threshold){
//								spread.get(i).get(seedUser).add(individual);
//								//System.out.println(spread);
//							}
//						}
//					
//					threshold = threshold + 0.1;
//			}
//			
//			//System.out.println("spread : "+spread);
//			return spread;
//		}

		public List<Map<String, Set<String>>> computeSpread(List<Map<String, Set<String>>> spread, List<SimpleEntry<String, Double>> friendsProb,
				String seedUser, double sum) {
			
			for(SimpleEntry<String, Double> e : friendsProb){
				
				double prob = 0.0;
				
				if(Double.isNaN(e.getValue())){
					prob = 0.0;
				}
				else {
					if(sum != 0.0 )
						prob = e.getValue()/sum;
					else
						prob = 0.0;
				}
				
				BigDecimal threshold = new BigDecimal("0.0");
				for(int i = 0; i< 28;i++){
					
					//System.out.println(threshold.doubleValue());
					if(spread.get(i).containsKey(seedUser)){
						if(prob >= threshold.doubleValue())
							spread.get(i).get(seedUser).add(e.getKey());
					}else{
						spread.get(i).put(seedUser, new HashSet<String>());
						if(prob >= threshold.doubleValue())
							spread.get(i).get(seedUser).add(e.getKey());
					}
					if(threshold.doubleValue() < 0.01){
						threshold = threshold.add(new BigDecimal("0.001"));
						//System.out.println("add : "+threshold);
					}
					else if(threshold.doubleValue() >= 0.01 && threshold.doubleValue() < 0.1){
						threshold = threshold.add(new BigDecimal("0.01"));
					}
					else{
						threshold = threshold.add(new BigDecimal("0.1"));
					}
				}
				
				
					
			}
			
			return spread;
		}
		
		public List<Map<String, Set<String>>> computeSpreadPercentage(List<Map<String, Set<String>>> spread, List<SimpleEntry<String, Double>> friendsProb,
				String seedUser, double sum) {
			
			//System.out.println("seedUser :"+seedUser);
		List<SimpleEntry<String, Double>> normalizedProbs = new ArrayList<SimpleEntry<String,Double>>(); 
			for(SimpleEntry<String, Double> e : friendsProb){
				
				double prob = 0.0;
				
				if(Double.isNaN(e.getValue())){
					prob = 0.0;
				}
				else {
					if(sum != 0.0 )
						prob = e.getValue()/sum;
					else
						prob = 0.0;
				}
				
				
				normalizedProbs.add(new SimpleEntry<String,Double>(e.getKey(),prob));
			}
			
			
			Collections.sort(normalizedProbs,new scoreComparator());
			//System.out.println(normalizedProbs);
			//System.out.println("before : "+normalizedProbs.size());
			
				double percentage = 95.0;
				for(int i = 0; i< 5;i++){
					
					double size = Math.round((percentage * normalizedProbs.size())/100.0);
					//System.out.println("after : "+size);
					for(int j = 0;j< size ;j++){
						
						if(spread.get(i).containsKey(seedUser)){
							spread.get(i).get(seedUser).add(normalizedProbs.get(j).getKey());
						}else{
							spread.get(i).put(seedUser, new HashSet<String>());
							spread.get(i).get(seedUser).add(normalizedProbs.get(j).getKey());
						}
					}
					percentage = percentage - 5.0;
			}
			
			return spread;
		}

		public List<Date> getCheckInTimesList(String target_location, String user, Initialize init) throws ParseException {
			
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			List<Date> checkinTimes = new ArrayList<Date>();
			if(init.userCheckinList.get(user)!=null){
			for(SimpleEntry<String,String> checkin : init.userCheckinList.get(user)){
				if(checkin.getKey().equals(target_location)){
					Date date = formatter.parse(checkin.getValue());
					checkinTimes.add(date);
				}
			}
			}
			return checkinTimes;
		}

		public List<Map<String, Set<String>>> computeSpreadWithTime(double threshold, List<Map<String, Set<String>>> spread,
				List<SimpleEntry<String, Double>> friendsProb, List<Date> seedUser_check_in_times,
				Map<String,List<Date>> friends_check_in_times, String cuttOffDate, String seedUser, double sum) throws ParseException {
			
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date testdate = formatter.parse(cuttOffDate);
			
		
			//System.out.println("before : "+seedUser_check_in_times);
			
			//List<Date> seedUser_truncatedTime = truncateList(seedUser_check_in_times,testdate);
			
			//System.out.println("after : "+seedUser_truncatedTime);
			
			for(SimpleEntry<String, Double> e : friendsProb){
				double prob = 0.0;
				
				if(Double.isNaN(e.getValue())){
					prob = 0.0;
				}
				else {
					if(sum != 0.0 )
						prob = e.getValue()/sum;
					else
						prob = 0.0;
				}
				
				
				int time_window = 10;
				for(int i = 0; i < 9;i++){
					if(spread.get(i).containsKey(seedUser)){
						if(prob >= threshold && checkTimeWindow(seedUser_check_in_times, friends_check_in_times.get(e.getKey()), testdate, time_window)){
							spread.get(i).get(seedUser).add(e.getKey());
							
						}
					}else{
						spread.get(i).put(seedUser, new HashSet<String>());
						if(prob >= threshold && checkTimeWindow(seedUser_check_in_times, friends_check_in_times.get(e.getKey()), testdate, time_window)){
							spread.get(i).get(seedUser).add(e.getKey());
							
						}
					}
					time_window = time_window + 10;
				}
			}
			return spread;
		}
		
		
		public List<Map<String, Set<String>>> computeSpreadWithPercentageTime(List<Map<String, Set<String>>> spread,
				List<SimpleEntry<String, Double>> friendsProb, List<Date> seedUser_check_in_times,
				Map<String,List<Date>> friends_check_in_times, String cuttOffDate, String seedUser, double sum) throws ParseException {
			
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date testdate = formatter.parse(cuttOffDate);
			
			//System.out.println("seedUser :"+seedUser);
			List<SimpleEntry<String, Double>> normalizedProbs = new ArrayList<SimpleEntry<String,Double>>(); 
				for(SimpleEntry<String, Double> e : friendsProb){
					
					double prob = 0.0;
					
					if(Double.isNaN(e.getValue())){
						prob = 0.0;
					}
					else {
						if(sum != 0.0 )
							prob = e.getValue()/sum;
						else
							prob = 0.0;
					}
					
					
					normalizedProbs.add(new SimpleEntry<String,Double>(e.getKey(),prob));
				}
				
				
				Collections.sort(normalizedProbs,new scoreComparator());
				//System.out.println(normalizedProbs);
				//System.out.println("before : "+normalizedProbs.size());
				
					double percentage = 80.0;
					double size = Math.round((percentage * normalizedProbs.size())/100.0);
					
					int time_window = 10;
					for(int i = 0; i < 9;i++){
						
						for(int j = 0;j< size ;j++){
						
						if(spread.get(i).containsKey(seedUser)){
							if(checkTimeWindow(seedUser_check_in_times, friends_check_in_times.get(normalizedProbs.get(j).getKey()), testdate, time_window)){
								spread.get(i).get(seedUser).add(normalizedProbs.get(j).getKey());
							}
						}else{
							spread.get(i).put(seedUser, new HashSet<String>());
							if(checkTimeWindow(seedUser_check_in_times, friends_check_in_times.get(normalizedProbs.get(j).getKey()), testdate, time_window))
								spread.get(i).get(seedUser).add(normalizedProbs.get(j).getKey());
						
						}
					}
					time_window = time_window + 10;
			}
			return spread;
		}

		private List<Date> truncateList(List<Date> seedUser_check_in_times, Date testdate) {
			int i = 0;
			while(i < seedUser_check_in_times.size() && seedUser_check_in_times.get(i).getTime()<testdate.getTime())
				i++;
			
			return seedUser_check_in_times.subList(0, i-1);
		}

		private boolean checkTimeWindow(List<Date> seedUser_check_in_times, List<Date> friend_check_in_times, Date testdate, int time_window) 
		{
			for(Date d : seedUser_check_in_times){	
				for(Date f : friend_check_in_times){
					float days = ((f.getTime()-d.getTime()) / (1000*60*60*24));
					
					if(f.getTime() > d.getTime() && days < time_window){
						//System.out.println("days : "+days);
						return true;
					}
					}
			}
			return false;
		}
		
		
}
